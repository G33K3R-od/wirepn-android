package com.wirepn.android.data

import kotlinx.serialization.Serializable

@Serializable
data class WireProfile(
    val id: String,
    val displayName: String,
    val configText: String,
    val createdAtEpochMs: Long,
)
