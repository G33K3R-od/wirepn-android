package com.wirepn.android.ui

import android.app.Activity
import android.net.VpnService
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Article
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import com.wirepn.android.BuildConfig
import com.wirepn.android.MainViewModel
import com.wirepn.android.R
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wirepn.android.ui.components.WirepnBottomBar
import com.wirepn.android.ui.components.WirepnNavTab
import com.wirepn.android.ui.components.WirepnTopBar
import com.wirepn.android.ui.navigation.WirepnRoutes
import com.wirepn.android.ui.screens.ConnectScreen
import com.wirepn.android.ui.screens.LogsScreen
import com.wirepn.android.ui.screens.ProfilesScreen
import com.wirepn.android.ui.screens.SettingsScreen
import com.wirepn.android.ui.state.rememberVpnDisplayState

private val TabMotionEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
private const val TabEnterMs = 320
private const val TabExitMs = 280

private fun tabOrder(): List<String> = buildList {
    add(WirepnRoutes.CONNECT)
    add(WirepnRoutes.PROFILES)
    add(WirepnRoutes.SETTINGS)
    if (BuildConfig.DEBUG) add(WirepnRoutes.LOGS)
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabEnter() =
    slideInHorizontally(
        initialOffsetX = {
            val order = tabOrder()
            val i = order.indexOf(initialState.destination.route)
            val t = order.indexOf(targetState.destination.route)
            val forward = (if (i >= 0) i else 0) < (if (t >= 0) t else 0)
            if (forward) it else -it
        },
        animationSpec = tween(TabEnterMs, easing = TabMotionEasing),
    ) + fadeIn(tween(TabEnterMs, easing = TabMotionEasing))

private fun AnimatedContentTransitionScope<NavBackStackEntry>.tabExit() =
    slideOutHorizontally(
        targetOffsetX = {
            val order = tabOrder()
            val i = order.indexOf(initialState.destination.route)
            val t = order.indexOf(targetState.destination.route)
            val forward = (if (i >= 0) i else 0) < (if (t >= 0) t else 0)
            if (forward) -it / 3 else it / 3
        },
        animationSpec = tween(TabExitMs, easing = TabMotionEasing),
    ) + fadeOut(tween(TabExitMs, easing = TabMotionEasing))

@Composable
fun WirepnApp(
    viewModel: MainViewModel,
) {
    val profiles by viewModel.profiles.collectAsState()
    val activeId by viewModel.activeProfileId.collectAsState()
    val vpnState by viewModel.vpnState.collectAsState()
    val vpnDisplay by rememberVpnDisplayState(vpnState)
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

    val bottomTabs = buildList {
        add(
            WirepnNavTab(
                route = WirepnRoutes.CONNECT,
                labelRes = R.string.nav_tab_connect,
                icon = Icons.Rounded.Shield,
            ),
        )
        add(
            WirepnNavTab(
                route = WirepnRoutes.PROFILES,
                labelRes = R.string.nav_tab_profiles,
                icon = Icons.Rounded.Group,
            ),
        )
        add(
            WirepnNavTab(
                route = WirepnRoutes.SETTINGS,
                labelRes = R.string.nav_tab_settings,
                icon = Icons.Rounded.Settings,
            ),
        )
        if (BuildConfig.DEBUG) {
            add(
                WirepnNavTab(
                    route = WirepnRoutes.LOGS,
                    labelRes = R.string.nav_tab_logs,
                    icon = Icons.AutoMirrored.Rounded.Article,
                ),
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { WirepnTopBar() },
        bottomBar = {
            WirepnBottomBar(
                navController = navController,
                tabs = bottomTabs,
                currentDestination = currentDestination,
            )
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = WirepnRoutes.CONNECT,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            composable(
                route = WirepnRoutes.CONNECT,
                enterTransition = { tabEnter() },
                exitTransition = { tabExit() },
                popEnterTransition = { tabEnter() },
                popExitTransition = { tabExit() },
            ) {
                ConnectScreen(
                    vpnDisplay = vpnDisplay,
                    activeProfileName = activeProfile?.displayName,
                    hasActiveProfile = activeProfile != null,
                    onConnect = { requestConnect() },
                    onDisconnect = { viewModel.disconnect() },
                    onOpenProfiles = {
                        navController.navigate(WirepnRoutes.PROFILES) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
            composable(
                route = WirepnRoutes.PROFILES,
                enterTransition = { tabEnter() },
                exitTransition = { tabExit() },
                popEnterTransition = { tabEnter() },
                popExitTransition = { tabExit() },
            ) {
                ProfilesScreen(
                    profiles = profiles,
                    activeId = activeId,
                    onSelect = { viewModel.setActiveProfile(it) },
                    onDelete = { viewModel.deleteProfile(it) },
                    onImportFile = { importLauncher.launch(arrayOf("*/*")) },
                    onPasteSave = { name, text -> viewModel.addProfile(name, text) },
                )
            }
            composable(
                route = WirepnRoutes.SETTINGS,
                enterTransition = { tabEnter() },
                exitTransition = { tabExit() },
                popEnterTransition = { tabEnter() },
                popExitTransition = { tabExit() },
            ) {
                SettingsScreen(
                    themePreference = themePreference,
                    onThemeChange = { viewModel.setThemePreference(it) },
                )
            }
            if (BuildConfig.DEBUG) {
                composable(
                    route = WirepnRoutes.LOGS,
                    enterTransition = { tabEnter() },
                    exitTransition = { tabExit() },
                    popEnterTransition = { tabEnter() },
                    popExitTransition = { tabExit() },
                ) {
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
