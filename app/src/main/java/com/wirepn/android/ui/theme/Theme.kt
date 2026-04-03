package com.wirepn.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.wirepn.android.data.ThemePreference

@Composable
fun WirepnTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themePreference) {
        ThemePreference.SYSTEM -> systemDark
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }
    val extraColors = if (darkTheme) {
        WirepnExtraColors(
            textDim = WirepnPalette.DarkTextMuted,
            dangerBg = Color(0x33F43F5E),
            statusConnecting = WirepnPalette.StatusConnecting,
            topFlourish = Color(0x1822C55E),
        )
    } else {
        WirepnExtraColors(
            textDim = WirepnPalette.LightTextDim,
            dangerBg = WirepnPalette.LightDangerBg,
            statusConnecting = WirepnPalette.StatusConnecting,
            topFlourish = WirepnPalette.TopFlourishTint,
        )
    }
    val colorScheme = if (darkTheme) WirepnDarkScheme else WirepnLightScheme

    CompositionLocalProvider(LocalWirepnExtraColors provides extraColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = WirepnTypography,
            shapes = WirepnShapes,
            content = content,
        )
    }
}
