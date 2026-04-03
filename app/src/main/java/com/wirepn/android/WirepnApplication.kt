package com.wirepn.android

import android.app.Application
import com.wirepn.android.data.AppPreferences
import com.wirepn.android.data.ProfileRepository
import com.wirepn.android.data.SecureProfileStorage
import com.wirepn.android.vpn.VpnNotificationManager
import com.wirepn.android.vpn.WireGuardTunnelController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class WirepnApplication : Application() {

    lateinit var profileRepository: ProfileRepository
        private set

    lateinit var wireGuard: WireGuardTunnelController
        private set

    lateinit var appPreferences: AppPreferences
        private set

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        val storage = SecureProfileStorage(this)
        profileRepository = ProfileRepository(storage)
        appPreferences = AppPreferences(this)
        wireGuard = WireGuardTunnelController(this, appPreferences)
        VpnNotificationManager.createChannel(this)
        applicationScope.launch {
            combine(
                wireGuard.state,
                profileRepository.activeProfileId,
                profileRepository.profiles,
            ) { state, activeId, profs ->
                val name = activeId?.let { id -> profs.firstOrNull { it.id == id }?.displayName }
                Pair(state, name)
            }.collect { (state, name) ->
                VpnNotificationManager.update(this@WirepnApplication, state, name)
            }
        }
    }
}
