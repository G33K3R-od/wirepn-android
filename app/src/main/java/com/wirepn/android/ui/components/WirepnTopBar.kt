package com.wirepn.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wirepn.android.R
import com.wirepn.android.ui.theme.LocalWirepnExtraColors
import com.wirepn.android.vpn.VpnConnectionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WirepnTopBar(
    vpnState: VpnConnectionState,
    modifier: Modifier = Modifier,
) {
    val extra = LocalWirepnExtraColors.current
    val (chipLabel, dotColor) = when (vpnState) {
        is VpnConnectionState.Disconnected ->
            stringResource(R.string.status_disconnected) to MaterialTheme.colorScheme.onSurfaceVariant
        is VpnConnectionState.Connecting ->
            stringResource(R.string.status_connecting) to extra.statusConnecting
        is VpnConnectionState.Connected ->
            stringResource(R.string.status_connected) to MaterialTheme.colorScheme.primary
        is VpnConnectionState.Error ->
            stringResource(R.string.status_error) to MaterialTheme.colorScheme.error
    }
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(extra.topFlourish),
        )
        CenterAlignedTopAppBar(
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = stringResource(R.string.app_subtitle),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            actions = {
                WirepnStatusChip(
                    label = chipLabel,
                    dotColor = dotColor,
                    modifier = Modifier.padding(end = 8.dp),
                )
            },
        )
    }
}
