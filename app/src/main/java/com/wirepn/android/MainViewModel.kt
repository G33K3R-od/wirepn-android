package com.wirepn.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.wirepn.android.data.AppPreferences
import com.wirepn.android.data.ProfileRepository
import com.wirepn.android.data.ThemePreference
import com.wirepn.android.vpn.TunnelNaming
import com.wirepn.android.vpn.WireGuardTunnelController
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ProfileRepository,
    private val wireGuard: WireGuardTunnelController,
    private val appPreferences: AppPreferences,
) : ViewModel() {

    val profiles = repository.profiles
    val activeProfileId = repository.activeProfileId
    val vpnState = wireGuard.state
    val themePreference = appPreferences.themePreference

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
