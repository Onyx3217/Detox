package com.detox.core.designsystem.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Fond d'espace profond OLED
val BackgroundDark = Color(0xFF06070B)
val SurfaceDark = Color(0xFF0F111A)
val SurfaceElevated = Color(0xFF171A27)
val CardBorder = Color(0xFF262A3D)

// Accents Néon & Braise incandescente
val EmberCore = Color(0xFFFF2A00)
val EmberPrimary = Color(0xFFFF5E00)
val EmberSecondary = Color(0xFFFFAA00)
val NeonCyan = Color(0xFF00F0FF)
val NeonViolet = Color(0xFFB026FF)

// Textes
val TextPrimary = Color(0xFFF6F8FD)
val TextSecondary = Color(0xFF8E95AA)
val TextMuted = Color(0xFF51586F)

val SuccessGreen = Color(0xFF00FF88)
val DangerRed = Color(0xFFFF1744)

// Dégradés vivants
val FlameGradient = Brush.horizontalGradient(
    listOf(EmberCore, EmberPrimary, EmberSecondary)
)

val GlowRingGradient = Brush.sweepGradient(
    listOf(EmberCore, EmberSecondary, NeonCyan, NeonViolet, EmberCore)
)

val CardGlowGradient = Brush.verticalGradient(
    listOf(SurfaceElevated, SurfaceDark)
)
