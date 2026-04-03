package com.wirepn.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.wirepn.android.ui.WirepnApp
import com.wirepn.android.ui.theme.WirepnTheme

class MainActivity : ComponentActivity() {

    private val vm: MainViewModel by viewModels {
        val app = application as WirepnApplication
        MainViewModelFactory(app.profileRepository, app.wireGuard, app.appPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra(EXTRA_CONNECT_VPN, false)) {
            intent.removeExtra(EXTRA_CONNECT_VPN)
            vm.requestConnectFromExternal()
        }
        enableEdgeToEdge()
        setContent {
            val themePref by vm.themePreference.collectAsState()
            WirepnTheme(themePreference = themePref) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    WirepnApp(viewModel = vm)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(EXTRA_CONNECT_VPN, false)) {
            intent.removeExtra(EXTRA_CONNECT_VPN)
            vm.requestConnectFromExternal()
        }
    }

    companion object {
        const val EXTRA_CONNECT_VPN = "com.wirepn.android.EXTRA_CONNECT_VPN"
    }
}
