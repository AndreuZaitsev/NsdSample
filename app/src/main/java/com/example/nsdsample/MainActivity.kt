package com.example.nsdsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.nsdsample.ui.theme.NsdSampleTheme
import kotlinx.coroutines.flow.MutableStateFlow

enum class WhoAmI {
    CTV, MOBILE, UNKNOWN
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NsdSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier.padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Buttons(
                            onStartCtv = ::startCtv,
                            onStartMobile = ::startMobile,
                            sayToMobile = ::sayToMobile,
                            sayToCtv = ::sayToCtv,
                            whoAmI.collectAsState()
                        )
                    }
                }
            }
        }
    }

    private val whoAmI = MutableStateFlow(WhoAmI.UNKNOWN)

    private val nsdServiceCTV by lazy { NsdServiceCTV(this) }
    private val nsdServiceMobile by lazy { NsdServiceMobile(this) }

    private fun startCtv() {
        nsdServiceCTV.start()
        whoAmI.value = WhoAmI.CTV
    }

    private fun startMobile() {
        nsdServiceMobile.start()
        whoAmI.value = WhoAmI.MOBILE
    }

    private fun sayToMobile() {
        nsdServiceCTV.sendMessage("Hello, Mobile!")
    }

    private fun sayToCtv() {
        nsdServiceMobile.sendMessage("Hello, CTV!")
    }
}
