package com.detox.app

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.detox.core.designsystem.theme.BackgroundDark
import com.detox.core.designsystem.theme.CardBorder
import com.detox.core.designsystem.theme.DangerRed
import com.detox.core.designsystem.theme.EmberCore
import com.detox.core.designsystem.theme.EmberPrimary
import com.detox.core.designsystem.theme.EmberSecondary
import com.detox.core.designsystem.theme.NeonCyan
import com.detox.core.designsystem.theme.NeonViolet
import com.detox.core.designsystem.theme.SuccessGreen
import com.detox.core.designsystem.theme.SurfaceDark
import com.detox.core.designsystem.theme.SurfaceElevated
import com.detox.core.designsystem.theme.TextMuted
import com.detox.core.designsystem.theme.TextPrimary
import com.detox.core.designsystem.theme.TextSecondary
import com.detox.core.system.oem.DiagnosticManager
import com.detox.core.system.oem.XiaomiHelper

@Composable
fun DiagnosticScreen() {
    val context = LocalContext.current
    val manager = DiagnosticManager(context)
    val status = manager.checkStatus()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AUDIT SYSTÈME & SANTÉ",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = if (status.isXiaomiDevice) "Appareil détecté : Xiaomi / HyperOS" else "Appareil Android standard",
                        color = if (status.isXiaomiDevice) EmberSecondary else NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = SurfaceDark,
                    border = BorderStroke(1.dp, CardBorder),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "DIAGNOSTIC",
                        color = NeonViolet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Standard Permissions
            PermissionCard(
                title = "Accès aux Données d'Utilisation",
                isGranted = status.hasUsageStatsPermission,
                description = "Requis pour compter le temps exact passé par application.",
                badgeText = "UsageStats",
                onAction = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            PermissionCard(
                title = "Exemption Batterie Android",
                isGranted = status.isBatteryOptimizationIgnored,
                description = "Empêche le système de couper le tracking quand l'écran est éteint.",
                badgeText = "No Doze",
                onAction = {
                    context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            PermissionCard(
                title = "Service d'Accessibilité",
                isGranted = status.isAccessibilityEnabled,
                description = "Indispensable pour intercepter les ouvertures d'applications non autorisées.",
                badgeText = "Accessibility",
                onAction = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            )

            // Xiaomi Specific Section
            if (status.isXiaomiDevice) {
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "OPTIMISATIONS MIUI / HYPEROS",
                    color = EmberSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OemActionCard(
                    number = "1",
                    title = "Paramètres Restreints (Sideload APK)",
                    instruction = "Obligatoire sur Android 13+ : Ouvrez la page Infos appli, cliquez sur '⋮' en haut à droite puis 'Autoriser les paramètres restreints'.",
                    buttonText = "Ouvrir Infos Appli",
                    onClick = {
                        context.startActivity(XiaomiHelper.getAppDetailsIntent(context))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OemActionCard(
                    number = "2",
                    title = "Démarrage Automatique (Autostart)",
                    instruction = "Permet à Detox de se relancer instantanément si le système redémarre.",
                    buttonText = "Gérer Autostart MIUI",
                    onClick = {
                        context.startActivity(XiaomiHelper.getAutostartIntent(context))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OemActionCard(
                    number = "3",
                    title = "Économiseur de Batterie MIUI",
                    instruction = "Positionnez sur 'Pas de restrictions' pour garantir la fluidité du tracking.",
                    buttonText = "Régler Batterie MIUI",
                    onClick = {
                        context.startActivity(XiaomiHelper.getBatteryOptimizationIntent(context))
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    isGranted: Boolean,
    description: String,
    badgeText: String,
    onAction: () -> Unit
) {
    Surface(
        color = SurfaceDark,
        border = BorderStroke(1.dp, if (isGranted) SuccessGreen.copy(alpha = 0.4f) else CardBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
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
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isGranted) SuccessGreen else DangerRed)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Surface(
                    color = if (isGranted) SuccessGreen.copy(alpha = 0.15f) else DangerRed.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isGranted) "ACTIF" else "REQUIS",
                        color = if (isGranted) SuccessGreen else DangerRed,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description, color = TextSecondary, fontSize = 12.sp)

            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = EmberPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Activer la permission", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun OemActionCard(
    number: String,
    title: String,
    instruction: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceDark,
        border = BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = EmberCore.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = number,
                        color = EmberSecondary,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = instruction, color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                border = BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = buttonText, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
