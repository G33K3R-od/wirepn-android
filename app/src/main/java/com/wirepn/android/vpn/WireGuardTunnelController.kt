package com.wirepn.android.vpn

import android.content.Context
import com.wireguard.android.backend.BackendException
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.BadConfigException
import com.wireguard.config.Config
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream

sealed class VpnConnectionState {
    data object Disconnected : VpnConnectionState()
    data object Connecting : VpnConnectionState()
    data object Connected : VpnConnectionState()
    data class Error(val message: String) : VpnConnectionState()
}

class WireGuardTunnelController(
    context: Context,
) {
    private val backend = GoBackend(context.applicationContext)

    private val _state = MutableStateFlow<VpnConnectionState>(VpnConnectionState.Disconnected)
    val state: StateFlow<VpnConnectionState> = _state.asStateFlow()

    private var activeTunnel: WireTunnel? = null

    suspend fun connect(configText: String, tunnelName: String) = withContext(Dispatchers.IO) {
        _state.value = VpnConnectionState.Connecting
        val config = try {
            Config.parse(ByteArrayInputStream(configText.toByteArray(Charsets.UTF_8)))
        } catch (e: BadConfigException) {
            _state.value = VpnConnectionState.Error(e.message ?: "Bad config")
            return@withContext
        } catch (e: Exception) {
            _state.value = VpnConnectionState.Error(e.message ?: "Config error")
            return@withContext
        }

        try {
            disconnectInternal()
            val tunnel = WireTunnel(tunnelName) { newState ->
                _state.value = when (newState) {
                    Tunnel.State.UP -> VpnConnectionState.Connected
                    Tunnel.State.DOWN -> VpnConnectionState.Disconnected
                    Tunnel.State.TOGGLE -> VpnConnectionState.Disconnected
                }
            }
            activeTunnel = tunnel
            backend.setState(tunnel, Tunnel.State.UP, config)
        } catch (e: BackendException) {
            activeTunnel = null
            _state.value = VpnConnectionState.Error(backendMessage(e))
        } catch (e: Exception) {
            activeTunnel = null
            _state.value = VpnConnectionState.Error(e.message ?: "VPN error")
        }
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        disconnectInternal()
        _state.value = VpnConnectionState.Disconnected
    }

    private suspend fun disconnectInternal() = withContext(Dispatchers.IO) {
        val tunnel = activeTunnel ?: return@withContext
        try {
            backend.setState(tunnel, Tunnel.State.DOWN, null)
        } catch (_: Exception) {
        } finally {
            activeTunnel = null
        }
    }

    private fun backendMessage(e: BackendException): String {
        return when (e.reason) {
            BackendException.Reason.VPN_NOT_AUTHORIZED -> "VPN permission required"
            BackendException.Reason.UNABLE_TO_START_VPN -> "Unable to start VPN service"
            BackendException.Reason.TUN_CREATION_ERROR -> "TUN creation failed"
            BackendException.Reason.DNS_RESOLUTION_FAILURE ->
                e.format.firstOrNull()?.toString()?.let { "DNS failed: $it" } ?: "DNS resolution failed"
            BackendException.Reason.TUNNEL_MISSING_CONFIG -> "Missing tunnel config"
            BackendException.Reason.GO_ACTIVATION_ERROR_CODE ->
                "WireGuard error (${e.format.firstOrNull() ?: "unknown"})"
            BackendException.Reason.WG_QUICK_CONFIG_ERROR_CODE -> "Invalid WireGuard settings"
            BackendException.Reason.UNKNOWN_KERNEL_MODULE_NAME -> "WireGuard error"
        }
    }

    private class WireTunnel(
        private val tunnelName: String,
        private val onState: (Tunnel.State) -> Unit,
    ) : Tunnel {
        override fun getName(): String = tunnelName
        override fun onStateChange(newState: Tunnel.State) {
            onState(newState)
        }
    }
}
