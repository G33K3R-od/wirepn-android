package com.wirepn.android.ui

import android.app.Activity
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wirepn.android.BuildConfig
import com.wirepn.android.MainViewModel
import com.wirepn.android.R
import com.wirepn.android.ui.components.WirepnTopBar
import com.wirepn.android.ui.navigation.WirepnRoutes
import com.wirepn.android.ui.screens.ConnectScreen
import com.wirepn.android.ui.screens.LogsScreen
import com.wirepn.android.ui.screens.ProfilesScreen
import com.wirepn.android.ui.screens.SettingsScreen

@Composable
fun WirepnApp(
    viewModel: MainViewModel,
) {
    val profiles by viewModel.profiles.collectAsState()
    val activeId by viewModel.activeProfileId.collectAsState()
    val vpnState by viewModel.vpnState.collectAsState()
    val themePreference by viewModel.themePreference.collectAsState()
    val context = LocalContext.current

    val activeProfile = remember(profiles, activeId) {
        profiles.firstOrNull { it.id == activeId }
    }

    var showImportError by remember { mutableStateOf<String?>(null) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openInputStream(uri)?.use { input ->
                input.bufferedReader().use { it.readText() }
            }.orEmpty()
        }.fold(
            onSuccess = { text ->
                if (text.isBlank()) {
                    showImportError = context.getString(R.string.empty_file_error)
                } else {
                    showImportError = null
                    viewModel.addProfile(context.getString(R.string.profile_imported_default), text)
                }
            },
            onFailure = { e ->
                showImportError = e.message ?: context.getString(R.string.import_failed)
            },
        )
    }

    val vpnPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.connectActiveProfile()
        }
    }

    fun requestConnect() {
        val prepare = VpnService.prepare(context)
        if (prepare != null) {
            vpnPermissionLauncher.launch(prepare)
        } else {
            viewModel.connectActiveProfile()
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomItems = buildList {
        add(BottomItem(WirepnRoutes.CONNECT, R.string.nav_connect, Icons.Outlined.Power))
        add(BottomItem(WirepnRoutes.PROFILES, R.string.nav_profiles, Icons.Outlined.People))
        add(BottomItem(WirepnRoutes.SETTINGS, R.string.nav_settings, Icons.Outlined.Settings))
        if (BuildConfig.DEBUG) {
            add(BottomItem(WirepnRoutes.LOGS, R.string.nav_logs, Icons.Outlined.Description))
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { WirepnTopBar(vpnState = vpnState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
            ) {
                bottomItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(stringResource(item.labelRes)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = WirepnRoutes.CONNECT,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            composable(WirepnRoutes.CONNECT) {
                ConnectScreen(
                    vpnState = vpnState,
                    activeProfileName = activeProfile?.displayName,
                    onConnect = { requestConnect() },
                    onDisconnect = { viewModel.disconnect() },
                )
            }
            composable(WirepnRoutes.PROFILES) {
                ProfilesScreen(
                    profiles = profiles,
                    activeId = activeId,
                    onSelect = { viewModel.setActiveProfile(it) },
                    onDelete = { viewModel.deleteProfile(it) },
                    onImportFile = { importLauncher.launch(arrayOf("*/*")) },
                    onPasteSave = { name, text -> viewModel.addProfile(name, text) },
                )
            }
            composable(WirepnRoutes.SETTINGS) {
                SettingsScreen(
                    themePreference = themePreference,
                    onThemeChange = { viewModel.setThemePreference(it) },
                )
            }
            if (BuildConfig.DEBUG) {
                composable(WirepnRoutes.LOGS) {
                    LogsScreen()
                }
            }
        }
    }

    showImportError?.let { msg ->
        AlertDialog(
            onDismissRequest = { showImportError = null },
            confirmButton = {
                TextButton(onClick = { showImportError = null }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            title = { Text(stringResource(R.string.vpn_error)) },
            text = { Text(msg) },
        )
    }
}

private data class BottomItem(
    val route: String,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)
