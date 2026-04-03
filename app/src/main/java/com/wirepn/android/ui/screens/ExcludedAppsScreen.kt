package com.wirepn.android.ui.screens

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.wirepn.android.MainViewModel
import com.wirepn.android.R
import com.wirepn.android.data.SplitTunnelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

private data class AppListItem(
    val packageName: String,
    val label: String,
    val isSystemApp: Boolean,
)

@SuppressLint("QueryPermissionsNeeded")
private fun loadInstalledAppMetadata(context: android.content.Context): List<AppListItem> {
    val pm = context.packageManager
    val self = context.packageName
    val installed = pm.getInstalledApplications(PackageManager.GET_META_DATA)
    return installed.mapNotNull { appInfo ->
        if (appInfo.packageName == self) return@mapNotNull null
        val label = runCatching { pm.getApplicationLabel(appInfo).toString().trim() }
            .getOrElse { appInfo.packageName }
        if (label.isEmpty()) return@mapNotNull null
        val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        AppListItem(appInfo.packageName, label, isSystem)
    }.distinctBy { it.packageName }
        .sortedWith(
            compareBy<AppListItem> { it.isSystemApp }.thenBy { it.label.lowercase() },
        )
}

private fun drawableToIconBitmap(drawable: Drawable, maxPx: Int): Bitmap {
    val w = drawable.intrinsicWidth.takeIf { it > 0 } ?: maxPx
    val h = drawable.intrinsicHeight.takeIf { it > 0 } ?: maxPx
    val scale = minOf(maxPx.toFloat() / w, maxPx.toFloat() / h, 1f)
    val tw = (w * scale).roundToInt().coerceAtLeast(1)
    val th = (h * scale).roundToInt().coerceAtLeast(1)
    return drawable.toBitmap(tw, th)
}

@SuppressLint("QueryPermissionsNeeded")
private suspend fun loadAppIconBitmap(
    pm: PackageManager,
    packageName: String,
    maxPx: Int,
): Bitmap? = withContext(Dispatchers.IO) {
    runCatching {
        val info = pm.getApplicationInfo(packageName, 0)
        val dr = pm.getApplicationIcon(info)
        drawableToIconBitmap(dr, maxPx)
    }.getOrNull()
}

@Composable
fun ExcludedAppsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val appContext = remember { context.applicationContext }
    val mode by viewModel.splitTunnelMode.collectAsState()
    val selected by viewModel.splitTunnelAppPackages.collectAsState()
    var apps by remember { mutableStateOf<List<AppListItem>>(emptyList()) }
    var listLoading by remember { mutableStateOf(true) }
    var query by remember { mutableStateOf("") }

    LaunchedEffect(appContext) {
        listLoading = true
        val list = withContext(Dispatchers.Default) {
            loadInstalledAppMetadata(appContext)
        }
        apps = list
        listLoading = false
    }

    val filtered = remember(apps, query) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) apps
        else apps.filter {
            it.label.lowercase().contains(q) || it.packageName.lowercase().contains(q)
        }
    }

    val subtitle = when (mode) {
        SplitTunnelMode.EXCLUDE_APPS -> stringResource(R.string.split_tunnel_subtitle_exclude)
        SplitTunnelMode.INCLUDE_APPS -> stringResource(R.string.split_tunnel_subtitle_include)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = mode == SplitTunnelMode.EXCLUDE_APPS,
                onClick = { viewModel.setSplitTunnelMode(SplitTunnelMode.EXCLUDE_APPS) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
            ) {
                Text(
                    text = stringResource(R.string.split_tunnel_mode_exclude),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            SegmentedButton(
                selected = mode == SplitTunnelMode.INCLUDE_APPS,
                onClick = { viewModel.setSplitTunnelMode(SplitTunnelMode.INCLUDE_APPS) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
            ) {
                Text(
                    text = stringResource(R.string.split_tunnel_mode_include),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(stringResource(R.string.split_tunnel_search_hint)) },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            enabled = !listLoading,
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (listLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (filtered.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.split_tunnel_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 24.dp),
                        )
                    }
                }
                items(filtered, key = { it.packageName }) { app ->
                    AppRoutingRow(
                        packageName = app.packageName,
                        label = app.label,
                        checked = selected.contains(app.packageName),
                        onToggle = { checked ->
                            val next = if (checked) {
                                selected + app.packageName
                            } else {
                                selected - app.packageName
                            }
                            viewModel.setSplitTunnelAppPackages(next)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppRoutingRow(
    packageName: String,
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val pm = remember { context.applicationContext.packageManager }
    val maxIconPx = remember {
        (48f * context.resources.displayMetrics.density).roundToInt().coerceIn(64, 160)
    }
    var iconBitmap by remember(packageName) { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(packageName) {
        iconBitmap = loadAppIconBitmap(pm, packageName, maxIconPx)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .clickable { onToggle(!checked) }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Android,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Checkbox(
                checked = checked,
                onCheckedChange = null,
            )
        }
    }
}
