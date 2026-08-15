package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CrosshairConfig
import com.example.data.model.CrosshairStyle
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonBadge
import com.example.ui.components.NeonModuleToggle
import com.example.ui.components.SectionHeader
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.LocalAppAccentTheme
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PvpScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val accent = LocalAppAccentTheme.current
    val hudSettings by viewModel.hudSettings.collectAsStateWithLifecycle()
    val cpsState by viewModel.cpsState.collectAsStateWithLifecycle()
    val comboCount by viewModel.comboCount.collectAsStateWithLifecycle()
    val maxCombo by viewModel.maxCombo.collectAsStateWithLifecycle()
    val isCrit by viewModel.isCriticalHit.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableIntStateOf(0) }
    val subTabs = listOf("CPS & Combo", "Crosshairs", "HUD Overlays")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            SectionHeader(
                title = "PvP & HUD Suite",
                subtitle = "Custom reticles, click speed, overlays & combat tools"
            )
        }

        // Sub-Tab Switcher
        item {
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = DarkSurfaceElevated,
                contentColor = accent.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                        color = accent.primary,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
            ) {
                subTabs.forEachIndexed { index, title ->
                    Tab(
                        selected = activeSubTab == index,
                        onClick = {
                            activeSubTab = index
                            viewModel.haptics.click(viewModel.hapticsEnabled.value)
                        },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = if (activeSubTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (activeSubTab == index) accent.primary else TextSecondary
                                )
                            )
                        }
                    )
                }
            }
        }

        when (activeSubTab) {
            0 -> {
                // CPS & COMBO TAB
                item {
                    CpsTesterCard(
                        cpsState = cpsState,
                        onRegisterClick = { viewModel.registerCpsClick() },
                        onSetChallengeMode = { viewModel.setChallengeMode(it) },
                        onReset = { viewModel.resetCpsTest() }
                    )
                }

                item {
                    ComboTrainerCard(
                        comboCount = comboCount,
                        maxCombo = maxCombo,
                        isCrit = isCrit,
                        onRegisterHit = { viewModel.registerComboHit(it) },
                        onReset = { viewModel.resetCombo() }
                    )
                }
            }
            1 -> {
                // CROSSHAIRS TAB
                item {
                    CrosshairCustomizerCard(
                        config = hudSettings.crosshairConfig,
                        isEnabled = hudSettings.crosshairCustomEnabled,
                        onToggle = { viewModel.toggleHudModule("crosshair") },
                        onUpdate = { viewModel.updateCrosshair(it) }
                    )
                }
            }
            2 -> {
                // HUD OVERLAYS TAB
                item {
                    HudOverlaysManagerCard(
                        hudSettings = hudSettings,
                        onToggleModule = { viewModel.toggleHudModule(it) },
                        onScaleChanged = { viewModel.updateHudScale(it) },
                        onOpacityChanged = { viewModel.updateHudOpacity(it) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

// ---------------------- CPS TESTER CARD ----------------------
@Composable
fun CpsTesterCard(
    cpsState: com.example.ui.CpsTestState,
    onRegisterClick: () -> Unit,
    onSetChallengeMode: (Int) -> Unit,
    onReset: () -> Unit
) {
    val accent = LocalAppAccentTheme.current

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.TouchApp,
                        contentDescription = null,
                        tint = accent.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CPS SPEED TESTER",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                // Reset button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .clickable { onReset() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Reset",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timer Challenge Mode Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0 to "Free Click", 5 to "5s Speed", 10 to "10s Endurance").forEach { (sec, label) ->
                    val isSelected = cpsState.challengeDurationSec == sec
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) accent.primary.copy(alpha = 0.2f) else DarkSurfaceElevated
                            )
                            .border(
                                1.dp,
                                if (isSelected) accent.primary else DarkCardBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSetChallengeMode(sec) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accent.primary else TextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Score Display Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ScoreItem(
                    label = "CURRENT CPS",
                    value = "%.1f".format(cpsState.currentCps),
                    color = accent.primary
                )
                ScoreItem(
                    label = "PEAK CPS",
                    value = "%.1f".format(cpsState.peakCps),
                    color = accent.secondary
                )
                ScoreItem(
                    label = "TOTAL CLICKS",
                    value = "${cpsState.totalClicks}",
                    color = NeonAmber
                )
                if (cpsState.challengeDurationSec > 0) {
                    ScoreItem(
                        label = "TIMER",
                        value = "%.1fs".format(cpsState.timeRemainingSec),
                        color = if (cpsState.isChallengeActive) NeonCrimson else TextMuted
                    )
                }
            }

            if (cpsState.lastScore != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(StatusOnline.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .border(1.dp, StatusOnline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Challenge Complete! Average Speed: ${"%.2f".format(cpsState.lastScore)} CPS",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = StatusOnline
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Giant Interactive Click Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                accent.primary.copy(alpha = 0.25f),
                                DarkSurfaceElevated
                            )
                        )
                    )
                    .border(1.5.dp, accent.primary.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onRegisterClick
                    )
                    .testTag("cps_click_box"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.FlashOn,
                        contentDescription = "Click",
                        tint = accent.primary,
                        modifier = Modifier.size(34.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TAP REPEATEDLY HERE",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Butterfly / Jitter / Drag Click Support",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            }
        }
    }
}

// ---------------------- COMBO TRAINER CARD ----------------------
@Composable
fun ComboTrainerCard(
    comboCount: Int,
    maxCombo: Int,
    isCrit: Boolean,
    onRegisterHit: (Boolean) -> Unit,
    onReset: () -> Unit
) {
    val accent = LocalAppAccentTheme.current

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ElectricBolt,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PVP COMBO & CRIT TRAINER",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceElevated)
                        .clickable { onReset() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Reset", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Combo Gauge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$comboCount",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = if (comboCount > 5) NeonAmber else accent.primary
                        )
                    )
                    Text(
                        text = "CURRENT COMBO",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(40.dp).background(DarkCardBorder))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$maxCombo",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = accent.secondary
                        )
                    )
                    Text(
                        text = "MAX STREAK",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(40.dp).background(DarkCardBorder))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isCrit) "CRITICAL!" else "NORMAL",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (isCrit) NeonCrimson else TextSecondary
                        )
                    )
                    Text(
                        text = "HIT TYPE",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons (Normal Hit vs Jump Crit)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { onRegisterHit(false) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent.primary)
                ) {
                    Text(
                        text = "SWORD HIT",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }

                Button(
                    onClick = { onRegisterHit(true) },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson)
                ) {
                    Text(
                        text = "JUMP CRIT",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

// ---------------------- CROSSHAIR CUSTOMIZER CARD ----------------------
@Composable
fun CrosshairCustomizerCard(
    config: CrosshairConfig,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    onUpdate: ((CrosshairConfig) -> CrosshairConfig) -> Unit
) {
    val accent = LocalAppAccentTheme.current

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CenterFocusStrong,
                        contentDescription = null,
                        tint = accent.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "TACTICAL CROSSHAIRS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Dynamic Bedrock Combat Reticle",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                    }
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = accent.primary
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // LIVE CROSSHAIR PREVIEW CANVAS
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkBg)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background grid / target mock
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    // Grid lines
                    for (x in 0..w.toInt() step 30) {
                        drawLine(
                            Color.White.copy(alpha = 0.03f),
                            Offset(x.toFloat(), 0f),
                            Offset(x.toFloat(), h)
                        )
                    }
                    for (y in 0..h.toInt() step 30) {
                        drawLine(
                            Color.White.copy(alpha = 0.03f),
                            Offset(0f, y.toFloat()),
                            Offset(w, y.toFloat())
                        )
                    }

                    // Target rings
                    drawCircle(
                        Color.White.copy(alpha = 0.05f),
                        radius = 50f,
                        center = Offset(w / 2, h / 2),
                        style = Stroke(width = 1f)
                    )
                    drawCircle(
                        Color.White.copy(alpha = 0.08f),
                        radius = 25f,
                        center = Offset(w / 2, h / 2),
                        style = Stroke(width = 1f)
                    )

                    // Draw Crosshair
                    val crosshairColor = try {
                        Color(android.graphics.Color.parseColor(config.colorHex))
                    } catch (e: Exception) {
                        accent.primary
                    }

                    val centerX = w / 2
                    val centerY = h / 2
                    val sz = config.size * 2f
                    val th = config.thickness * 1.5f
                    val gp = config.gap * 2f

                    // Center dot
                    if (config.showCenterDot) {
                        drawCircle(
                            color = crosshairColor,
                            radius = th / 1.5f,
                            center = Offset(centerX, centerY)
                        )
                    }

                    when (config.style) {
                        CrosshairStyle.CLASSIC_CROSS -> {
                            // Top
                            drawLine(crosshairColor, Offset(centerX, centerY - gp), Offset(centerX, centerY - gp - sz), strokeWidth = th)
                            // Bottom
                            drawLine(crosshairColor, Offset(centerX, centerY + gp), Offset(centerX, centerY + gp + sz), strokeWidth = th)
                            // Left
                            drawLine(crosshairColor, Offset(centerX - gp, centerY), Offset(centerX - gp - sz, centerY), strokeWidth = th)
                            // Right
                            drawLine(crosshairColor, Offset(centerX + gp, centerY), Offset(centerX + gp + sz, centerY), strokeWidth = th)
                        }
                        CrosshairStyle.DOT -> {
                            drawCircle(crosshairColor, radius = sz / 2.5f, center = Offset(centerX, centerY))
                        }
                        CrosshairStyle.CIRCLE -> {
                            drawCircle(
                                color = crosshairColor,
                                radius = gp + sz / 2,
                                center = Offset(centerX, centerY),
                                style = Stroke(width = th)
                            )
                        }
                        CrosshairStyle.CHEVRON -> {
                            val path = Path().apply {
                                moveTo(centerX - sz, centerY + sz / 2)
                                lineTo(centerX, centerY - sz / 2)
                                lineTo(centerX + sz, centerY + sz / 2)
                            }
                            drawPath(path, color = crosshairColor, style = Stroke(width = th))
                        }
                        CrosshairStyle.SQUARE -> {
                            drawRect(
                                color = crosshairColor,
                                topLeft = Offset(centerX - (gp + sz) / 2, centerY - (gp + sz) / 2),
                                size = Size(gp + sz, gp + sz),
                                style = Stroke(width = th)
                            )
                        }
                        CrosshairStyle.T_SHAPE -> {
                            // Bottom
                            drawLine(crosshairColor, Offset(centerX, centerY + gp), Offset(centerX, centerY + gp + sz), strokeWidth = th)
                            // Left
                            drawLine(crosshairColor, Offset(centerX - gp, centerY), Offset(centerX - gp - sz, centerY), strokeWidth = th)
                            // Right
                            drawLine(crosshairColor, Offset(centerX + gp, centerY), Offset(centerX + gp + sz, centerY), strokeWidth = th)
                        }
                    }
                }

                Text(
                    text = "COMBAT TARGET PREVIEW",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.sp,
                        color = TextMuted
                    ),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Style Selector Chips
            Text(
                text = "RETICLE STYLE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = accent.primary)
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(CrosshairStyle.values()) { style ->
                    val isSelected = config.style == style
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) accent.primary.copy(alpha = 0.2f) else DarkSurfaceElevated)
                            .border(1.dp, if (isSelected) accent.primary else DarkCardBorder, RoundedCornerShape(8.dp))
                            .clickable { onUpdate { it.copy(style = style) } }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = style.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accent.primary else TextSecondary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Color Selector Chips
            Text(
                text = "RETICLE COLOR",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = accent.primary)
            )
            Spacer(modifier = Modifier.height(6.dp))
            val colors = listOf(
                "#00F0FF" to NeonCyan,
                "#A855F7" to NeonPurple,
                "#10B981" to NeonEmerald,
                "#F43F5E" to NeonCrimson,
                "#F59E0B" to NeonAmber,
                "#FFFFFF" to Color.White
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                colors.forEach { (hex, col) ->
                    val isSelected = config.colorHex.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(col, CircleShape)
                            .border(
                                2.dp,
                                if (isSelected) Color.White else Color.Transparent,
                                CircleShape
                            )
                            .clickable { onUpdate { it.copy(colorHex = hex) } },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = if (hex == "#FFFFFF") Color.Black else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sliders: Size, Thickness, Gap
            SliderControl(
                label = "Crosshair Size: ${config.size.toInt()}px",
                value = config.size,
                range = 6f..30f,
                onValueChange = { onUpdate { prev -> prev.copy(size = it) } }
            )

            SliderControl(
                label = "Thickness: ${"%.1f".format(config.thickness)}px",
                value = config.thickness,
                range = 1f..6f,
                onValueChange = { onUpdate { prev -> prev.copy(thickness = it) } }
            )

            SliderControl(
                label = "Center Gap: ${config.gap.toInt()}px",
                value = config.gap,
                range = 0f..20f,
                onValueChange = { onUpdate { prev -> prev.copy(gap = it) } }
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Center Dot Indicator",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                )
                Switch(
                    checked = config.showCenterDot,
                    onCheckedChange = { onUpdate { prev -> prev.copy(showCenterDot = it) } },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = accent.primary
                    )
                )
            }
        }
    }
}

// ---------------------- HUD OVERLAYS MANAGER CARD WITH DRAGGABLE PREVIEW ----------------------
@Composable
fun HudOverlaysManagerCard(
    hudSettings: com.example.data.model.HudSettings,
    onToggleModule: (String) -> Unit,
    onScaleChanged: (Float) -> Unit,
    onOpacityChanged: (Float) -> Unit
) {
    val accent = LocalAppAccentTheme.current

    // Individual HUD Element Position States (offsets in px within preview container)
    var fpsOffset by remember { mutableStateOf(Offset(12f, 10f)) }
    var pingOffset by remember { mutableStateOf(Offset(12f, 38f)) }
    var coordsOffset by remember { mutableStateOf(Offset(12f, 66f)) }
    var cpsOffset by remember { mutableStateOf(Offset(12f, 116f)) }
    var potionOffset by remember { mutableStateOf(Offset(230f, 10f)) }
    var armorOffset by remember { mutableStateOf(Offset(12f, 160f)) }
    var keystrokesOffset by remember { mutableStateOf(Offset(230f, 130f)) }

    var draggingElement by remember { mutableStateOf<String?>(null) }

    fun resetToDefault() {
        fpsOffset = Offset(12f, 10f)
        pingOffset = Offset(12f, 38f)
        coordsOffset = Offset(12f, 66f)
        cpsOffset = Offset(12f, 116f)
        potionOffset = Offset(230f, 10f)
        armorOffset = Offset(12f, 160f)
        keystrokesOffset = Offset(230f, 130f)
    }

    fun applyStreamerPreset() {
        fpsOffset = Offset(12f, 8f)
        pingOffset = Offset(110f, 8f)
        cpsOffset = Offset(200f, 8f)
        coordsOffset = Offset(12f, 34f)
        potionOffset = Offset(240f, 8f)
        armorOffset = Offset(12f, 180f)
        keystrokesOffset = Offset(240f, 135f)
    }

    fun applyPvpClusterPreset() {
        fpsOffset = Offset(12f, 10f)
        pingOffset = Offset(12f, 36f)
        coordsOffset = Offset(12f, 62f)
        cpsOffset = Offset(130f, 130f)
        potionOffset = Offset(230f, 10f)
        armorOffset = Offset(12f, 140f)
        keystrokesOffset = Offset(230f, 120f)
    }

    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    title = "Client HUD Overlays",
                    subtitle = "Drag & drop elements directly on the preview canvas"
                )
            }

            // Interactive Drag Guide Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, accent.primary.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.OpenWith,
                            contentDescription = null,
                            tint = accent.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (draggingElement != null) "Repositioning: $draggingElement" else "Drag any widget to customize on-screen position",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (draggingElement != null) accent.primary else TextSecondary,
                                fontWeight = if (draggingElement != null) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }

                    IconButton(
                        onClick = { resetToDefault() },
                        modifier = Modifier
                            .size(26.dp)
                            .testTag("reset_hud_positions_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.RestartAlt,
                            contentDescription = "Reset Positions",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // INTERACTIVE DRAG-AND-DROP CANVAS CONTAINER
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF0F172A),
                                Color(0xFF020617)
                            )
                        )
                    )
                    .border(
                        1.5.dp,
                        if (draggingElement != null) accent.primary else DarkCardBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .testTag("interactive_hud_canvas")
            ) {
                val maxX = constraints.maxWidth.toFloat()
                val maxY = constraints.maxHeight.toFloat()

                // Subtle Minecraft Bedrock grid & crosshair center reference
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val midX = size.width / 2
                    val midY = size.height / 2

                    // Grid lines
                    drawLine(
                        color = Color.White.copy(alpha = 0.04f),
                        start = Offset(midX, 0f),
                        end = Offset(midX, size.height),
                        strokeWidth = 1f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.04f),
                        start = Offset(0f, midY),
                        end = Offset(size.width, midY),
                        strokeWidth = 1f
                    )

                    // Center Reticle
                    val reticleLen = 8.dp.toPx()
                    drawLine(
                        color = Color.White.copy(alpha = 0.4f),
                        start = Offset(midX - reticleLen, midY),
                        end = Offset(midX + reticleLen, midY),
                        strokeWidth = 1.5f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.4f),
                        start = Offset(midX, midY - reticleLen),
                        end = Offset(midX, midY + reticleLen),
                        strokeWidth = 1.5f
                    )
                }

                // 1. DRAGGABLE FPS DISPLAY
                if (hudSettings.fpsEnabled) {
                    DraggableHudElement(
                        title = "FPS",
                        offset = fpsOffset,
                        isDragging = draggingElement == "FPS Display",
                        onDragStart = { draggingElement = "FPS Display" },
                        onDragEnd = { draggingElement = null },
                        onDrag = { dx, dy ->
                            fpsOffset = Offset(
                                (fpsOffset.x + dx).coerceIn(4f, maxX - 120f),
                                (fpsOffset.y + dy).coerceIn(4f, maxY - 30f)
                            )
                        },
                        testTag = "draggable_hud_fps"
                    ) {
                        Text(
                            text = "FPS: 118 [120]",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = accent.primary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // 2. DRAGGABLE PING DISPLAY
                if (hudSettings.pingEnabled) {
                    DraggableHudElement(
                        title = "Ping",
                        offset = pingOffset,
                        isDragging = draggingElement == "Ping Gauge",
                        onDragStart = { draggingElement = "Ping Gauge" },
                        onDragEnd = { draggingElement = null },
                        onDrag = { dx, dy ->
                            pingOffset = Offset(
                                (pingOffset.x + dx).coerceIn(4f, maxX - 130f),
                                (pingOffset.y + dy).coerceIn(4f, maxY - 30f)
                            )
                        },
                        testTag = "draggable_hud_ping"
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(StatusOnline, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "PING: 32ms",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = StatusOnline,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // 3. DRAGGABLE COORDINATES DISPLAY
                if (hudSettings.coordinatesEnabled) {
                    DraggableHudElement(
                        title = "Coords",
                        offset = coordsOffset,
                        isDragging = draggingElement == "Coordinates",
                        onDragStart = { draggingElement = "Coordinates" },
                        onDragEnd = { draggingElement = null },
                        onDrag = { dx, dy ->
                            coordsOffset = Offset(
                                (coordsOffset.x + dx).coerceIn(4f, maxX - 180f),
                                (coordsOffset.y + dy).coerceIn(4f, maxY - 45f)
                            )
                        },
                        testTag = "draggable_hud_coords"
                    ) {
                        Column {
                            Text(
                                text = "XYZ: 142.5 / 68.0 / -230.1",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = accent.secondary,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "Facing: North (Plains)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            )
                        }
                    }
                }

                // 4. DRAGGABLE CPS COUNTER DISPLAY
                if (hudSettings.cpsEnabled) {
                    DraggableHudElement(
                        title = "CPS",
                        offset = cpsOffset,
                        isDragging = draggingElement == "CPS Counter",
                        onDragStart = { draggingElement = "CPS Counter" },
                        onDragEnd = { draggingElement = null },
                        onDrag = { dx, dy ->
                            cpsOffset = Offset(
                                (cpsOffset.x + dx).coerceIn(4f, maxX - 100f),
                                (cpsOffset.y + dy).coerceIn(4f, maxY - 30f)
                            )
                        },
                        testTag = "draggable_hud_cps"
                    ) {
                        Text(
                            text = "CPS: 12.4 | 10.8",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                // 5. DRAGGABLE POTIONS HUD
                if (hudSettings.potionEnabled) {
                    DraggableHudElement(
                        title = "Potions",
                        offset = potionOffset,
                        isDragging = draggingElement == "Potion Effects",
                        onDragStart = { draggingElement = "Potion Effects" },
                        onDragEnd = { draggingElement = null },
                        onDrag = { dx, dy ->
                            potionOffset = Offset(
                                (potionOffset.x + dx).coerceIn(4f, maxX - 110f),
                                (potionOffset.y + dy).coerceIn(4f, maxY - 65f)
                            )
                        },
                        testTag = "draggable_hud_potions"
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            PotionHudItem(name = "Speed II", time = "03:45", color = NeonCyan)
                            PotionHudItem(name = "Strength I", time = "01:20", color = NeonCrimson)
                            PotionHudItem(name = "Fire Res", time = "06:12", color = NeonAmber)
                        }
                    }
                }

                // 6. DRAGGABLE ARMOR DURABILITY HUD
                if (hudSettings.armorEnabled) {
                    DraggableHudElement(
                        title = "Armor",
                        offset = armorOffset,
                        isDragging = draggingElement == "Armor Durability",
                        onDragStart = { draggingElement = "Armor Durability" },
                        onDragEnd = { draggingElement = null },
                        onDrag = { dx, dy ->
                            armorOffset = Offset(
                                (armorOffset.x + dx).coerceIn(4f, maxX - 100f),
                                (armorOffset.y + dy).coerceIn(4f, maxY - 60f)
                            )
                        },
                        testTag = "draggable_hud_armor"
                    ) {
                        Column {
                            ArmorDurabilityItem(item = "Helmet", percent = 92, color = NeonCyan)
                            ArmorDurabilityItem(item = "Chest", percent = 78, color = accent.primary)
                            ArmorDurabilityItem(item = "Legs", percent = 85, color = NeonCyan)
                            ArmorDurabilityItem(item = "Boots", percent = 64, color = NeonCyan)
                        }
                    }
                }

                // 7. DRAGGABLE KEYSTROKES OVERLAY
                if (hudSettings.keystrokesEnabled) {
                    DraggableHudElement(
                        title = "WASD",
                        offset = keystrokesOffset,
                        isDragging = draggingElement == "Keystrokes",
                        onDragStart = { draggingElement = "Keystrokes" },
                        onDragEnd = { draggingElement = null },
                        onDrag = { dx, dy ->
                            keystrokesOffset = Offset(
                                (keystrokesOffset.x + dx).coerceIn(4f, maxX - 90f),
                                (keystrokesOffset.y + dy).coerceIn(4f, maxY - 75f)
                            )
                        },
                        testTag = "draggable_hud_keystrokes"
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(3.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("W", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary))
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                listOf("A", "S", "D").forEach { key ->
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(3.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(key, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextPrimary))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Preset Layout Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PresetLayoutButton(
                    title = "Default",
                    onClick = { resetToDefault() },
                    modifier = Modifier.weight(1f)
                )
                PresetLayoutButton(
                    title = "Streamer",
                    onClick = { applyStreamerPreset() },
                    modifier = Modifier.weight(1f)
                )
                PresetLayoutButton(
                    title = "PvP Cluster",
                    onClick = { applyPvpClusterPreset() },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Module Toggles List
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                NeonModuleToggle(
                    title = "FPS & Target Frame Rate",
                    subtitle = "Real-time client frame rate with min/max jitter",
                    isEnabled = hudSettings.fpsEnabled,
                    icon = Icons.Outlined.Speed,
                    onToggle = { onToggleModule("fps") }
                )

                NeonModuleToggle(
                    title = "Server Latency / Ping",
                    subtitle = "Dynamic ms gauge with color status",
                    isEnabled = hudSettings.pingEnabled,
                    icon = Icons.Filled.Bolt,
                    onToggle = { onToggleModule("ping") }
                )

                NeonModuleToggle(
                    title = "Coordinates & Biome Indicator",
                    subtitle = "XYZ coordinates, pitch, yaw and compass orientation",
                    isEnabled = hudSettings.coordinatesEnabled,
                    icon = Icons.Filled.LocationOn,
                    onToggle = { onToggleModule("coords") }
                )

                NeonModuleToggle(
                    title = "CPS Counter HUD",
                    subtitle = "Live left & right click speed monitor",
                    isEnabled = hudSettings.cpsEnabled,
                    icon = Icons.Outlined.TouchApp,
                    onToggle = { onToggleModule("cps") }
                )

                NeonModuleToggle(
                    title = "Armor & Tool Durability",
                    subtitle = "Percentage durability bars with low endurance alerts",
                    isEnabled = hudSettings.armorEnabled,
                    icon = Icons.Outlined.Shield,
                    onToggle = { onToggleModule("armor") }
                )

                NeonModuleToggle(
                    title = "Potion Effects Timer",
                    subtitle = "Active buffs, debuffs, amplifiers and countdowns",
                    isEnabled = hudSettings.potionEnabled,
                    icon = Icons.Outlined.Science,
                    onToggle = { onToggleModule("potion") }
                )

                NeonModuleToggle(
                    title = "WASD Keystrokes Overlay",
                    subtitle = "Interactive touch/controller movement visualizer",
                    isEnabled = hudSettings.keystrokesEnabled,
                    icon = Icons.Filled.SportsEsports,
                    onToggle = { onToggleModule("keystrokes") }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SliderControl(
                label = "HUD Scaling: ${"%.1f".format(hudSettings.hudScale)}x",
                value = hudSettings.hudScale,
                range = 0.7f..1.4f,
                onValueChange = onScaleChanged
            )

            SliderControl(
                label = "Background Opacity: ${(hudSettings.hudOpacity * 100).toInt()}%",
                value = hudSettings.hudOpacity,
                range = 0.2f..1.0f,
                onValueChange = onOpacityChanged
            )
        }
    }
}

@Composable
fun DraggableHudElement(
    title: String,
    offset: Offset,
    isDragging: Boolean,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    testTag: String,
    content: @Composable () -> Unit
) {
    val accent = LocalAppAccentTheme.current

    Box(
        modifier = Modifier
            .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { onDragStart() },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                )
            }
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isDragging) accent.primary.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.65f)
            )
            .border(
                1.dp,
                if (isDragging) accent.primary else Color.White.copy(alpha = 0.15f),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(testTag)
    ) {
        content()
    }
}

@Composable
fun PresetLayoutButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAppAccentTheme.current
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        )
    }
}

@Composable
fun PotionHudItem(name: String, time: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$name ",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
        )
        Text(
            text = time,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = TextMuted)
        )
    }
}

@Composable
fun ArmorDurabilityItem(item: String, percent: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(3.dp)
                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(percent / 100f)
                    .height(3.dp)
                    .background(color, RoundedCornerShape(2.dp))
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$percent%",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, color = TextPrimary)
        )
    }
}

@Composable
fun ScoreItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                color = color
            )
        )
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
fun SliderControl(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    val accent = LocalAppAccentTheme.current
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = accent.primary,
                activeTrackColor = accent.primary,
                inactiveTrackColor = DarkSurfaceElevated
            )
        )
    }
}
