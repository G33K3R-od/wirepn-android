package com.wirepn.android.data

/**
 * How [AppPreferences.splitTunnelAppPackages] is applied in the WireGuard [Interface] section.
 */
enum class SplitTunnelMode {
    /** [ExcludedApplications] — selected apps bypass the VPN (split tunneling). */
    EXCLUDE_APPS,

    /** [IncludedApplications] — only selected apps use the VPN (whitelist). */
    INCLUDE_APPS,
}
