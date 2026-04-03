package com.wirepn.android.vpn

import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.wirepn.android.MainActivity
import com.wirepn.android.R
import com.wirepn.android.WirepnApplication
import com.wirepn.android.vpn.TunnelNaming
import com.wirepn.android.vpn.VpnConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WirepnVpnTileService : TileService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var listenJob: Job? = null

    override fun onStartListening() {
        super.onStartListening()
        val app = applicationContext as WirepnApplication
        listenJob?.cancel()
        listenJob = scope.launch {
            app.wireGuard.state.collect { refreshTile() }
        }
        refreshTile()
    }

    override fun onStopListening() {
        listenJob?.cancel()
        listenJob = null
        super.onStopListening()
    }

    private fun refreshTile() {
        val tile = qsTile ?: return
        val app = applicationContext as WirepnApplication
        val vpnState = app.wireGuard.state.value
        tile.label = getString(R.string.qs_tile_label)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.subtitle = null
        }
        tile.state = when (vpnState) {
            is VpnConnectionState.Connected,
            is VpnConnectionState.Connecting,
            -> Tile.STATE_ACTIVE
            is VpnConnectionState.Disconnected,
            is VpnConnectionState.Error,
            -> Tile.STATE_INACTIVE
        }
        tile.updateTile()
    }

    override fun onClick() {
        val app = applicationContext as WirepnApplication
        scope.launch {
            when (val state = app.wireGuard.state.value) {
                is VpnConnectionState.Connected,
                is VpnConnectionState.Connecting,
                -> app.wireGuard.disconnect()
                is VpnConnectionState.Disconnected,
                is VpnConnectionState.Error,
                -> {
                    val id = app.profileRepository.activeProfileId.value
                    if (id == null) {
                        launchMain()
                        return@launch
                    }
                    val profile = app.profileRepository.profileById(id)
                    if (profile == null) {
                        launchMain()
                        return@launch
                    }
                    val prepare = VpnService.prepare(this@WirepnVpnTileService)
                    if (prepare != null) {
                        val intent = Intent(this@WirepnVpnTileService, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            putExtra(MainActivity.EXTRA_CONNECT_VPN, true)
                        }
                        @Suppress("DEPRECATION")
                        startActivityAndCollapse(intent)
                    } else {
                        val name = TunnelNaming.fromDisplayName(profile.displayName, profile.id)
                        app.wireGuard.connect(profile.configText, name)
                    }
                }
            }
        }
    }

    private fun launchMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        @Suppress("DEPRECATION")
        startActivityAndCollapse(intent)
    }
}
