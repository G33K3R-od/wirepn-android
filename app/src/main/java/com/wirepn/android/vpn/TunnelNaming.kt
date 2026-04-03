package com.wirepn.android.vpn

import com.wireguard.android.backend.Tunnel

object TunnelNaming {
    fun fromDisplayName(displayName: String, profileId: String): String {
        val cleaned = displayName.replace(Regex("[^a-zA-Z0-9_=+.-]"), "_").take(15)
        if (cleaned.isNotEmpty() && !Tunnel.isNameInvalid(cleaned)) return cleaned
        val noDash = profileId.replace("-", "").take(15)
        if (noDash.isNotEmpty() && !Tunnel.isNameInvalid(noDash)) return noDash
        val tail = profileId.replace("-", "").take(13)
        return "wg$tail"
    }
}
