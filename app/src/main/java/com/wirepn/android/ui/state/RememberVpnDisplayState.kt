package com.wirepn.android.ui.state

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.wirepn.android.vpn.VpnConnectionState
import kotlinx.coroutines.delay

private const val DefaultMinConnectingMs = 1_450L
private const val DefaultMinDisconnectingMs = 800L

/**
 * Сглаживает слишком быстрые переходы бэкенда: минимум времени на фазу «Подключение…» и
 * отдельная фаза «Отключение…» при разрыве VPN.
 */
@Composable
fun rememberVpnDisplayState(
    raw: VpnConnectionState,
    minConnectingMs: Long = DefaultMinConnectingMs,
    minDisconnectingMs: Long = DefaultMinDisconnectingMs,
): MutableState<VpnDisplayState> {
    val displayed = remember {
        mutableStateOf<VpnDisplayState>(mapRawToDisplay(raw))
    }
    var connectingStartedAt by remember { mutableLongStateOf(0L) }
    var previousRaw by remember { mutableStateOf<VpnConnectionState?>(null) }

    LaunchedEffect(raw) {
        val prev = previousRaw
        try {
            when (raw) {
                is VpnConnectionState.Connecting -> {
                    connectingStartedAt = SystemClock.elapsedRealtime()
                    displayed.value = VpnDisplayState.Connecting
                }
                is VpnConnectionState.Connected -> {
                    if (displayed.value is VpnDisplayState.Connecting) {
                        val elapsed = SystemClock.elapsedRealtime() - connectingStartedAt
                        delay((minConnectingMs - elapsed).coerceAtLeast(0))
                    }
                    displayed.value = VpnDisplayState.Connected
                }
                is VpnConnectionState.Error -> {
                    displayed.value = VpnDisplayState.Error(raw.message)
                }
                is VpnConnectionState.Disconnected -> {
                    when (prev) {
                        is VpnConnectionState.Connected -> {
                            displayed.value = VpnDisplayState.Disconnecting
                            delay(minDisconnectingMs)
                        }
                        else -> Unit
                    }
                    displayed.value = VpnDisplayState.Disconnected
                }
            }
        } finally {
            previousRaw = raw
        }
    }

    return displayed
}

private fun mapRawToDisplay(raw: VpnConnectionState): VpnDisplayState = when (raw) {
    is VpnConnectionState.Connected -> VpnDisplayState.Connected
    is VpnConnectionState.Connecting -> VpnDisplayState.Connecting
    is VpnConnectionState.Error -> VpnDisplayState.Error(raw.message)
    is VpnConnectionState.Disconnected -> VpnDisplayState.Disconnected
}
