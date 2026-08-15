package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "servers")
data class ServerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val ip: String,
    val port: Int = 19132,
    val description: String = "",
    val isFeatured: Boolean = false,
    val isFavorite: Boolean = false,
    val bannerColorHex: String = "#A855F7",
    val pingMs: Int = -1,
    val onlinePlayers: Int = 0,
    val maxPlayers: Int = 0,
    val motd: String = "",
    val gameModes: String = "BedWars, SkyWars, Duels",
    val lastPingTimestamp: Long = 0L
)

enum class ModCategory(val displayName: String, val iconName: String) {
    ALL("All Packs", "All"),
    TEXTURE_PACK("Textures", "Palette"),
    ADDON("Add-ons", "Extension"),
    SHADER("Shaders", "WbSunny"),
    UI_TWEAK("UI Tweaks", "DashboardCustomize"),
    UTILITY("PvP Utility", "SportsEsports"),
    WORLD("Worlds", "Public")
}

@Entity(tableName = "mod_packs")
data class ModPackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: ModCategory,
    val fileName: String,
    val fileSizeFormatted: String,
    val version: String,
    val author: String,
    val description: String,
    val isEnabled: Boolean = true,
    val isBuiltIn: Boolean = false,
    val features: String = "", // Comma-separated list
    val resolution: String = "32x",
    val fileUri: String = "",
    val dateAdded: Long = System.currentTimeMillis()
)

enum class PerformanceProfile(
    val title: String,
    val subtitle: String,
    val description: String,
    val targetFps: Int,
    val renderDistance: Int,
    val fancyGraphics: Boolean,
    val smoothLighting: Boolean,
    val particleLimit: String
) {
    ULTRA_FPS(
        title = "Ultra FPS",
        subtitle = "Max Latency & Frame Rate",
        description = "Stripped particle effects, optimized chunk buffering and low-overhead UI for maximum PvP frame rates.",
        targetFps = 120,
        renderDistance = 6,
        fancyGraphics = false,
        smoothLighting = false,
        particleLimit = "Minimal"
    ),
    BALANCED(
        title = "Balanced",
        subtitle = "Competitive & Clear",
        description = "Balanced visual clarity with 8 chunk render distance and smooth lighting for optimal tournament play.",
        targetFps = 90,
        renderDistance = 8,
        fancyGraphics = true,
        smoothLighting = true,
        particleLimit = "Decreased"
    ),
    HIGH_QUALITY(
        title = "High Quality",
        subtitle = "Full Visual Fidelity",
        description = "Max render distance, full particle fidelity, fancy clouds and cinematic camera transitions.",
        targetFps = 60,
        renderDistance = 14,
        fancyGraphics = true,
        smoothLighting = true,
        particleLimit = "All"
    ),
    BATTERY_SAVER(
        title = "Battery Saver",
        subtitle = "Cooling & Efficiency",
        description = "Locks frame rates to 30/60 FPS, minimizes background polling and optimizes thermal endurance.",
        targetFps = 30,
        renderDistance = 6,
        fancyGraphics = false,
        smoothLighting = false,
        particleLimit = "Minimal"
    )
}

enum class CrosshairStyle(val displayName: String) {
    CLASSIC_CROSS("Classic Cross"),
    DOT("Center Dot"),
    CIRCLE("Tactical Circle"),
    CHEVRON("Apex Chevron"),
    SQUARE("Minimal Square"),
    T_SHAPE("T-Shape")
}

data class CrosshairConfig(
    val style: CrosshairStyle = CrosshairStyle.CLASSIC_CROSS,
    val colorHex: String = "#00F0FF",
    val size: Float = 14f,
    val thickness: Float = 2.5f,
    val gap: Float = 5f,
    val showCenterDot: Boolean = true,
    val dynamicAttackBloom: Boolean = true,
    val outline: Boolean = true
)

data class HudSettings(
    val fpsEnabled: Boolean = true,
    val pingEnabled: Boolean = true,
    val coordinatesEnabled: Boolean = true,
    val cpsEnabled: Boolean = true,
    val comboEnabled: Boolean = true,
    val armorEnabled: Boolean = true,
    val potionEnabled: Boolean = true,
    val keystrokesEnabled: Boolean = true,
    val crosshairCustomEnabled: Boolean = true,
    val fullbrightEnabled: Boolean = true,
    val zoomEnabled: Boolean = true,
    val fastSneakEnabled: Boolean = true,
    val lowFireEnabled: Boolean = true,
    val powerSaveEnabled: Boolean = false,
    val hudScale: Float = 1.0f,
    val hudOpacity: Float = 0.85f,
    val crosshairConfig: CrosshairConfig = CrosshairConfig()
)
