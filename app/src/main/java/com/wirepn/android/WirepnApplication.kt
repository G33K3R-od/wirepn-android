package com.wirepn.android

import android.app.Application
import com.wirepn.android.data.AppPreferences
import com.wirepn.android.data.ProfileRepository
import com.wirepn.android.data.SecureProfileStorage
import com.wirepn.android.vpn.WireGuardTunnelController

class WirepnApplication : Application() {

    lateinit var profileRepository: ProfileRepository
        private set

    lateinit var wireGuard: WireGuardTunnelController
        private set

    lateinit var appPreferences: AppPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        val storage = SecureProfileStorage(this)
        profileRepository = ProfileRepository(storage)
        wireGuard = WireGuardTunnelController(this)
        appPreferences = AppPreferences(this)
    }
}
