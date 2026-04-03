package com.wirepn.android.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Brand tokens aligned with WirePN desktop (`app.css`). */
object WirepnPalette {
    val LightBackgroundBase = Color(0xFFF1F5F9)
    val LightSurface = Color(0xFFFFFFFF)
    val LightTextPrimary = Color(0xFF0F172A)
    val LightTextMuted = Color(0xFF64748B)
    val LightTextDim = Color(0xFF94A3B8)
    val LightBorder = Color(0x170F172A)
    val LightAccent = Color(0xFF16A34A)
    val LightAccentTint = Color(0x2416A34A)
    val LightAccentText = Color(0xFF14532D)
    val LightDanger = Color(0xFFE11D48)
    val LightDangerBg = Color(0x1AF43F5E)

    val DarkBackgroundBase = Color(0xFF0B1220)
    val DarkSurface = Color(0xFF111827)
    val DarkTextPrimary = Color(0xFFE2E8F0)
    val DarkTextMuted = Color(0xFF94A3B8)
    val DarkAccent = Color(0xFF22C55E)
    val DarkAccentText = Color(0xFFDCFCE7)

    val StatusConnecting = Color(0xFFD97706)
    val TopFlourishTint = Color(0x1216A34A)
}

internal val WirepnLightScheme = lightColorScheme(
    primary = WirepnPalette.LightAccent,
    onPrimary = Color.White,
    primaryContainer = WirepnPalette.LightAccentTint,
    onPrimaryContainer = WirepnPalette.LightAccentText,
    secondary = WirepnPalette.LightTextMuted,
    onSecondary = Color.White,
    background = WirepnPalette.LightBackgroundBase,
    onBackground = WirepnPalette.LightTextPrimary,
    surface = WirepnPalette.LightSurface,
    onSurface = WirepnPalette.LightTextPrimary,
    surfaceVariant = Color(0xFFF8FAFC),
    onSurfaceVariant = WirepnPalette.LightTextMuted,
    outline = WirepnPalette.LightBorder,
    outlineVariant = WirepnPalette.LightBorder,
    error = WirepnPalette.LightDanger,
    onError = Color.White,
    errorContainer = WirepnPalette.LightDangerBg,
    onErrorContainer = WirepnPalette.LightDanger,
)

internal val WirepnDarkScheme = darkColorScheme(
    primary = WirepnPalette.DarkAccent,
    onPrimary = Color(0xFF052E16),
    primaryContainer = Color(0xFF14532D),
    onPrimaryContainer = WirepnPalette.DarkAccentText,
    secondary = WirepnPalette.DarkTextMuted,
    onSecondary = Color.White,
    background = WirepnPalette.DarkBackgroundBase,
    onBackground = WirepnPalette.DarkTextPrimary,
    surface = WirepnPalette.DarkSurface,
    onSurface = WirepnPalette.DarkTextPrimary,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = WirepnPalette.DarkTextMuted,
    outline = Color(0x33E2E8F0),
    outlineVariant = Color(0x22E2E8F0),
    error = WirepnPalette.LightDanger,
    onError = Color.White,
    errorContainer = Color(0x33F43F5E),
    onErrorContainer = Color(0xFFFFB4AB),
)

@Immutable
data class WirepnExtraColors(
    val textDim: Color,
    val dangerBg: Color,
    val statusConnecting: Color,
    val topFlourish: Color,
)

val LocalWirepnExtraColors = staticCompositionLocalOf {
    WirepnExtraColors(
        textDim = WirepnPalette.LightTextDim,
        dangerBg = WirepnPalette.LightDangerBg,
        statusConnecting = WirepnPalette.StatusConnecting,
        topFlourish = WirepnPalette.TopFlourishTint,
    )
}
