package com.detox.app

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledAppItem(
    val name: String,
    val packageName: String,
    val isEssential: Boolean = false,
    val iconDrawable: Drawable? = null
)

@Composable
fun AppManagementScreen() {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val appsList = remember { mutableStateListOf<InstalledAppItem>() }
    
    // Stored preferences for selected blocked apps
    val prefs = remember { context.getSharedPreferences("detox_blocked_apps", Context.MODE_PRIVATE) }
    val blockedPackages = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        val savedSet = prefs.getStringSet("blocked_set", setOf(
            "com.instagram.android",
            "com.zhiliaoapp.musically",
            "com.google.android.youtube",
            "com.twitter.android",
            "com.facebook.katana"
        )) ?: emptySet()
        blockedPackages.clear()
        blockedPackages.addAll(savedSet)

        val loadedApps = withContext(Dispatchers.IO) {
            getInstalledApps(context)
        }
        appsList.clear()
        appsList.addAll(loadedApps)
        isLoading = false
    }

    fun saveBlockedList() {
        prefs.edit().putStringSet("blocked_set", blockedPackages.toSet()).apply()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundDark
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FILTRES & RESTRICTIONS",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "${blockedPackages.size} applications dans la liste de blocage",
                        color = EmberSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = EmberCore.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, EmberPrimary.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "ACTIF",
                        color = EmberSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Horizontal Visual Bar: Currently Blocked Apps Avatars
            val activeBlockedApps = appsList.filter { blockedPackages.contains(it.packageName) }
            if (activeBlockedApps.isNotEmpty()) {
                Text(
                    text = "APPS VERROUILLÉES EN CAS DE DÉPASSEMENT :",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(activeBlockedApps, key = { it.packageName }) { appItem ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable {
                                blockedPackages.remove(appItem.packageName)
                                saveBlockedList()
                            }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(SurfaceDark)
                                    .border(1.5.dp, EmberPrimary, RoundedCornerShape(14.dp))
                            ) {
                                if (appItem.iconDrawable != null) {
                                    Image(
                                        bitmap = appItem.iconDrawable.toBitmap(96, 96).asImageBitmap(),
                                        contentDescription = appItem.name,
                                        modifier = Modifier.size(34.dp)
                                    )
                                } else {
                                    Text(
                                        text = appItem.name.take(1).uppercase(),
                                        color = EmberSecondary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = appItem.name.take(8),
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Search Bar with glow border
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher dans vos applications...", color = TextMuted, fontSize = 13.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                ),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = EmberPrimary)
                }
            } else {
                val filteredApps = appsList.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.packageName.contains(searchQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredApps, key = { it.packageName }) { appItem ->
                        val isBlocked = blockedPackages.contains(appItem.packageName)
                        AppRow(
                            item = appItem,
                            isBlocked = isBlocked,
                            onToggle = { isChecked ->
                                if (isChecked) {
                                    if (!blockedPackages.contains(appItem.packageName)) {
                                        blockedPackages.add(appItem.packageName)
                                    }
                                } else {
                                    blockedPackages.remove(appItem.packageName)
                                }
                                saveBlockedList()
                            }
                        )
                    }
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
    val borderColor = if (isBlocked) EmberPrimary.copy(alpha = 0.5f) else CardBorder

    Surface(
        color = SurfaceDark,
        border = BorderStroke(1.dp, borderColor),
        shape = RoundedCornerShape(16.dp),
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isBlocked) {
                                Brush.linearGradient(listOf(EmberCore.copy(alpha = 0.3f), EmberPrimary.copy(alpha = 0.15f)))
                            } else {
                                Brush.linearGradient(listOf(SurfaceElevated, SurfaceDark))
                            }
                        )
                        .border(
                            1.dp,
                            if (isBlocked) EmberPrimary.copy(alpha = 0.6f) else CardBorder,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    if (item.iconDrawable != null) {
                        Image(
                            bitmap = item.iconDrawable.toBitmap(96, 96).asImageBitmap(),
                            contentDescription = item.name,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Text(
                            text = item.name.take(1).uppercase(),
                            color = if (isBlocked) EmberSecondary else NeonCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = item.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (item.isEssential) "Protégée (Système vital)" else item.packageName,
                        color = if (item.isEssential) NeonViolet else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (item.isEssential) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }

            if (!item.isEssential) {
                Switch(
                    checked = isBlocked,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TextPrimary,
                        checkedTrackColor = EmberPrimary,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceElevated
                    )
                )
            }
        }
    }
}

private fun getInstalledApps(context: Context): List<InstalledAppItem> {
    val pm = context.packageManager
    
    // Explicit Launcher Intent query (Guaranteed to return all user-facing installed apps)
    val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val resolveInfos = pm.queryIntentActivities(mainIntent, 0)

    val essentialList = setOf(
        "com.android.dialer",
        "com.google.android.dialer",
        "com.android.settings",
        "com.android.emergency",
        context.packageName
    )

    val items = mutableListOf<InstalledAppItem>()
    val seenPackages = mutableSetOf<String>()

    for (info in resolveInfos) {
        val pkg = info.activityInfo.packageName
        if (seenPackages.contains(pkg)) continue
        seenPackages.add(pkg)

        val name = info.loadLabel(pm).toString()
        val icon = try { info.loadIcon(pm) } catch (e: Exception) { null }
        val isEssential = essentialList.contains(pkg)

        items.add(
            InstalledAppItem(
                name = name,
                packageName = pkg,
                isEssential = isEssential,
                iconDrawable = icon
            )
        )
    }

    if (items.isEmpty()) {
        // Fallback for emulators/edge cases
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (app in packages) {
            val pkg = app.packageName
            val isEssential = essentialList.contains(pkg)
            items.add(
                InstalledAppItem(
                    name = pm.getApplicationLabel(app).toString(),
                    packageName = pkg,
                    isEssential = isEssential,
                    iconDrawable = try { pm.getApplicationIcon(app) } catch (e: Exception) { null }
                )
            )
        }
    }

    return items.sortedWith(compareBy({ !it.isEssential }, { it.name }))
}
