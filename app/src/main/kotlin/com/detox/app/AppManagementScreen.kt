package com.detox.app

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.detox.core.designsystem.theme.BackgroundDark
import com.detox.core.designsystem.theme.DangerRed
import com.detox.core.designsystem.theme.EmberPrimary
import com.detox.core.designsystem.theme.EmberSecondary
import com.detox.core.designsystem.theme.NeonAccent
import com.detox.core.designsystem.theme.SurfaceDark
import com.detox.core.designsystem.theme.SurfaceElevated
import com.detox.core.designsystem.theme.TextMuted
import com.detox.core.designsystem.theme.TextPrimary
import com.detox.core.designsystem.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledAppItem(
    val name: String,
    val packageName: String,
    val isBlocked: Boolean = false,
    val isEssential: Boolean = false
)

@Composable
fun AppManagementScreen() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val appsList = remember { mutableStateListOf<InstalledAppItem>() }
    val blockedPackages = remember {
        mutableStateListOf(
            "com.instagram.android",
            "com.zhiliaoapp.musically",
            "com.google.android.youtube",
            "com.twitter.android",
            "com.facebook.katana"
        )
    }

    LaunchedEffect(Unit) {
        val loadedApps = withContext(Dispatchers.IO) {
            getInstalledApps(context, blockedPackages)
        }
        appsList.clear()
        appsList.addAll(loadedApps)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Text(
                text = "GESTION DES APPLICATIONS",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choisissez quelles applications restreindre quand le quota n'est pas rempli.",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher une application...", color = TextMuted, fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmberPrimary,
                    unfocusedBorderColor = SurfaceElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val filteredApps = appsList.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.packageName.contains(searchQuery, ignoreCase = true)
            }

            // Apps List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredApps, key = { it.packageName }) { appItem ->
                    AppRow(
                        item = appItem,
                        isBlocked = blockedPackages.contains(appItem.packageName),
                        onToggle = { isChecked ->
                            if (isChecked) {
                                if (!blockedPackages.contains(appItem.packageName)) {
                                    blockedPackages.add(appItem.packageName)
                                }
                            } else {
                                blockedPackages.remove(appItem.packageName)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppRow(
    item: InstalledAppItem,
    isBlocked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
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
                // Colored letter badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (isBlocked) EmberPrimary.copy(alpha = 0.2f) else SurfaceElevated)
                ) {
                    Text(
                        text = item.name.take(1).uppercase(),
                        color = if (isBlocked) EmberPrimary else NeonAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = item.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = if (item.isEssential) "Essentielle (Jamais bloquée)" else item.packageName,
                        color = if (item.isEssential) EmberSecondary else TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            if (!item.isEssential) {
                Switch(
                    checked = isBlocked,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = EmberPrimary,
                        checkedTrackColor = EmberPrimary.copy(alpha = 0.4f),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceElevated
                    )
                )
            }
        }
    }
}

private fun getInstalledApps(context: Context, blockedList: List<String>): List<InstalledAppItem> {
    val pm = context.packageManager
    val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

    val essentialList = setOf(
        "com.android.dialer",
        "com.google.android.dialer",
        "com.android.settings",
        "com.android.emergency",
        context.packageName
    )

    // Pre-populate standard common apps if on emulator/no third-party apps found
    val standardCommonApps = listOf(
        InstalledAppItem("Instagram", "com.instagram.android"),
        InstalledAppItem("TikTok", "com.zhiliaoapp.musically"),
        InstalledAppItem("YouTube", "com.google.android.youtube"),
        InstalledAppItem("WhatsApp", "com.whatsapp"),
        InstalledAppItem("Twitter / X", "com.twitter.android"),
        InstalledAppItem("Snapchat", "com.snapchat.android"),
        InstalledAppItem("Facebook", "com.facebook.katana"),
        InstalledAppItem("Téléphone", "com.android.dialer", isEssential = true),
        InstalledAppItem("Paramètres", "com.android.settings", isEssential = true)
    )

    val realApps = packages.filter { app ->
        (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0 || essentialList.contains(app.packageName)
    }.map { app ->
        val name = pm.getApplicationLabel(app).toString()
        val isEssential = essentialList.contains(app.packageName)
        InstalledAppItem(
            name = name,
            packageName = app.packageName,
            isEssential = isEssential
        )
    }

    return if (realApps.size > 2) {
        realApps.sortedWith(compareBy({ !it.isEssential }, { it.name }))
    } else {
        standardCommonApps
    }
}
