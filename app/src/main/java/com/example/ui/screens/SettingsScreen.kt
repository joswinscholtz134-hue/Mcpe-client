package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PerformanceProfile
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonModuleToggle
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AppAccentTheme
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.LocalAppAccentTheme
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.MinecraftLauncher

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val accent = LocalAppAccentTheme.current
    val currentProfile by viewModel.currentProfile.collectAsStateWithLifecycle()
    val hudSettings by viewModel.hudSettings.collectAsStateWithLifecycle()
    val accentTheme by viewModel.accentTheme.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsStateWithLifecycle()
    val soundEffectsEnabled by viewModel.soundEffectsEnabled.collectAsStateWithLifecycle()
    val systemStats by viewModel.systemStats.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(
                title = "Client Settings",
                subtitle = "Profiles, themes, audio & optimizations"
            )
        }

        // Accent Theme Selector
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Palette,
                            contentDescription = null,
                            tint = accent.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "NEON COLOR THEME",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppAccentTheme.values().forEach { theme ->
                            val isSelected = theme == accentTheme
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) theme.primary.copy(alpha = 0.25f) else DarkSurfaceElevated
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isSelected) theme.primary else DarkCardBorder,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.setAccentTheme(theme) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                Brush.linearGradient(listOf(theme.primary, theme.secondary)),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = null,
                                                tint = Color.Black,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = theme.title.split(" ").last(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) theme.primary else TextSecondary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Performance Profile Selection with full breakdown
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Speed,
                            contentDescription = null,
                            tint = accent.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PERFORMANCE PRESETS",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    PerformanceProfile.values().forEach { profile ->
                        val isSelected = profile == currentProfile
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) accent.primary.copy(alpha = 0.15f) else DarkSurfaceElevated
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) accent.primary else DarkCardBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.setPerformanceProfile(profile) }
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = profile.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) accent.primary else TextPrimary
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        NeonBadge(text = "${profile.targetFps} FPS", color = accent.secondary)
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = accent.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = profile.description,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(
                                        text = "Chunks: ${profile.renderDistance}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                    Text(
                                        text = "Particles: ${profile.particleLimit}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                    Text(
                                        text = "Fancy: ${if (profile.fancyGraphics) "ON" else "OFF"}",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Battery & Power Saving Controls
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (hudSettings.powerSaveEnabled) Color(0xFF10B981) else DarkCardBorder,
                backgroundColor = if (hudSettings.powerSaveEnabled) Color(0xFF10B981).copy(alpha = 0.12f) else DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (hudSettings.powerSaveEnabled) Color(0xFF10B981).copy(alpha = 0.25f) else accent.primary.copy(alpha = 0.15f),
                                        CircleShape
                                    )
                                    .border(
                                        1.dp,
                                        if (hudSettings.powerSaveEnabled) Color(0xFF10B981) else DarkCardBorder,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (hudSettings.powerSaveEnabled) Icons.Filled.BatterySaver else Icons.Filled.BatteryChargingFull,
                                    contentDescription = "Power Save",
                                    tint = if (hudSettings.powerSaveEnabled) Color(0xFF10B981) else accent.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Power Save Mode",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (hudSettings.powerSaveEnabled) Color(0xFF10B981) else TextPrimary
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    NeonBadge(
                                        text = if (hudSettings.powerSaveEnabled) "ACTIVE • 30Hz" else "OFF",
                                        color = if (hudSettings.powerSaveEnabled) Color(0xFF10B981) else TextMuted
                                    )
                                }
                                Text(
                                    text = "Reduces UI animation intensity & lowers HUD polling rate",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }
                        }

                        Switch(
                            checked = hudSettings.powerSaveEnabled,
                            onCheckedChange = { viewModel.toggleHudModule("power_save") },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = Color(0xFF10B981)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated.copy(alpha = 0.8f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text("HUD FPS Limit", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
                                Text(if (hudSettings.powerSaveEnabled) "30 FPS" else "Uncapped (60-120)", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (hudSettings.powerSaveEnabled) Color(0xFF10B981) else TextPrimary))
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated.copy(alpha = 0.8f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text("UI Animations", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
                                Text(if (hudSettings.powerSaveEnabled) "Static / Minimal" else "Full Neon Glow", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (hudSettings.powerSaveEnabled) Color(0xFF10B981) else TextPrimary))
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated.copy(alpha = 0.8f))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Column {
                                Text("Battery Drain", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 9.sp))
                                Text(if (hudSettings.powerSaveEnabled) "-45% Overhead" else "Standard", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (hudSettings.powerSaveEnabled) Color(0xFF10B981) else TextPrimary))
                            }
                        }
                    }
                }
            }
        }

        // Client & PvP Tweaks
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "VISUAL & CLIENT TWEAKS",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = accent.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    NeonModuleToggle(
                        title = "Fullbright (Night Vision)",
                        subtitle = "Maximum ambient cave & underwater brightness",
                        isEnabled = hudSettings.fullbrightEnabled,
                        icon = Icons.Outlined.Visibility,
                        onToggle = { viewModel.toggleHudModule("fullbright") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NeonModuleToggle(
                        title = "Fast Sneak & Movement",
                        subtitle = "Smoothed camera height transition when crouching",
                        isEnabled = hudSettings.fastSneakEnabled,
                        icon = Icons.Outlined.ElectricBolt,
                        onToggle = { viewModel.toggleHudModule("fast_sneak") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NeonModuleToggle(
                        title = "Low Fire & Clear Shield",
                        subtitle = "Lowers on-screen fire height for clearer PvP vision",
                        isEnabled = hudSettings.lowFireEnabled,
                        icon = Icons.Outlined.ZoomIn,
                        onToggle = { viewModel.toggleHudModule("low_fire") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    NeonModuleToggle(
                        title = "OptiFine Zoom FOV",
                        subtitle = "Instant smooth telephoto lens view key",
                        isEnabled = hudSettings.zoomEnabled,
                        icon = Icons.Outlined.ZoomIn,
                        onToggle = { viewModel.toggleHudModule("zoom") }
                    )
                }
            }
        }

        // Haptics and Audio
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "FEEDBACK & SOUNDS",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = accent.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Vibration,
                                contentDescription = null,
                                tint = accent.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Tactile Haptic Feedback", style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary))
                                Text("Vibrate on CPS clicks and button taps", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                            }
                        }
                        Switch(
                            checked = hapticsEnabled,
                            onCheckedChange = { viewModel.setHapticsEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = accent.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.VolumeUp,
                                contentDescription = null,
                                tint = accent.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Client Sound Cues", style = MaterialTheme.typography.titleSmall.copy(color = TextPrimary))
                                Text("Critical hit sounds & challenge timers", style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                            }
                        }
                        Switch(
                            checked = soundEffectsEnabled,
                            onCheckedChange = { viewModel.setSoundsEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = accent.primary
                            )
                        )
                    }
                }
            }
        }

        // Bedrock Environment & Directories
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Folder,
                            contentDescription = null,
                            tint = accent.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MINECRAFT BEDROCK DIRECTORY",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Target Path: /Android/data/com.mojang.minecraftpe/files/games/com.mojang/",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Status: ${if (systemStats.isMinecraftInstalled) "Minecraft Bedrock Detected" else "Not Detected / External Realm"}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = if (systemStats.isMinecraftInstalled) StatusOnline else TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Credits & Special Tribute
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = accent.primary.copy(alpha = 0.6f),
                backgroundColor = DarkSurfaceElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(accent.primary.copy(alpha = 0.2f), CircleShape)
                                    .border(1.dp, accent.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = accent.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "CREDITS & COMMUNITY",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                                Text(
                                    text = "Credit to Iceelotl",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = accent.primary,
                                        fontWeight = FontWeight.SemiBold
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
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    Color(0xFFC084FC).copy(alpha = 0.7f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "RIP JUICE WRLD 999 🕊️",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Special thanks to Iceelotl for client design inspiration and Bedrock community tooling concepts.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // Fair Play & Clean Client Disclaimer
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = DarkCardBorder
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = StatusOnline,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FAIR PLAY & CLEAN CLIENT",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = StatusOnline
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MCPE Client is an ethical companion designed strictly for Bedrock players to customize legitimate HUD displays, monitor latency, test click speeds, manage servers, and import resource packs. It does not modify game binaries, bypass authentication, or implement unauthorized combat cheats.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Version 1.0.0 (Bedrock 1.20+ Ready)",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}
