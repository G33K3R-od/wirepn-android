package com.wirepn.android.vpn

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wirepn.android.WirepnApplication
import kotlinx.coroutines.launch

class VpnControlReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_DISCONNECT_VPN) return
        val app = context.applicationContext as? WirepnApplication ?: return
        app.applicationScope.launch {
            app.wireGuard.disconnect()
        }
    }

    companion object {
        const val ACTION_DISCONNECT_VPN = "com.wirepn.android.action.DISCONNECT_VPN"
    }
}
