package com.detox.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.detox.core.designsystem.component.LevelProgressRing
import com.detox.core.designsystem.theme.BackgroundDark
import com.detox.core.designsystem.theme.EmberPrimary
import com.detox.core.designsystem.theme.EmberSecondary
import com.detox.core.designsystem.theme.NeonAccent
import com.detox.core.designsystem.theme.SurfaceDark
import com.detox.core.designsystem.theme.SurfaceElevated
import com.detox.core.designsystem.theme.TextMuted
import com.detox.core.designsystem.theme.TextPrimary
import com.detox.core.designsystem.theme.TextSecondary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.detox.core.designsystem.theme.BackgroundDark
import com.detox.core.designsystem.theme.EmberPrimary
import com.detox.core.designsystem.theme.SurfaceDark
import com.detox.core.designsystem.theme.TextMuted
import com.detox.core.designsystem.theme.TextPrimary

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
                contentColor = TextPrimary
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text("📊", fontSize = 20.sp) },
                    label = { Text("Tableau", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmberPrimary,
                        selectedTextColor = EmberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = BackgroundDark
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text("📱", fontSize = 20.sp) },
                    label = { Text("Apps", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmberPrimary,
                        selectedTextColor = EmberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = BackgroundDark
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Text("⚙️", fontSize = 20.sp) },
                    label = { Text("Diagnostic", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = EmberPrimary,
                        selectedTextColor = EmberPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted,
                        indicatorColor = BackgroundDark
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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DETOX",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp
                    )
                    Text(
                        text = "Série de 5 jours 🔥",
                        color = EmberSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Score : 88/100",
                        color = NeonAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation / Shortcut to Diagnostic & Xiaomi Check
            val context = androidx.compose.ui.platform.LocalContext.current
            androidx.compose.material3.Button(
                onClick = {
                    com.detox.feature.blocker.BlockActivity.start(
                        context = context,
                        packageName = "Instagram (Test)",
                        cooldownSec = 15
                    )
                },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "🛡️ Tester l'Écran de Blocage",
                    color = EmberSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Central Level Ring (Canvas / Shader Glow)
            LevelProgressRing(
                level = 3,
                tierName = "Braise",
                progressRatio = 0.65f,
                currentXp = 340L,
                xpToNext = 520L
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Quick Stats Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Déverrouillages",
                    value = "24",
                    subtitle = "Quota: max 50",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Temps d'écran",
                    value = "1h 15m",
                    subtitle = "Quota: max 3h",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Top Distractions Section Preview
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp)),
                color = SurfaceElevated
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Statut Quota Déconnexion",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Actuellement validé. Aucune application verrouillée.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(16.dp)),
        color = SurfaceDark
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = TextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = EmberPrimary, fontSize = 11.sp)
        }
    }
}
