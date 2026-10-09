package com.detox.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.detox.core.designsystem.component.LevelProgressRing
import com.detox.core.designsystem.theme.BackgroundDark
import com.detox.core.designsystem.theme.CardBorder
import com.detox.core.designsystem.theme.DangerRed
import com.detox.core.designsystem.theme.EmberCore
import com.detox.core.designsystem.theme.EmberPrimary
import com.detox.core.designsystem.theme.EmberSecondary
import com.detox.core.designsystem.theme.FlameGradient
import com.detox.core.designsystem.theme.NeonCyan
import com.detox.core.designsystem.theme.NeonViolet
import com.detox.core.designsystem.theme.SuccessGreen
import com.detox.core.designsystem.theme.SurfaceDark
import com.detox.core.designsystem.theme.SurfaceElevated
import com.detox.core.designsystem.theme.TextMuted
import com.detox.core.designsystem.theme.TextPrimary
import com.detox.core.designsystem.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContainerScreen()
        }
    }
}

@Composable
fun MainContainerScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceDark,
                tonalElevation = 12.dp,
                modifier = Modifier.border(BorderStroke(1.dp, CardBorder))
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("⚡", fontSize = 20.sp) },
                    label = { Text("Noyau", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonCyan,
                        selectedTextColor = NeonCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SurfaceElevated
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("🛡️", fontSize = 20.sp) },
                    label = { Text("Apps", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmberPrimary,
                        selectedTextColor = EmberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SurfaceElevated
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text("⚙️", fontSize = 20.sp) },
                    label = { Text("Système", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonViolet,
                        selectedTextColor = NeonViolet,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = SurfaceElevated
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen()
                1 -> AppManagementScreen()
                2 -> DiagnosticScreen()
            }
        }
    }
}

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("detox_runtime_state", Context.MODE_PRIVATE) }

    // Persistent State for Today's Stats & Quotas
    var unlocksCount by remember { mutableIntStateOf(prefs.getInt("current_unlocks", 24)) }
    var screenMinutes by remember { mutableIntStateOf(prefs.getInt("current_screen_min", 75)) }
    var maxUnlocksQuota by remember { mutableIntStateOf(prefs.getInt("max_unlocks_quota", 50)) }
    var maxScreenMinutesQuota by remember { mutableIntStateOf(prefs.getInt("max_screen_quota", 180)) }
    var totalXp by remember { mutableIntStateOf(prefs.getInt("total_xp", 340)) }

    // Dialog state for custom quota setter
    var showCustomDialog by remember { mutableStateOf(false) }

    fun persistStats() {
        prefs.edit()
            .putInt("current_unlocks", unlocksCount)
            .putInt("current_screen_min", screenMinutes)
            .putInt("max_unlocks_quota", maxUnlocksQuota)
            .putInt("max_screen_quota", maxScreenMinutesQuota)
            .putInt("total_xp", totalXp)
            .apply()
    }

    // Dynamic Score Calculation
    val unlocksPenalty = unlocksCount * 0.5
    val screenPenalty = screenMinutes * 0.2
    val quotaMet = unlocksCount <= maxUnlocksQuota && screenMinutes <= maxScreenMinutesQuota
    val calculatedScore = (100.0 - unlocksPenalty - screenPenalty + (if (quotaMet) 10.0 else 0.0))
        .toInt().coerceIn(0, 100)

    val currentLevel = (1 + (totalXp / 250)).coerceAtLeast(1)
    val xpInLevel = totalXp % 250
    val levelRatio = xpInLevel.toFloat() / 250f

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (quotaMet) SuccessGreen else DangerRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DETOX PROTOCOL",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }
                    Text(
                        text = "Reset quotidien à 00:00",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                // Streak Flame Badge
                Surface(
                    color = EmberCore.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, EmberPrimary.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🔥", fontSize = 15.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "5 JOURS",
                            color = EmberSecondary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Central Level Ring (Glowing Neon Pulse)
            LevelProgressRing(
                level = currentLevel,
                tierName = when {
                    currentLevel < 5 -> "Braise"
                    currentLevel < 10 -> "Flamme"
                    currentLevel < 20 -> "Aurore"
                    else -> "Nébuleuse"
                },
                progressRatio = levelRatio,
                currentXp = xpInLevel.toLong(),
                xpToNext = 250L
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Control Buttons : Reset à zéro & Configurer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bouton Remise à Zéro
                Button(
                    onClick = {
                        unlocksCount = 0
                        screenMinutes = 0
                        totalXp += 20 // Bonus de reset / discipline
                        persistStats()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                    border = BorderStroke(1.dp, DangerRed.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "↺ Remise à Zéro",
                        color = DangerRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bouton Configurer Soi-Même
                Button(
                    onClick = { showCustomDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                    border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "✏️ Définir Quotas",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Score Banner Card
            Surface(
                color = SurfaceDark,
                border = BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "INDICE DU JOUR",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$calculatedScore / 100",
                            color = if (calculatedScore >= 60) NeonCyan else DangerRed,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Surface(
                        color = if (quotaMet) SuccessGreen.copy(alpha = 0.15f) else DangerRed.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = if (quotaMet) "QUOTA RESPECTÉ" else "DÉPASSEMENT",
                            color = if (quotaMet) SuccessGreen else DangerRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-Time Quota Progress Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MetricCard(
                    title = "DÉVERROUILLAGES",
                    value = "$unlocksCount",
                    maxValue = "max $maxUnlocksQuota",
                    ratio = unlocksCount.toFloat() / maxUnlocksQuota.toFloat(),
                    accentColor = NeonCyan,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "TEMPS D'ÉCRAN",
                    value = "${screenMinutes / 60}h ${screenMinutes % 60}m",
                    maxValue = "max ${maxScreenMinutesQuota / 60}h",
                    ratio = screenMinutes.toFloat() / maxScreenMinutesQuota.toFloat(),
                    accentColor = EmberPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Interactive Simulator Button
            Button(
                onClick = {
                    com.detox.feature.blocker.BlockActivity.start(
                        context = context,
                        packageName = "Instagram",
                        cooldownSec = 15
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                border = BorderStroke(1.dp, EmberPrimary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(text = "🛡️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tester l'Écran de Respiration Immédiat",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal Dialog to Set Custom Values
    if (showCustomDialog) {
        var tempUnlocks by remember { mutableFloatStateOf(maxUnlocksQuota.toFloat()) }
        var tempScreenHours by remember { mutableFloatStateOf((maxScreenMinutesQuota / 60f)) }

        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            containerColor = SurfaceDark,
            title = {
                Text(
                    text = "CONFIGURER VOS OBJECTIFS",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column {
                    Text(
                        text = "Max Déverrouillages : ${tempUnlocks.toInt()}",
                        color = NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = tempUnlocks,
                        onValueChange = { tempUnlocks = it },
                        valueRange = 10f..150f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Temps d'écran max : ${String.format("%.1f", tempScreenHours)}h",
                        color = EmberSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = tempScreenHours,
                        onValueChange = { tempScreenHours = it },
                        valueRange = 0.5f..8f,
                        colors = SliderDefaults.colors(
                            thumbColor = EmberPrimary,
                            activeTrackColor = EmberPrimary
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        maxUnlocksQuota = tempUnlocks.toInt()
                        maxScreenMinutesQuota = (tempScreenHours * 60).toInt()
                        persistStats()
                        showCustomDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmberPrimary)
                ) {
                    Text("Enregistrer", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showCustomDialog = false }) {
                    Text("Annuler", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    maxValue: String,
    ratio: Float,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = maxValue,
                color = TextMuted,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { ratio.coerceIn(0f, 1f) },
                color = accentColor,
                trackColor = SurfaceElevated,
                strokeCap = StrokeCap.Round,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
        }
    }
}
