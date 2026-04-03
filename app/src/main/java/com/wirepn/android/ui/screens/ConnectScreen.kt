package com.wirepn.android.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wirepn.android.R
import com.wirepn.android.ui.components.ConnectionStatusHero
import com.wirepn.android.ui.state.VpnDisplayState

private val WirepnMotionEasing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
private val WirepnMotionExitEasing = CubicBezierEasing(0.4f, 0f, 1f, 1f)
private val AnimEaseIn = tween<Float>(340, easing = WirepnMotionEasing)
private val AnimEaseOut = tween<Float>(240, easing = WirepnMotionExitEasing)
private val TitleFadeIn = tween<Float>(280, easing = WirepnMotionEasing)
private val TitleFadeOut = tween<Float>(200, easing = WirepnMotionExitEasing)

@Composable
fun ConnectScreen(
    vpnDisplay: VpnDisplayState,
    activeProfileName: String?,
    hasActiveProfile: Boolean,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onOpenProfiles: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val profileLine = activeProfileName?.takeIf { it.isNotBlank() }
        ?: stringResource(R.string.none_selected)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(0.08f))

            AnimatedContent(
                targetState = vpnDisplay,
                transitionSpec = {
                    (
                        fadeIn(AnimEaseIn) + scaleIn(
                            initialScale = 0.97f,
                            animationSpec = AnimEaseIn,
                        )
                        ).togetherWith(
                        fadeOut(AnimEaseOut) + scaleOut(
                            targetScale = 1.02f,
                            animationSpec = AnimEaseOut,
                        ),
                    )
                },
                label = "hero",
            ) { state ->
                ConnectionStatusHero(displayState = state)
            }

            Spacer(modifier = Modifier.height(28.dp))

            AnimatedContent(
                targetState = vpnDisplay,
                transitionSpec = {
                    fadeIn(TitleFadeIn).togetherWith(fadeOut(TitleFadeOut))
                },
                label = "statusTitle",
            ) { state ->
                val title = when (state) {
                    is VpnDisplayState.Connected -> stringResource(R.string.status_connected)
                    is VpnDisplayState.Connecting -> stringResource(R.string.status_connecting)
                    is VpnDisplayState.Disconnecting -> stringResource(R.string.status_disconnecting)
                    is VpnDisplayState.Disconnected -> stringResource(R.string.status_disconnected)
                    is VpnDisplayState.Error -> stringResource(R.string.status_error)
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.connect_active_profile),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = profileLine,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                when (vpnDisplay) {
                    is VpnDisplayState.Connecting -> {
                        Button(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            ),
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(R.string.status_connecting))
                        }
                    }
                    is VpnDisplayState.Disconnecting -> {
                        OutlinedButton(
                            onClick = {},
                            enabled = false,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(R.string.status_disconnecting))
                        }
                    }
                    is VpnDisplayState.Connected -> {
                        OutlinedButton(
                            onClick = onDisconnect,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                        ) {
                            Text(
                                stringResource(R.string.disconnect),
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                    is VpnDisplayState.Error -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                            ) {
                                Text(
                                    text = vpnDisplay.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(16.dp),
                                )
                            }
                            Button(
                                onClick = onConnect,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Text(stringResource(R.string.connect_retry))
                            }
                        }
                    }
                    is VpnDisplayState.Disconnected -> {
                        if (!hasActiveProfile) {
                            FilledTonalButton(
                                onClick = onOpenProfiles,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Text(stringResource(R.string.connect_choose_profile))
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        } else {
                            Button(
                                onClick = onConnect,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Text(
                                    stringResource(R.string.connect),
                                    style = MaterialTheme.typography.titleSmall,
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(0.15f))
        }
    }
}
