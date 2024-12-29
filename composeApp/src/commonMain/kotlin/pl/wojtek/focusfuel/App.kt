package pl.wojtek.focusfuel

import AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.runtime.Navigator
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(circuit: Circuit, backstack: SaveableBackStack, navigator: Navigator) {
    LaunchedEffect(Unit) {
        Logger.setLogWriters(platformLogWriter())
    }

    AppTheme {
        CircuitCompositionLocals(circuit) {
            ContentWithOverlays {
                NavigableCircuitContent(navigator = navigator, backStack = backstack)
            }
        }
    }
}


