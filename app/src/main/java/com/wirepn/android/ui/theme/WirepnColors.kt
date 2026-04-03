package com.wirepn.android.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Палитра как у WirePN Desktop: тёмный navy, акцент mint `#00FFC2`, вторичный teal `#007B83`.
 * Surface-уровни — холодные сине-серые, без «нейтрального сланца» Material по умолчанию.
 */
object WirepnPalette {
    val NavyDeep = Color(0xFF0A1931)
    val NavySurface = Color(0xFF0D1424)
    val NavySurfaceElevated = Color(0xFF121E2E)
    val NavySurfaceCard = Color(0xFF152536)
    val NavySurfaceDock = Color(0xFF1A2D42)

    val TealBrand = Color(0xFF007B83)
    val MintBrand = Color(0xFF00FFC2)
    val MintMuted = Color(0xFF00C9A7)
    val TealSoft = Color(0xFF0E7490)

    val LightBackground = Color(0xFFF0F4F8)
    val LightSurface = Color(0xFFFFFFFF)
    val LightSurfaceMuted = Color(0xFFE8EEF4)
    val LightTextPrimary = Color(0xFF0A1931)
    val LightTextMuted = Color(0xFF475569)
    val LightTextDim = Color(0xFF64748B)

    val DarkTextPrimary = Color(0xFFE8F4F8)
    val DarkTextMuted = Color(0xFF94A8B8)
    val DarkTextDim = Color(0xFF6B7C8C)

    val StatusConnecting = Color(0xFFFFB020)
    val Error = Color(0xFFFF6B6B)
    val ErrorContainerLight = Color(0x1AE11D48)

    val TopAccentLine = Color(0x4000FFC2)
    val DockBorder = Color(0x2800FFC2)
}

internal val WirepnLightScheme = lightColorScheme(
    primary = WirepnPalette.TealBrand,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8EAEE),
    onPrimaryContainer = Color(0xFF003438),
    secondary = WirepnPalette.MintMuted,
    onSecondary = Color(0xFF00322A),
    tertiary = WirepnPalette.NavyDeep,
    onTertiary = Color.White,
    background = WirepnPalette.LightBackground,
    onBackground = WirepnPalette.LightTextPrimary,
    surface = WirepnPalette.LightSurface,
    onSurface = WirepnPalette.LightTextPrimary,
    surfaceVariant = WirepnPalette.LightSurfaceMuted,
    onSurfaceVariant = WirepnPalette.LightTextMuted,
    outline = Color(0x330A1931),
    outlineVariant = Color(0x140A1931),
    error = Color(0xFFE11D48),
    onError = Color.White,
    errorContainer = Color(0xFFFFE4E8),
    onErrorContainer = Color(0xFF680018),
    surfaceContainerLowest = Color(0xFFF8FAFC),
    surfaceContainerLow = Color(0xFFF1F5F9),
    surfaceContainer = Color(0xFFE8EEF4),
    surfaceContainerHigh = Color(0xFFE2E8F0),
    surfaceContainerHighest = Color(0xFFCBD5E1),
)

internal val WirepnDarkScheme = darkColorScheme(
    primary = WirepnPalette.MintBrand,
    onPrimary = WirepnPalette.NavyDeep,
    primaryContainer = Color(0xFF004D4A),
    onPrimaryContainer = Color(0xFFB0FFF0),
    secondary = WirepnPalette.TealBrand,
    onSecondary = Color.White,
    tertiary = WirepnPalette.TealSoft,
    onTertiary = Color.White,
    background = WirepnPalette.NavyDeep,
    onBackground = WirepnPalette.DarkTextPrimary,
    surface = WirepnPalette.NavySurface,
    onSurface = WirepnPalette.DarkTextPrimary,
    surfaceVariant = WirepnPalette.NavySurfaceElevated,
    onSurfaceVariant = WirepnPalette.DarkTextMuted,
    outline = Color(0x3300FFC2),
    outlineVariant = Color(0x1800FFC2),
    error = WirepnPalette.Error,
    onError = WirepnPalette.NavyDeep,
    errorContainer = Color(0x33FF6B6B),
    onErrorContainer = Color(0xFFFFDAD6),
    surfaceContainerLowest = Color(0xFF060D18),
    surfaceContainerLow = WirepnPalette.NavyDeep,
    surfaceContainer = WirepnPalette.NavySurfaceElevated,
    surfaceContainerHigh = WirepnPalette.NavySurfaceCard,
    surfaceContainerHighest = WirepnPalette.NavySurfaceDock,
)

@Immutable
data class WirepnExtraColors(
    val textDim: Color,
    val dangerBg: Color,
    val statusConnecting: Color,
    val topAccent: Color,
    val heroRingIdle: Color,
    val heroGlow: Color,
)

val LocalWirepnExtraColors = staticCompositionLocalOf {
    WirepnExtraColors(
        textDim = WirepnPalette.LightTextDim,
        dangerBg = WirepnPalette.ErrorContainerLight,
        statusConnecting = WirepnPalette.StatusConnecting,
        topAccent = WirepnPalette.TopAccentLine,
        heroRingIdle = Color(0x33007B83),
        heroGlow = Color(0x4000FFC2),
    )
}
