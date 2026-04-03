package com.wirepn.android.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK,
}

class AppPreferences(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _themePreference = MutableStateFlow(loadTheme())
    val themePreference: StateFlow<ThemePreference> = _themePreference.asStateFlow()

    private val _splitTunnelMode = MutableStateFlow(loadSplitTunnelMode())
    val splitTunnelMode: StateFlow<SplitTunnelMode> = _splitTunnelMode.asStateFlow()

    private val _splitTunnelAppPackages = MutableStateFlow(loadSplitTunnelPackages())
    val splitTunnelAppPackages: StateFlow<Set<String>> = _splitTunnelAppPackages.asStateFlow()

    fun setThemePreference(value: ThemePreference) {
        prefs.edit().putString(KEY_THEME, value.name).apply()
        _themePreference.value = value
    }

    fun setSplitTunnelMode(value: SplitTunnelMode) {
        prefs.edit().putString(KEY_SPLIT_TUNNEL_MODE, value.name).apply()
        _splitTunnelMode.value = value
    }

    fun setSplitTunnelAppPackages(packages: Set<String>) {
        prefs.edit()
            .putStringSet(KEY_SPLIT_TUNNEL_APPS, packages)
            .remove(KEY_EXCLUDED_APPS_LEGACY)
            .apply()
        _splitTunnelAppPackages.value = packages
    }

    private fun loadTheme(): ThemePreference {
        val raw = prefs.getString(KEY_THEME, null) ?: return ThemePreference.SYSTEM
        return runCatching { ThemePreference.valueOf(raw) }.getOrDefault(ThemePreference.SYSTEM)
    }

    private fun loadSplitTunnelMode(): SplitTunnelMode {
        val raw = prefs.getString(KEY_SPLIT_TUNNEL_MODE, null) ?: return SplitTunnelMode.EXCLUDE_APPS
        return runCatching { SplitTunnelMode.valueOf(raw) }.getOrDefault(SplitTunnelMode.EXCLUDE_APPS)
    }

    private fun loadSplitTunnelPackages(): Set<String> {
        val raw = prefs.getStringSet(KEY_SPLIT_TUNNEL_APPS, null)
            ?: prefs.getStringSet(KEY_EXCLUDED_APPS_LEGACY, null)
            ?: return emptySet()
        return raw.mapNotNull { it?.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    companion object {
        private const val PREFS_NAME = "wirepn_app"
        private const val KEY_THEME = "theme_preference"
        private const val KEY_SPLIT_TUNNEL_MODE = "split_tunnel_mode"
        private const val KEY_SPLIT_TUNNEL_APPS = "split_tunnel_app_packages"
        /** Older key; migrated in [loadSplitTunnelPackages]. */
        private const val KEY_EXCLUDED_APPS_LEGACY = "excluded_app_packages"
    }
}
