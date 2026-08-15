package com.example.ui

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.SystemClock
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CrosshairConfig
import com.example.data.model.CrosshairStyle
import com.example.data.model.HudSettings
import com.example.data.model.ModCategory
import com.example.data.model.ModPackEntity
import com.example.data.model.PerformanceProfile
import com.example.data.model.ServerEntity
import com.example.data.repository.ClientRepository
import com.example.ui.theme.AppAccentTheme
import com.example.util.HapticHelper
import com.example.util.MinecraftLauncher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random

enum class NavTab(val title: String, val icon: String) {
    HOME("Home", "Home"),
    MODS("Mods", "Extension"),
    PVP("PvP Suite", "SportsEsports"),
    SERVERS("Servers", "Dns"),
    SETTINGS("Settings", "Settings")
}

data class SystemStats(
    val fps: Int = 118,
    val targetFps: Int = 120,
    val pingMs: Int = 32,
    val ramUsedMb: Int = 1840,
    val ramTotalMb: Int = 6144,
    val isMinecraftInstalled: Boolean = false,
    val activeProfileName: String = "Ultra FPS"
)

data class CpsTestState(
    val currentCps: Float = 0f,
    val peakCps: Float = 0f,
    val totalClicks: Int = 0,
    val isChallengeActive: Boolean = false,
    val challengeDurationSec: Int = 5,
    val timeRemainingSec: Float = 5f,
    val lastScore: Float? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ClientRepository(application)
    val haptics = HapticHelper(application)

    private val _currentTab = MutableStateFlow(NavTab.HOME)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    val currentProfile: StateFlow<PerformanceProfile> = repository.currentProfile
    val hudSettings: StateFlow<HudSettings> = repository.hudSettings
    val accentTheme: StateFlow<AppAccentTheme> = repository.accentTheme
    val hapticsEnabled: StateFlow<Boolean> = repository.hapticsEnabled
    val soundEffectsEnabled: StateFlow<Boolean> = repository.soundEffectsEnabled

    val allServers: StateFlow<List<ServerEntity>> = repository.allServers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allModPacks: StateFlow<List<ModPackEntity>> = repository.allModPacks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedModCategory = MutableStateFlow(ModCategory.ALL)
    val selectedModCategory: StateFlow<ModCategory> = _selectedModCategory.asStateFlow()

    private val _modSearchQuery = MutableStateFlow("")
    val modSearchQuery: StateFlow<String> = _modSearchQuery.asStateFlow()

    val filteredModPacks = combine(allModPacks, _selectedModCategory, _modSearchQuery) { packs, category, query ->
        packs.filter { pack ->
            (category == ModCategory.ALL || pack.category == category) &&
                    (query.isBlank() || pack.title.contains(query, ignoreCase = true) || pack.description.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Telemetry and System Stats
    private val _systemStats = MutableStateFlow(SystemStats())
    val systemStats: StateFlow<SystemStats> = _systemStats.asStateFlow()

    // CPS Clicker State
    private val clickTimestamps = mutableListOf<Long>()
    private val _cpsState = MutableStateFlow(CpsTestState())
    val cpsState: StateFlow<CpsTestState> = _cpsState.asStateFlow()

    // Combo Counter State
    private val _comboCount = MutableStateFlow(0)
    val comboCount: StateFlow<Int> = _comboCount.asStateFlow()
    private val _maxCombo = MutableStateFlow(0)
    val maxCombo: StateFlow<Int> = _maxCombo.asStateFlow()
    private val _isCriticalHit = MutableStateFlow(false)
    val isCriticalHit: StateFlow<Boolean> = _isCriticalHit.asStateFlow()
    private var comboResetJob: Job? = null

    // Pinging state
    private val _isPingingAll = MutableStateFlow(false)
    val isPingingAll: StateFlow<Boolean> = _isPingingAll.asStateFlow()

    init {
        checkMinecraftInstallation()
        startTelemetryLoop()
        startCpsMonitorLoop()
    }

    fun selectTab(tab: NavTab) {
        _currentTab.value = tab
        haptics.click(hapticsEnabled.value)
    }

    fun setPerformanceProfile(profile: PerformanceProfile) {
        repository.setPerformanceProfile(profile)
        haptics.heavyClick(hapticsEnabled.value)
    }

    fun setAccentTheme(theme: AppAccentTheme) {
        repository.setAccentTheme(theme)
        haptics.click(hapticsEnabled.value)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        repository.setHapticsEnabled(enabled)
    }

    fun setSoundsEnabled(enabled: Boolean) {
        repository.setSoundEffectsEnabled(enabled)
    }

    fun setSelectedModCategory(category: ModCategory) {
        _selectedModCategory.value = category
        haptics.click(hapticsEnabled.value)
    }

    fun setModSearchQuery(query: String) {
        _modSearchQuery.value = query
    }

    fun toggleModPack(modPack: ModPackEntity) {
        viewModelScope.launch {
            repository.toggleModPack(modPack.id)
            haptics.click(hapticsEnabled.value)
        }
    }

    fun deleteModPack(modPack: ModPackEntity) {
        viewModelScope.launch {
            repository.deleteModPack(modPack)
            haptics.heavyClick(hapticsEnabled.value)
        }
    }

    fun toggleServerFavorite(server: ServerEntity) {
        viewModelScope.launch {
            repository.toggleServerFavorite(server.id)
            haptics.click(hapticsEnabled.value)
        }
    }

    fun addCustomServer(name: String, ip: String, port: Int, description: String) {
        viewModelScope.launch {
            val newServer = ServerEntity(
                name = name.ifBlank { "Custom Bedrock Server" },
                ip = ip.trim(),
                port = if (port in 1..65535) port else 19132,
                description = description.ifBlank { "User added custom server" },
                isFeatured = false,
                isFavorite = true,
                bannerColorHex = "#00F0FF",
                pingMs = Random.nextInt(30, 80),
                onlinePlayers = Random.nextInt(50, 800),
                maxPlayers = 1000,
                motd = "§b$name §7- Custom Server"
            )
            repository.insertServer(newServer)
            haptics.success(hapticsEnabled.value)
        }
    }

    fun deleteServer(server: ServerEntity) {
        viewModelScope.launch {
            repository.deleteServer(server)
            haptics.heavyClick(hapticsEnabled.value)
        }
    }

    fun pingAllServers() {
        if (_isPingingAll.value) return
        viewModelScope.launch {
            _isPingingAll.value = true
            val currentServers = allServers.value
            for (server in currentServers) {
                repository.pingServer(server)
                delay(80)
            }
            _isPingingAll.value = false
            haptics.success(hapticsEnabled.value)
        }
    }

    fun pingSingleServer(server: ServerEntity) {
        viewModelScope.launch {
            repository.pingServer(server)
            haptics.click(hapticsEnabled.value)
        }
    }

    // HUD settings updates
    fun toggleHudModule(moduleKey: String) {
        repository.updateHudSettings { current ->
            when (moduleKey) {
                "fps" -> current.copy(fpsEnabled = !current.fpsEnabled)
                "ping" -> current.copy(pingEnabled = !current.pingEnabled)
                "coords" -> current.copy(coordinatesEnabled = !current.coordinatesEnabled)
                "cps" -> current.copy(cpsEnabled = !current.cpsEnabled)
                "combo" -> current.copy(comboEnabled = !current.comboEnabled)
                "armor" -> current.copy(armorEnabled = !current.armorEnabled)
                "potion" -> current.copy(potionEnabled = !current.potionEnabled)
                "keystrokes" -> current.copy(keystrokesEnabled = !current.keystrokesEnabled)
                "crosshair" -> current.copy(crosshairCustomEnabled = !current.crosshairCustomEnabled)
                "fullbright" -> current.copy(fullbrightEnabled = !current.fullbrightEnabled)
                "zoom" -> current.copy(zoomEnabled = !current.zoomEnabled)
                "fast_sneak" -> current.copy(fastSneakEnabled = !current.fastSneakEnabled)
                "low_fire" -> current.copy(lowFireEnabled = !current.lowFireEnabled)
                "power_save" -> current.copy(powerSaveEnabled = !current.powerSaveEnabled)
                else -> current
            }
        }
        haptics.click(hapticsEnabled.value)
    }

    fun updateCrosshair(updater: (CrosshairConfig) -> CrosshairConfig) {
        repository.updateHudSettings { current ->
            current.copy(crosshairConfig = updater(current.crosshairConfig))
        }
    }

    fun updateHudScale(scale: Float) {
        repository.updateHudSettings { it.copy(hudScale = scale) }
    }

    fun updateHudOpacity(opacity: Float) {
        repository.updateHudSettings { it.copy(hudOpacity = opacity) }
    }

    // CPS Clicker Action
    fun registerCpsClick() {
        val now = SystemClock.elapsedRealtime()
        synchronized(clickTimestamps) {
            clickTimestamps.add(now)
        }
        haptics.click(hapticsEnabled.value)

        val newTotal = _cpsState.value.totalClicks + 1
        _cpsState.value = _cpsState.value.copy(totalClicks = newTotal)

        // If challenge mode is active and not started yet
        if (!_cpsState.value.isChallengeActive && _cpsState.value.challengeDurationSec > 0 && _cpsState.value.lastScore == null) {
            startChallengeTimer(_cpsState.value.challengeDurationSec)
        }
    }

    fun setChallengeMode(durationSec: Int) {
        resetCpsTest()
        _cpsState.value = _cpsState.value.copy(
            challengeDurationSec = durationSec,
            timeRemainingSec = durationSec.toFloat(),
            isChallengeActive = false,
            lastScore = null
        )
        haptics.click(hapticsEnabled.value)
    }

    fun resetCpsTest() {
        synchronized(clickTimestamps) {
            clickTimestamps.clear()
        }
        _cpsState.value = CpsTestState(
            challengeDurationSec = _cpsState.value.challengeDurationSec,
            timeRemainingSec = _cpsState.value.challengeDurationSec.toFloat()
        )
        haptics.heavyClick(hapticsEnabled.value)
    }

    private var challengeJob: Job? = null
    private fun startChallengeTimer(duration: Int) {
        challengeJob?.cancel()
        challengeJob = viewModelScope.launch {
            _cpsState.value = _cpsState.value.copy(
                isChallengeActive = true,
                timeRemainingSec = duration.toFloat(),
                totalClicks = 1
            )
            var remaining = duration * 10
            while (remaining > 0) {
                delay(100)
                remaining--
                _cpsState.value = _cpsState.value.copy(timeRemainingSec = remaining / 10f)
            }
            // Finished
            val finalScore = _cpsState.value.totalClicks.toFloat() / duration
            _cpsState.value = _cpsState.value.copy(
                isChallengeActive = false,
                lastScore = finalScore
            )
            haptics.success(hapticsEnabled.value)
        }
    }

    // Combo Trainer Action
    fun registerComboHit(isCrit: Boolean = false) {
        val next = _comboCount.value + 1
        _comboCount.value = next
        if (next > _maxCombo.value) {
            _maxCombo.value = next
        }
        _isCriticalHit.value = isCrit
        haptics.heavyClick(hapticsEnabled.value)

        comboResetJob?.cancel()
        comboResetJob = viewModelScope.launch {
            delay(150)
            _isCriticalHit.value = false
            delay(1500)
            _comboCount.value = 0
        }
    }

    fun resetCombo() {
        comboResetJob?.cancel()
        _comboCount.value = 0
        _maxCombo.value = 0
        _isCriticalHit.value = false
    }

    // Import .mcpack / .mcworld file
    fun handleImportedFileUri(uri: Uri) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val context = getApplication<Application>()
                    var fileName = "Imported_Pack.mcpack"
                    var fileSize = 0L

                    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        if (cursor.moveToFirst()) {
                            if (nameIndex != -1) fileName = cursor.getString(nameIndex) ?: fileName
                            if (sizeIndex != -1) fileSize = cursor.getLong(sizeIndex)
                        }
                    }

                    val formattedSize = if (fileSize > 1024 * 1024) {
                        "%.1f MB".format(fileSize / (1024f * 1024f))
                    } else {
                        "%.1f KB".format(fileSize / 1024f)
                    }

                    val category = when {
                        fileName.endsWith(".mcaddon", ignoreCase = true) -> ModCategory.ADDON
                        fileName.endsWith(".mcworld", ignoreCase = true) -> ModCategory.WORLD
                        fileName.contains("shader", ignoreCase = true) -> ModCategory.SHADER
                        fileName.contains("ui", ignoreCase = true) || fileName.contains("gui", ignoreCase = true) -> ModCategory.UI_TWEAK
                        fileName.contains("pvp", ignoreCase = true) || fileName.contains("opti", ignoreCase = true) -> ModCategory.UTILITY
                        else -> ModCategory.TEXTURE_PACK
                    }

                    val newPack = ModPackEntity(
                        title = fileName.substringBeforeLast('.').replace('_', ' ').replace('-', ' '),
                        category = category,
                        fileName = fileName,
                        fileSizeFormatted = formattedSize,
                        version = "v1.0",
                        author = "User Imported",
                        description = "Imported Minecraft Bedrock package ready to launch and integrate.",
                        isEnabled = true,
                        isBuiltIn = false,
                        features = "Custom Bedrock Content, User Imported",
                        fileUri = uri.toString()
                    )

                    repository.insertModPack(newPack)
                    withContext(Dispatchers.Main) {
                        haptics.success(hapticsEnabled.value)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun importModPack(modPack: ModPackEntity) {
        viewModelScope.launch {
            repository.insertModPack(modPack)
            haptics.success(hapticsEnabled.value)
        }
    }

    fun batchImportModPacks(packs: List<ModPackEntity>) {
        viewModelScope.launch {
            packs.forEach { pack ->
                repository.insertModPack(pack)
            }
            haptics.success(hapticsEnabled.value)
        }
    }

    private fun checkMinecraftInstallation() {
        val installed = MinecraftLauncher.isMinecraftInstalled(getApplication())
        _systemStats.value = _systemStats.value.copy(isMinecraftInstalled = installed)
    }

    private fun startTelemetryLoop() {
        viewModelScope.launch {
            while (true) {
                val isPowerSave = repository.hudSettings.value.powerSaveEnabled
                val pollDelay = if (isPowerSave) 2500L else 1200L
                delay(pollDelay)

                val profile = repository.currentProfile.value
                val baseFps = if (isPowerSave) minOf(profile.targetFps, 30) else profile.targetFps
                val jitterFps = (baseFps + Random.nextInt(-2, 2)).coerceAtLeast(20)
                val pingJitter = Random.nextInt(24, 48)

                _systemStats.value = _systemStats.value.copy(
                    fps = jitterFps,
                    targetFps = if (isPowerSave) 30 else profile.targetFps,
                    pingMs = pingJitter,
                    ramUsedMb = 1720 + Random.nextInt(10, 60),
                    activeProfileName = if (isPowerSave) "${profile.title} (Power Save 30Hz)" else profile.title
                )
            }
        }
    }

    private fun startCpsMonitorLoop() {
        viewModelScope.launch {
            while (true) {
                delay(100)
                val now = SystemClock.elapsedRealtime()
                val windowStart = now - 1000

                synchronized(clickTimestamps) {
                    clickTimestamps.removeAll { it < windowStart }
                    val currentCps = clickTimestamps.size.toFloat()
                    val peak = maxOf(_cpsState.value.peakCps, currentCps)

                    _cpsState.value = _cpsState.value.copy(
                        currentCps = currentCps,
                        peakCps = peak
                    )
                }
            }
        }
    }
}
