package com.example.nsdsample

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

@Composable
fun Buttons(
    onStartCtv: () -> Unit = {},
    onStartMobile: () -> Unit = {},
    sayToMobile: () -> Unit = {},
    sayToCtv: () -> Unit = {},
    whoAmI: State<WhoAmI>,
) {
    Column {

        when (whoAmI.value) {
            WhoAmI.CTV -> {
                Button(onClick = sayToMobile) { Text("Say to Mobile") }
            }

            WhoAmI.MOBILE -> {
                Button(onClick = sayToCtv) { Text("Say to CTV") }
            }

            WhoAmI.UNKNOWN -> {
                Button(onClick = onStartCtv) { Text("Start CTV") }
                Button(onClick = onStartMobile) { Text("Start Mobile") }
            }
        }
    }
}