package com.wirepn.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wirepn.android.data.AppPreferences
import com.wirepn.android.data.ProfileRepository
import com.wirepn.android.data.SplitTunnelMode
import com.wirepn.android.data.ThemePreference
import com.wirepn.android.vpn.TunnelNaming
import com.wirepn.android.vpn.WireGuardTunnelController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.wirepn.android.vpn.VpnConnectionState

class MainViewModel(
    private val repository: ProfileRepository,
    private val wireGuard: WireGuardTunnelController,
    private val appPreferences: AppPreferences,
) : ViewModel() {

    val profiles = repository.profiles
    val activeProfileId = repository.activeProfileId
    val vpnState = wireGuard.state
    val themePreference = appPreferences.themePreference
    val splitTunnelMode = appPreferences.splitTunnelMode
    val splitTunnelAppPackages = appPreferences.splitTunnelAppPackages

    private val _externalConnectSignal = MutableStateFlow(0)
    val externalConnectSignal: StateFlow<Int> = _externalConnectSignal.asStateFlow()

    private var splitTunnelReconnectJob: Job? = null

    fun requestConnectFromExternal() {
        _externalConnectSignal.value = _externalConnectSignal.value + 1
    }

    fun setSplitTunnelMode(mode: SplitTunnelMode) {
        appPreferences.setSplitTunnelMode(mode)
        scheduleReconnectIfVpnConnected()
    }

    fun setSplitTunnelAppPackages(packages: Set<String>) {
        appPreferences.setSplitTunnelAppPackages(packages)
        scheduleReconnectIfVpnConnected()
    }

    /** Re-applies the active profile so split-tunnel changes take effect without a manual reconnect. */
    private fun scheduleReconnectIfVpnConnected() {
        splitTunnelReconnectJob?.cancel()
        splitTunnelReconnectJob = viewModelScope.launch {
            delay(500)
            if (vpnState.value !is VpnConnectionState.Connected) return@launch
            val id = repository.activeProfileId.value ?: return@launch
            val profile = repository.profileById(id) ?: return@launch
            val name = TunnelNaming.fromDisplayName(profile.displayName, profile.id)
            wireGuard.connect(profile.configText, name)
        }
    }

    suspend fun queryVpnLockdown(): Pair<Boolean, Boolean> = wireGuard.queryVpnLockdown()

    fun setThemePreference(preference: ThemePreference) {
        appPreferences.setThemePreference(preference)
    }

    fun setActiveProfile(id: String?) {
        repository.setActiveProfile(id)
    }

    fun deleteProfile(id: String) {
        viewModelScope.launch {
            if (repository.activeProfileId.value == id) {
                wireGuard.disconnect()
            }
            repository.deleteProfile(id)
        }
    }

    fun connectActiveProfile() {
        viewModelScope.launch {
            val id = repository.activeProfileId.value ?: return@launch
            val profile = repository.profileById(id) ?: return@launch
            val name = TunnelNaming.fromDisplayName(profile.displayName, profile.id)
            wireGuard.connect(profile.configText, name)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            wireGuard.disconnect()
        }
    }

    fun addProfile(displayName: String, configText: String) {
        repository.addProfile(displayName, configText)
    }
}

class MainViewModelFactory(
    private val repository: ProfileRepository,
    private val wireGuard: WireGuardTunnelController,
    private val appPreferences: AppPreferences,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass != MainViewModel::class.java) {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(repository, wireGuard, appPreferences) as T
    }
}
