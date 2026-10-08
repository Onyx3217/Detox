package com.detox.app

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.detox.core.designsystem.theme.BackgroundDark
import com.detox.core.designsystem.theme.DangerRed
import com.detox.core.designsystem.theme.EmberPrimary
import com.detox.core.designsystem.theme.EmberSecondary
import com.detox.core.designsystem.theme.NeonAccent
import com.detox.core.designsystem.theme.SuccessGreen
import com.detox.core.designsystem.theme.SurfaceDark
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "DIAGNOSTIC & FIABILITÉ",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Assurez-vous que le système ne tue pas Detox en arrière-plan.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Standard Permissions
            PermissionCard(
                title = "Données d'utilisation (UsageStats)",
                isGranted = status.hasUsageStatsPermission,
                description = "Permet de mesurer le temps passé par application.",
                onAction = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermissionCard(
                title = "Exemption de batterie Android",
                isGranted = status.isBatteryOptimizationIgnored,
                description = "Évite que le système n'endorme le service de tracking.",
                onAction = {
                    context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            PermissionCard(
                title = "Service d'Accessibilité",
                isGranted = false,
                description = "Nécessaire pour intercepter et bloquer les applications interdites.",
                onAction = {
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }
            )

            // Xiaomi Specific Section
            if (status.isXiaomiDevice) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "OPTIMISATIONS XIAOMI (MIUI / HyperOS)",
                    color = EmberSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                OemActionCard(
                    title = "1. Paramètres restreints (Sideload / APK)",
                    instruction = "Obligatoire sur Android 13+ si installé par APK : Allez dans Infos appli > appuyez sur '⋮' en haut à droite > 'Autoriser les paramètres restreints'.",
                    buttonText = "Ouvrir Infos Appli",
                    onClick = {
                        context.startActivity(XiaomiHelper.getAppDetailsIntent(context))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OemActionCard(
                    title = "2. Démarrage automatique (Autostart)",
                    instruction = "Activez le démarrage automatique pour relancer Detox après un redémarrage.",
                    buttonText = "Ouvrir Autostart MIUI",
                    onClick = {
                        context.startActivity(XiaomiHelper.getAutostartIntent(context))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                OemActionCard(
                    title = "3. Économiseur de batterie MIUI",
                    instruction = "Réglez l'économiseur de batterie MIUI sur 'Pas de restrictions'.",
                    buttonText = "Gérer la batterie MIUI",
                    onClick = {
                        context.startActivity(XiaomiHelper.getBatteryOptimizationIntent(context))
                    }
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    isGranted: Boolean,
    description: String,
    onAction: () -> Unit
) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    text = if (isGranted) "ACTIF" else "INACTIF",
                    color = if (isGranted) SuccessGreen else DangerRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = description, color = TextSecondary, fontSize = 12.sp)
            if (!isGranted) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onAction,
                    colors = ButtonDefaults.buttonColors(containerColor = EmberPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Activer", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun OemActionCard(
    title: String,
    instruction: String,
    buttonText: String,
    onClick: () -> Unit
) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = NeonAccent, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = instruction, color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = buttonText, color = TextPrimary, fontSize = 12.sp)
            }
        }
    }
}
