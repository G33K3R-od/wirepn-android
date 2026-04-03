package com.wirepn.android.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class ProfileRepository(
    private val storage: SecureProfileStorage,
) {
    private val _profiles = MutableStateFlow(storage.loadProfiles())
    val profiles: StateFlow<List<WireProfile>> = _profiles.asStateFlow()

    private val _activeId = MutableStateFlow(storage.loadActiveProfileId())
    val activeProfileId: StateFlow<String?> = _activeId.asStateFlow()

    fun addProfile(displayName: String, configText: String): WireProfile {
        val profile = WireProfile(
            id = UUID.randomUUID().toString(),
            displayName = displayName.ifBlank { "Profile" },
            configText = configText.trim(),
            createdAtEpochMs = System.currentTimeMillis(),
        )
        _profiles.update { it + profile }
        if (_activeId.value == null) {
            _activeId.value = profile.id
            storage.saveActiveProfileId(profile.id)
        }
        persist()
        return profile
    }

    fun deleteProfile(id: String) {
        _profiles.update { list -> list.filterNot { it.id == id } }
        _activeId.update { current -> if (current == id) null else current }
        persist()
        storage.saveActiveProfileId(_activeId.value)
    }

    fun setActiveProfile(id: String?) {
        _activeId.value = id
        storage.saveActiveProfileId(id)
    }

    fun profileById(id: String): WireProfile? = _profiles.value.firstOrNull { it.id == id }

    private fun persist() {
        storage.saveProfiles(_profiles.value)
    }
}
