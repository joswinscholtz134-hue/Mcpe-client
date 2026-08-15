package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Neon Accent Palettes
val NeonPurple = Color(0xFFA855F7)
val NeonPurpleBright = Color(0xFFC084FC)
val NeonPurpleGlow = Color(0x66A855F7)

val NeonBlue = Color(0xFF3B82F6)
val NeonBlueBright = Color(0xFF60A5FA)
val NeonBlueGlow = Color(0x663B82F6)

val NeonCyan = Color(0xFF00F0FF)
val NeonCyanBright = Color(0xFF38BDF8)
val NeonCyanGlow = Color(0x6600F0FF)

val NeonEmerald = Color(0xFF10B981)
val NeonEmeraldBright = Color(0xFF34D399)
val NeonEmeraldGlow = Color(0x6610B981)

val NeonCrimson = Color(0xFFF43F5E)
val NeonCrimsonBright = Color(0xFFFB7185)
val NeonCrimsonGlow = Color(0x66F43F5E)

val NeonFuchsia = Color(0xFFD946EF)
val NeonGreen = Color(0xFF22C55E)
val NeonAmber = Color(0xFFF59E0B)

// "Elegant Dark" OLED Backgrounds & Glass Surfaces
val DarkBg = Color(0xFF050505)
val DarkSurface = Color(0xFF0A0A0E)
val DarkSurfaceElevated = Color(0xFF111118)
val DarkSurfaceGlass = Color(0xDF0E0F17)
val DarkCardBorder = Color(0x28FFFFFF)
val DarkCardBorderGlow = Color(0x38A855F7)

// Text Colors
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Status Colors
val StatusOnline = Color(0xFF22C55E)
val StatusWarning = Color(0xFFEAB308)
val StatusOffline = Color(0xFFEF4444)

enum class AppAccentTheme(
    val title: String,
    val primary: Color,
    val secondary: Color,
    val glow: Color
) {
    PURPLE("Neon Purple", NeonPurple, NeonBlue, NeonPurpleGlow),
    CYAN("Cyber Cyan", NeonCyan, NeonPurple, NeonCyanGlow),
    EMERALD("Emerald Jade", NeonEmerald, NeonBlue, NeonEmeraldGlow),
    CRIMSON("Blood Crimson", NeonCrimson, NeonAmber, NeonCrimsonGlow)
}
