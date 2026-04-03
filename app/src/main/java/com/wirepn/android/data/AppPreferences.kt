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

    fun setThemePreference(value: ThemePreference) {
        prefs.edit().putString(KEY_THEME, value.name).apply()
        _themePreference.value = value
    }

    private fun loadTheme(): ThemePreference {
        val raw = prefs.getString(KEY_THEME, null) ?: return ThemePreference.SYSTEM
        return runCatching { ThemePreference.valueOf(raw) }.getOrDefault(ThemePreference.SYSTEM)
    }

    companion object {
        private const val PREFS_NAME = "wirepn_app"
        private const val KEY_THEME = "theme_preference"
    }
}
