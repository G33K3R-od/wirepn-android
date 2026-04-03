package com.wirepn.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wirepn.android.ui.WirepnApp
import com.wirepn.android.ui.theme.WirepnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as WirepnApplication
        setContent {
            val vm: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    app.profileRepository,
                    app.wireGuard,
                    app.appPreferences,
                ),
            )
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
}
