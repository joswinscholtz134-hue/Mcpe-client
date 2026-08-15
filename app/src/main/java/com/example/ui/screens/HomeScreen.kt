package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.PerformanceProfile
import com.example.ui.MainViewModel
import com.example.ui.NavTab
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonPlayButton
import com.example.ui.components.SectionHeader
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LocalAppAccentTheme
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.MinecraftLauncher

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAppAccentTheme.current
    val systemStats by viewModel.systemStats.collectAsStateWithLifecycle()
    val currentProfile by viewModel.currentProfile.collectAsStateWithLifecycle()
    val hudSettings by viewModel.hudSettings.collectAsStateWithLifecycle()
    val servers by viewModel.allServers.collectAsStateWithLifecycle()
    val topServer = servers.firstOrNull { it.isFavorite } ?: servers.firstOrNull()

    LaunchedEffect(Unit) {
        Toast.makeText(
            context,
            "Credit to Iceelotl • RIP JUICE WRLD 🕊️ 999",
            Toast.LENGTH_LONG
        ).show()
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // Opening Credit & Tribute Bar
        item {
            Spacer(modifier = Modifier.height(4.dp))
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = accent.primary.copy(alpha = 0.5f),
                backgroundColor = DarkSurfaceElevated
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(accent.primary.copy(alpha = 0.2f), CircleShape)
                                .border(1.dp, accent.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = accent.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Credit to Iceelotl",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Client Creator & Design Credit",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF9333EA).copy(alpha = 0.4f),
                                        Color(0xFF3B82F6).copy(alpha = 0.4f)
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFC084FC),
                                        Color(0xFF60A5FA)
                                    )
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RIP JUICE WRLD",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.8.sp,
                                    color = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "999 🕊️",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFC084FC)
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            // Hero Banner with glowing overlay & Client Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            listOf(
                                accent.primary.copy(alpha = 0.6f),
                                DarkCardBorder
                            )
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                // Background image
                Image(
                    painter = painterResource(id = R.drawable.bg_hero_banner),
                    contentDescription = "Hero Wallpaper",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Black.copy(alpha = 0.92f)
                                )
                            )
                        )
                )

                // Client Identity Overlay
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_app_logo),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, accent.primary, RoundedCornerShape(10.dp))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MCPE CLIENT",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.2.sp,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "BEDROCK PRO SUITE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = accent.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }

                        // Minecraft Status Pill
                        val isInstalled = systemStats.isMinecraftInstalled
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isInstalled) StatusOnline.copy(alpha = 0.2f) else StatusWarning.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    0.8.dp,
                                    if (isInstalled) StatusOnline.copy(alpha = 0.6f) else StatusWarning.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(
                                            if (isInstalled) StatusOnline else StatusWarning,
                                            CircleShape
                                        )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isInstalled) "MC READY" else "EXTERNAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isInstalled) StatusOnline else StatusWarning
                                    )
                                )
                            }
                        }
                    }

                    Column {
                        Text(
                            text = "NEXT-GEN BEDROCK COMPANION",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Maximized FPS • Custom HUD • One-Tap Server Deep-Link",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }
        }

        // Giant LAUNCH MINECRAFT / PLAY Button
        item {
            NeonPlayButton(
                text = "LAUNCH MINECRAFT",
                subtext = "START BEDROCK CLIENT COMPANION",
                icon = Icons.Filled.RocketLaunch,
                onClick = {
                    MinecraftLauncher.launchMinecraft(context)
                }
            )
        }

        // Live Telemetry Bar
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TelemetryStatItem(
                        icon = Icons.Filled.Speed,
                        value = "${systemStats.fps} FPS",
                        label = "TARGET ${systemStats.targetFps}",
                        color = accent.primary
                    )
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(DarkCardBorder))
                    TelemetryStatItem(
                        icon = Icons.Filled.Wifi,
                        value = "${systemStats.pingMs} ms",
                        label = "LATENCY",
                        color = if (systemStats.pingMs < 50) StatusOnline else StatusWarning
                    )
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(DarkCardBorder))
                    TelemetryStatItem(
                        icon = Icons.Filled.Memory,
                        value = "${systemStats.ramUsedMb} MB",
                        label = "RAM ALLOC",
                        color = accent.secondary
                    )
                }
            }
        }

        // Active Performance Profiles
        item {
            SectionHeader(
                title = "Performance Profile",
                subtitle = "Select instant optimization preset"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(PerformanceProfile.values()) { profile ->
                    val isSelected = profile == currentProfile
                    GlassCard(
                        modifier = Modifier.width(160.dp),
                        borderColor = if (isSelected) accent.primary else DarkCardBorder,
                        backgroundColor = if (isSelected) accent.primary.copy(alpha = 0.15f) else DarkSurfaceElevated,
                        onClick = { viewModel.setPerformanceProfile(profile) }
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = profile.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) accent.primary else TextPrimary
                                    )
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = accent.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${profile.targetFps} FPS Target",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isSelected) accent.secondary else TextMuted
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = profile.subtitle,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                ),
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }

        // Quick Module Toggles
        item {
            SectionHeader(
                title = "Quick Client Modules",
                subtitle = "Persistent HUD and PvP utilities"
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickModuleTile(
                        title = "Fullbright",
                        desc = "Gamma Vision",
                        icon = Icons.Filled.Visibility,
                        isEnabled = hudSettings.fullbrightEnabled,
                        modifier = Modifier.weight(1f),
                        onToggle = { viewModel.toggleHudModule("fullbright") }
                    )
                    QuickModuleTile(
                        title = "Crosshair",
                        desc = "Custom Reticle",
                        icon = Icons.Outlined.CenterFocusStrong,
                        isEnabled = hudSettings.crosshairCustomEnabled,
                        modifier = Modifier.weight(1f),
                        onToggle = { viewModel.toggleHudModule("crosshair") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickModuleTile(
                        title = "Armor HUD",
                        desc = "Durability Bar",
                        icon = Icons.Outlined.Shield,
                        isEnabled = hudSettings.armorEnabled,
                        modifier = Modifier.weight(1f),
                        onToggle = { viewModel.toggleHudModule("armor") }
                    )
                    QuickModuleTile(
                        title = "CPS Counter",
                        desc = "Clicks / Sec",
                        icon = Icons.Outlined.TouchApp,
                        isEnabled = hudSettings.cpsEnabled,
                        modifier = Modifier.weight(1f),
                        onToggle = { viewModel.toggleHudModule("cps") }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickModuleTile(
                        title = "Keystrokes",
                        desc = "WASD Visualizer",
                        icon = Icons.Filled.SportsEsports,
                        isEnabled = hudSettings.keystrokesEnabled,
                        modifier = Modifier.weight(1f),
                        onToggle = { viewModel.toggleHudModule("keystrokes") }
                    )
                    QuickModuleTile(
                        title = "Zoom FOV",
                        desc = "OptiFine View",
                        icon = Icons.Outlined.ZoomIn,
                        isEnabled = hudSettings.zoomEnabled,
                        modifier = Modifier.weight(1f),
                        onToggle = { viewModel.toggleHudModule("zoom") }
                    )
                }
            }
        }

        // Quick Connect Recent/Favorite Server
        if (topServer != null) {
            item {
                SectionHeader(
                    title = "Featured Bedrock Server",
                    subtitle = "One-tap direct server join"
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = accent.primary.copy(alpha = 0.4f),
                    onClick = {
                        MinecraftLauncher.connectToServer(context, topServer.name, topServer.ip, topServer.port)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = topServer.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                NeonBadge(
                                    text = "${topServer.pingMs}ms",
                                    color = if (topServer.pingMs < 60) StatusOnline else StatusWarning
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${topServer.ip}:${topServer.port}",
                                style = MaterialTheme.typography.labelSmall.copy(color = accent.secondary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${topServer.onlinePlayers} players online",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(listOf(accent.primary, accent.secondary))
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = "Join",
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CONNECT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }

    // Floating Action Button Group for Quick Settings Toggles
    QuickSettingsFabGroup(
        hudSettings = hudSettings,
        onToggleModule = { viewModel.toggleHudModule(it) },
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp, bottom = 84.dp)
    )
}
}

@Composable
fun QuickSettingsFabGroup(
    hudSettings: com.example.data.model.HudSettings,
    onToggleModule: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAppAccentTheme.current
    var isExpanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 135f else 0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f),
        label = "fab_rotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Expanded mini-FAB action items
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }) + scaleIn(initialScale = 0.8f),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }) + scaleOut(targetScale = 0.8f)
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated.copy(alpha = 0.95f))
                        .border(1.dp, accent.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "QUICK HUD TOGGLES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = accent.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                // 1. FPS Display toggle
                FabQuickToggleItem(
                    title = "FPS Display",
                    subtitle = "Frame Rate HUD",
                    icon = Icons.Filled.Speed,
                    isEnabled = hudSettings.fpsEnabled,
                    testTag = "fab_toggle_fps",
                    onToggle = { onToggleModule("fps") }
                )

                // 2. Ping Display toggle
                FabQuickToggleItem(
                    title = "Ping Display",
                    subtitle = "Latency HUD",
                    icon = Icons.Filled.Wifi,
                    isEnabled = hudSettings.pingEnabled,
                    testTag = "fab_toggle_ping",
                    onToggle = { onToggleModule("ping") }
                )

                // 3. Coordinates toggle
                FabQuickToggleItem(
                    title = "Coordinates",
                    subtitle = "XYZ Position",
                    icon = Icons.Outlined.CenterFocusStrong,
                    isEnabled = hudSettings.coordinatesEnabled,
                    testTag = "fab_toggle_coords",
                    onToggle = { onToggleModule("coords") }
                )

                // 4. CPS Counter toggle
                FabQuickToggleItem(
                    title = "CPS Counter",
                    subtitle = "Clicks / Sec",
                    icon = Icons.Outlined.TouchApp,
                    isEnabled = hudSettings.cpsEnabled,
                    testTag = "fab_toggle_cps",
                    onToggle = { onToggleModule("cps") }
                )

                // 5. OptiFine Zoom toggle
                FabQuickToggleItem(
                    title = "OptiFine Zoom",
                    subtitle = "FOV Enhancer",
                    icon = Icons.Outlined.ZoomIn,
                    isEnabled = hudSettings.zoomEnabled,
                    testTag = "fab_toggle_zoom",
                    onToggle = { onToggleModule("zoom") }
                )
            }
        }

        // Main FAB Button
        Box(
            modifier = Modifier
                .size(56.dp)
                .shadow(16.dp, CircleShape, spotColor = accent.glow, ambientColor = accent.primary)
                .clip(CircleShape)
                .background(
                    if (isExpanded) {
                        Brush.radialGradient(listOf(DarkSurfaceElevated, Color.Black))
                    } else {
                        Brush.linearGradient(listOf(accent.primary, accent.secondary))
                    }
                )
                .border(
                    width = 2.dp,
                    color = if (isExpanded) accent.primary else Color.White.copy(alpha = 0.6f),
                    shape = CircleShape
                )
                .clickable { isExpanded = !isExpanded }
                .testTag("fab_quick_settings_toggle"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isExpanded) Icons.Filled.Close else Icons.Filled.Tune,
                contentDescription = if (isExpanded) "Close Quick Settings" else "Open Quick Settings Toggles",
                tint = if (isExpanded) accent.primary else Color.Black,
                modifier = Modifier
                    .size(26.dp)
                    .rotate(rotation)
            )
        }
    }
}

@Composable
private fun FabQuickToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isEnabled: Boolean,
    testTag: String,
    onToggle: () -> Unit
) {
    val accent = LocalAppAccentTheme.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onToggle
            )
            .testTag(testTag)
    ) {
        // Label Pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated.copy(alpha = 0.94f))
                .border(
                    1.dp,
                    if (isEnabled) accent.primary.copy(alpha = 0.6f) else DarkCardBorder,
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isEnabled) TextPrimary else TextSecondary
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            color = TextMuted
                        )
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (isEnabled) accent.primary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (isEnabled) "ON" else "OFF",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isEnabled) accent.primary else TextMuted
                        )
                    )
                }
            }
        }

        // Circular Icon Button
        Box(
            modifier = Modifier
                .size(44.dp)
                .shadow(8.dp, CircleShape, spotColor = if (isEnabled) accent.glow else Color.Transparent)
                .clip(CircleShape)
                .background(
                    if (isEnabled) {
                        Brush.radialGradient(listOf(accent.primary, accent.primary.copy(alpha = 0.75f)))
                    } else {
                        Brush.radialGradient(listOf(DarkSurfaceElevated, Color.Black))
                    }
                )
                .border(
                    1.5.dp,
                    if (isEnabled) Color.White.copy(alpha = 0.8f) else DarkCardBorder,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isEnabled) Color.Black else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun TelemetryStatItem(
    icon: ImageVector,
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 9.sp,
                color = TextMuted
            )
        )
    }
}

@Composable
fun QuickModuleTile(
    title: String,
    desc: String,
    icon: ImageVector,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    val accent = LocalAppAccentTheme.current
    GlassCard(
        modifier = modifier,
        borderColor = if (isEnabled) accent.primary.copy(alpha = 0.5f) else DarkCardBorder,
        backgroundColor = if (isEnabled) accent.primary.copy(alpha = 0.12f) else DarkSurfaceElevated,
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isEnabled) accent.primary else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isEnabled) TextPrimary else TextSecondary
                        )
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        if (isEnabled) accent.primary else TextMuted.copy(alpha = 0.4f),
                        CircleShape
                    )
            )
        }
    }
}
