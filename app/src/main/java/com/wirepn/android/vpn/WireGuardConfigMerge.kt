package com.wirepn.android.vpn

import com.wirepn.android.data.SplitTunnelMode

/**
 * Merges [ExcludedApplications] or [IncludedApplications] into the [Interface] section for
 * wireguard-android / GoBackend.
 */
object WireGuardConfigMerge {

    fun mergeSplitTunneling(
        configText: String,
        mode: SplitTunnelMode,
        packages: Set<String>,
    ): String {
        val fromPrefs = packages.map { it.trim() }.filter { it.isNotEmpty() }.sorted().distinct()
        val lines = configText.lines().toMutableList()
        val ifaceIdx = lines.indexOfFirst { it.trim().equals("[Interface]", ignoreCase = true) }
        if (ifaceIdx < 0) {
            return appendInterfaceBlock(lines, mode, fromPrefs)
        }

        val sectionEnd = interfaceSectionEnd(lines, ifaceIdx)
        val existingExcluded = parseAppsLineInSection(lines, ifaceIdx, sectionEnd, "ExcludedApplications")
        val existingIncluded = parseAppsLineInSection(lines, ifaceIdx, sectionEnd, "IncludedApplications")

        removeApplicationLines(lines, ifaceIdx)

        when (mode) {
            SplitTunnelMode.EXCLUDE_APPS -> {
                val merged = (existingExcluded + fromPrefs).sorted().distinct()
                if (merged.isNotEmpty()) {
                    lines.add(ifaceIdx + 1, "ExcludedApplications = ${merged.joinToString(",")}")
                }
            }
            SplitTunnelMode.INCLUDE_APPS -> {
                val merged = (existingIncluded + fromPrefs).sorted().distinct()
                if (merged.isNotEmpty()) {
                    lines.add(ifaceIdx + 1, "IncludedApplications = ${merged.joinToString(",")}")
                }
            }
        }
        return lines.joinToString("\n")
    }

    private fun interfaceSectionEnd(lines: List<String>, ifaceIdx: Int): Int {
        var end = ifaceIdx + 1
        while (end < lines.size) {
            val t = lines[end].trim()
            if (t.startsWith("[") && t.endsWith("]")) break
            end++
        }
        return end - 1
    }

    private fun appendInterfaceBlock(
        lines: MutableList<String>,
        mode: SplitTunnelMode,
        fromPrefs: List<String>,
    ): String {
        if (fromPrefs.isEmpty()) return lines.joinToString("\n")
        val line = when (mode) {
            SplitTunnelMode.EXCLUDE_APPS -> "ExcludedApplications = ${fromPrefs.joinToString(",")}"
            SplitTunnelMode.INCLUDE_APPS -> "IncludedApplications = ${fromPrefs.joinToString(",")}"
        }
        val peerIdx = lines.indexOfFirst { it.trim().startsWith("[Peer]") }
        val insertSection = if (peerIdx >= 0) peerIdx else lines.size
        lines.add(insertSection, line)
        lines.add(insertSection, "[Interface]")
        return lines.joinToString("\n")
    }

    private fun parseAppsLineInSection(
        lines: List<String>,
        ifaceIdx: Int,
        sectionEnd: Int,
        key: String,
    ): Set<String> {
        for (i in ifaceIdx + 1..sectionEnd) {
            val line = lines[i]
            if (line.trim().startsWith(key, ignoreCase = true)) {
                return parseCommaList(line).toSet()
            }
        }
        return emptySet()
    }

    private fun parseCommaList(line: String): List<String> {
        val idx = line.indexOf('=')
        if (idx < 0) return emptyList()
        return line.substring(idx + 1)
            .split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun removeApplicationLines(lines: MutableList<String>, ifaceIdx: Int) {
        var i = ifaceIdx + 1
        while (i < lines.size) {
            val trimmed = lines[i].trim()
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) break
            if (trimmed.startsWith("ExcludedApplications", ignoreCase = true) ||
                trimmed.startsWith("IncludedApplications", ignoreCase = true)
            ) {
                lines.removeAt(i)
                continue
            }
            i++
        }
    }
}
