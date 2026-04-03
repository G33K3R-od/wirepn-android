package com.wirepn.android.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SecureProfileStorage(context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREFS_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun loadProfiles(): List<WireProfile> {
        val raw = prefs.getString(KEY_PROFILES, null) ?: return emptyList()
        return runCatching { json.decodeFromString<List<WireProfile>>(raw) }.getOrDefault(emptyList())
    }

    fun saveProfiles(profiles: List<WireProfile>) {
        prefs.edit().putString(KEY_PROFILES, json.encodeToString(profiles)).apply()
    }

    fun loadActiveProfileId(): String? = prefs.getString(KEY_ACTIVE_ID, null)

    fun saveActiveProfileId(id: String?) {
        val e = prefs.edit()
        if (id == null) e.remove(KEY_ACTIVE_ID) else e.putString(KEY_ACTIVE_ID, id)
        e.apply()
    }

    companion object {
        private const val PREFS_NAME = "wirepn_profiles_secure"
        private const val KEY_PROFILES = "profiles_v1"
        private const val KEY_ACTIVE_ID = "active_profile_id"
    }
}
