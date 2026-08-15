package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.NavTab
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LocalAppAccentTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun ClientBottomNav(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = LocalAppAccentTheme.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(24.dp, RoundedCornerShape(26.dp), spotColor = accent.primary.copy(alpha = 0.25f))
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(
                            accent.primary.copy(alpha = 0.4f),
                            DarkCardBorder.copy(alpha = 0.5f)
                        )
                    )
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .clip(RoundedCornerShape(26.dp)),
        color = DarkSurface.copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.65f),
                    label = "scale"
                )
                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) accent.primary else TextMuted,
                    label = "tint"
                )

                val (filledIcon, outlinedIcon) = when (tab) {
                    NavTab.HOME -> Icons.Filled.Home to Icons.Outlined.Home
                    NavTab.MODS -> Icons.Filled.Extension to Icons.Outlined.Extension
                    NavTab.PVP -> Icons.Filled.SportsEsports to Icons.Outlined.SportsEsports
                    NavTab.SERVERS -> Icons.Filled.Dns to Icons.Outlined.Dns
                    NavTab.SETTINGS -> Icons.Filled.Settings to Icons.Outlined.Settings
                }

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (isSelected) accent.primary.copy(alpha = 0.15f) else Color.Transparent
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = accent.primary),
                            onClick = { onTabSelected(tab) }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("nav_tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) filledIcon else outlinedIcon,
                            contentDescription = tab.title,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accent.primary else TextMuted
                            )
                        )
                    }
                }
            }
        }
    }
}
