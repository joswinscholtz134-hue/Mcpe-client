package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardBorderGlow
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceGlass
import com.example.ui.theme.LocalAppAccentTheme
import com.example.ui.theme.LocalPowerSaveMode
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    borderColor: Color = DarkCardBorder,
    glowColor: Color = LocalAppAccentTheme.current.glow.copy(alpha = 0.15f),
    backgroundColor: Color = DarkSurfaceGlass,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(color = LocalAppAccentTheme.current.primary),
            onClick = onClick
        )
    } else Modifier

    Surface(
        modifier = modifier
            .shadow(elevation = 12.dp, shape = shape, spotColor = glowColor, ambientColor = glowColor)
            .border(
                border = BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        colors = listOf(
                            borderColor.copy(alpha = 0.8f),
                            borderColor.copy(alpha = 0.2f)
                        )
                    )
                ),
                shape = shape
            )
            .clip(shape)
            .then(clickModifier),
        shape = shape,
        color = backgroundColor,
        tonalElevation = 4.dp
    ) {
        content()
    }
}

@Composable
fun NeonPlayButton(
    text: String = "LAUNCH MINECRAFT",
    subtext: String = "PLAY BEDROCK EDITION",
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accent = LocalAppAccentTheme.current
    val isPowerSave = LocalPowerSaveMode.current
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val animatedGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )
    val glowAlpha = if (isPowerSave) 0.3f else animatedGlowAlpha

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = accent.primary.copy(alpha = glowAlpha),
                ambientColor = accent.secondary.copy(alpha = glowAlpha)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        accent.primary,
                        accent.secondary
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White),
                onClick = onClick
            )
            .testTag("launch_minecraft_button"),
        contentAlignment = Alignment.Center
    ) {
        // Inner glowing border effect
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(2.dp)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(18.dp)
                )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = Color.Black
                    )
                )
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.75f)
                    )
                )
            }
        }
    }
}

@Composable
fun NeonBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalAppAccentTheme.current.primary,
    textColor: Color = Color.White
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.18f), shape = RoundedCornerShape(8.dp))
            .border(0.8.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@Composable
fun NeonModuleToggle(
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    val accent = LocalAppAccentTheme.current
    val borderColor by animateColorAsState(
        targetValue = if (isEnabled) accent.primary.copy(alpha = 0.6f) else DarkCardBorder,
        label = "border"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isEnabled) accent.primary.copy(alpha = 0.12f) else DarkSurfaceElevated.copy(alpha = 0.7f),
        label = "bg"
    )

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = borderColor,
        backgroundColor = bgColor,
        onClick = onToggle
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (isEnabled) accent.primary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.05f),
                            CircleShape
                        )
                        .border(
                            0.8.dp,
                            if (isEnabled) accent.primary.copy(alpha = 0.6f) else Color.Transparent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isEnabled) accent.primary else TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isEnabled) TextPrimary else TextSecondary
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = accent.primary,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = DarkSurfaceElevated
                )
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    val accent = LocalAppAccentTheme.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(18.dp)
                    .background(
                        brush = Brush.verticalGradient(listOf(accent.primary, accent.secondary)),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        color = TextPrimary
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        }
        if (action != null) {
            action()
        }
    }
}
