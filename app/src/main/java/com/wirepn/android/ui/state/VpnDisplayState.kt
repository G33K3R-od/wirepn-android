package com.wirepn.android.ui.state

/** Состояние для UI: включает фазу отключения и минимальные длительности анимаций. */
sealed class VpnDisplayState {
    data object Disconnected : VpnDisplayState()
    data object Connecting : VpnDisplayState()
    data object Disconnecting : VpnDisplayState()
    data object Connected : VpnDisplayState()
    data class Error(val message: String) : VpnDisplayState()
}
