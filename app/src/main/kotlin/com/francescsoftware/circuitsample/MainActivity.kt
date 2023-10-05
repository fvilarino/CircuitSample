package com.francescsoftware.circuitsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.francescsoftware.circuitsample.ui.home.HomeScreen
import com.francescsoftware.circuitsample.ui.theme.CircuitSampleTheme
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var circuit: Circuit

    override fun onCreate(savedInstanceState: Bundle?) {
        val appComponent = (application as CircuitApplication).appComponent
        appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContent {
            CircuitSampleTheme {
                CircuitCompositionLocals(circuit) {
                    SampleApp()
                }
            }
        }
    }
}

@Composable
fun SampleApp() {
    CircuitSampleTheme {
        Surface {
            val backstack = rememberSaveableBackStack {
                push(HomeScreen)
            }
            val navigator = rememberCircuitNavigator(backstack)
            NavigableCircuitContent(
                navigator = navigator,
                backstack = backstack,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}