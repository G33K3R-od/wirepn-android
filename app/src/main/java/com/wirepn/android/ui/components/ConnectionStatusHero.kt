package com.wirepn.android.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.wirepn.android.ui.state.VpnDisplayState
import com.wirepn.android.ui.theme.LocalWirepnExtraColors

private val HeroSize = 220.dp
private val RingStroke = 10.dp
private val RingStrokeInner = 5.dp

/**
 * Крупный индикатор: двойное кольцо при подключении/отключении, стабильное свечение при VPN.
 */
@Composable
fun ConnectionStatusHero(
    displayState: VpnDisplayState,
    modifier: Modifier = Modifier,
) {
    val extra = LocalWirepnExtraColors.current
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val error = MaterialTheme.colorScheme.error

    val infinite = rememberInfiniteTransition(label = "hero")
    val rotationFast by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringSpin",
    )
    val rotationSlow by infinite.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "ringSpinSlow",
    )
    val breathe by infinite.animateFloat(
        initialValue = 0.96f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breathe",
    )
    val glowPulse by infinite.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow",
    )

    Box(
        modifier = modifier.size(HeroSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = RingStroke.toPx()
            val strokeInner = RingStrokeInner.toPx()
            val w = size.width
            val h = size.height
            val r = (w.coerceAtMost(h) - stroke) / 2f
            val topLeft = Offset((w - 2 * r) / 2f, (h - 2 * r) / 2f)
            val rInner = r * 0.82f
            val topLeftInner = Offset((w - 2 * rInner) / 2f, (h - 2 * rInner) / 2f)

            when (displayState) {
                is VpnDisplayState.Disconnected -> {
                    drawCircle(
                        color = extra.heroRingIdle,
                        radius = r,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }
                is VpnDisplayState.Connecting -> {
                    rotate(rotationFast) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                0f to Color.Transparent,
                                0.3f to primary.copy(alpha = 0.2f),
                                0.55f to primary,
                                0.85f to secondary.copy(alpha = 0.9f),
                                1f to Color.Transparent,
                                center = Offset(w / 2f, h / 2f),
                            ),
                            topLeft = topLeft,
                            size = Size(r * 2f, r * 2f),
                            startAngle = 0f,
                            sweepAngle = 280f,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round),
                        )
                    }
                    rotate(rotationSlow) {
                        drawArc(
                            color = secondary.copy(alpha = 0.55f),
                            topLeft = topLeftInner,
                            size = Size(rInner * 2f, rInner * 2f),
                            startAngle = 120f,
                            sweepAngle = 200f,
                            useCenter = false,
                            style = Stroke(width = strokeInner, cap = StrokeCap.Round),
                        )
                    }
                    drawCircle(
                        color = extra.heroGlow.copy(alpha = 0.1f + glowPulse * 0.1f),
                        radius = r * 0.72f,
                    )
                }
                is VpnDisplayState.Disconnecting -> {
                    rotate(-rotationFast * 0.55f) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                0f to Color.Transparent,
                                0.35f to secondary.copy(alpha = 0.45f),
                                0.65f to secondary.copy(alpha = 0.9f),
                                1f to Color.Transparent,
                                center = Offset(w / 2f, h / 2f),
                            ),
                            topLeft = topLeft,
                            size = Size(r * 2f, r * 2f),
                            startAngle = 0f,
                            sweepAngle = 260f,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round),
                        )
                    }
                    rotate(rotationSlow * 0.35f) {
                        drawArc(
                            color = primary.copy(alpha = 0.35f),
                            topLeft = topLeftInner,
                            size = Size(rInner * 2f, rInner * 2f),
                            startAngle = 200f,
                            sweepAngle = 140f,
                            useCenter = false,
                            style = Stroke(width = strokeInner, cap = StrokeCap.Round),
                        )
                    }
                    drawCircle(
                        color = secondary.copy(alpha = 0.1f + glowPulse * 0.06f),
                        radius = r * 0.68f,
                    )
                }
                is VpnDisplayState.Connected -> {
                    drawCircle(
                        color = primary.copy(alpha = 0.18f),
                        radius = r * 0.92f,
                    )
                    drawArc(
                        color = primary,
                        topLeft = topLeft,
                        size = Size(r * 2f, r * 2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                    drawCircle(
                        color = extra.heroGlow.copy(alpha = 0.12f + (breathe - 0.96f) * 0.5f),
                        radius = r * 0.55f,
                    )
                }
                is VpnDisplayState.Error -> {
                    drawCircle(
                        color = error.copy(alpha = 0.35f),
                        radius = r,
                        style = Stroke(width = stroke, cap = StrokeCap.Round),
                    )
                }
            }
        }

        val iconScale = when (displayState) {
            is VpnDisplayState.Connected -> breathe
            is VpnDisplayState.Connecting -> 0.92f + glowPulse * 0.08f
            is VpnDisplayState.Disconnecting -> 0.88f + glowPulse * 0.06f
            else -> 1f
        }
        when (displayState) {
            is VpnDisplayState.Error -> {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = error,
                )
            }
            else -> {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .scale(iconScale)
                        .alpha(
                            when (displayState) {
                                is VpnDisplayState.Disconnecting -> 0.65f
                                is VpnDisplayState.Disconnected -> 0.5f
                                else -> 1f
                            },
                        ),
                    tint = when (displayState) {
                        is VpnDisplayState.Connected -> primary
                        is VpnDisplayState.Connecting -> primary.copy(alpha = 0.95f)
                        is VpnDisplayState.Disconnecting -> secondary.copy(alpha = 0.75f)
                        else -> onSurface.copy(alpha = 0.45f)
                    },
                )
            }
        }
    }
}
