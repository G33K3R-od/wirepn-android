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
            textDim = WirepnPalette.DarkTextDim,
            dangerBg = Color(0x33FF6B6B),
            statusConnecting = WirepnPalette.StatusConnecting,
            topAccent = Color(0x5000FFC2),
            heroRingIdle = Color(0x4000FFC2),
            heroGlow = Color(0x5500FFC2),
        )
    } else {
        WirepnExtraColors(
            textDim = WirepnPalette.LightTextDim,
            dangerBg = WirepnPalette.ErrorContainerLight,
            statusConnecting = WirepnPalette.StatusConnecting,
            topAccent = Color(0x33007B83),
            heroRingIdle = Color(0x26007B83),
            heroGlow = Color(0x33007B83),
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
