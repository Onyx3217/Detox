package com.detox.core.designsystem.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.detox.core.designsystem.theme.EmberCore
import com.detox.core.designsystem.theme.EmberPrimary
import com.detox.core.designsystem.theme.EmberSecondary
import com.detox.core.designsystem.theme.GlowRingGradient
import com.detox.core.designsystem.theme.NeonCyan
import com.detox.core.designsystem.theme.NeonViolet
import com.detox.core.designsystem.theme.SurfaceDark
import com.detox.core.designsystem.theme.SurfaceElevated
import com.detox.core.designsystem.theme.TextMuted
import com.detox.core.designsystem.theme.TextPrimary
import com.detox.core.designsystem.theme.TextSecondary

@Composable
fun LevelProgressRing(
    level: Int,
    tierName: String,
    progressRatio: Float,
    currentXp: Long,
    xpToNext: Long,
    modifier: Modifier = Modifier
) {
    // Pulse animation for breathing neon halo
    val infiniteTransition = rememberInfiniteTransition(label = "RingPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = progressRatio.coerceIn(0.02f, 1f),
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label = "LevelProgressRingAnimation"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(260.dp)
    ) {
        // Deep background glow circle
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            EmberCore.copy(alpha = pulseAlpha * 0.4f),
                            NeonViolet.copy(alpha = pulseAlpha * 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Canvas(modifier = Modifier.size(240.dp)) {
            val strokeWidth = 14.dp.toPx()
            val glowWidth = 24.dp.toPx()
            val arcSize = size.width - glowWidth
            val topLeft = Offset(glowWidth / 2, glowWidth / 2)

            // Outer dark track
            drawArc(
                color = SurfaceElevated.copy(alpha = 0.9f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Ambient Glow Underlay
            drawArc(
                brush = GlowRingGradient,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                alpha = pulseAlpha,
                style = Stroke(width = glowWidth, cap = StrokeCap.Round)
            )

            // Sharp Foreground Arc
            drawArc(
                brush = GlowRingGradient,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = Size(arcSize, arcSize),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Center Cyber Badge
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                color = EmberCore.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "RANG ${tierName.uppercase()}",
                    color = EmberSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$level",
                color = TextPrimary,
                fontSize = 58.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )

            Text(
                text = "${(animatedProgress * 100).toInt()}% VERS LE NIV. ${level + 1}",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$currentXp / $xpToNext XP",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
