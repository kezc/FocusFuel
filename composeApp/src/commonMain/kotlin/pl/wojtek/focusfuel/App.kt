package pl.wojtek.focusfuel

import AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.counter.CounterScreen

@Composable
@Preview
fun App(circuit: Circuit) {
    LaunchedEffect(Unit) {
        Logger.setLogWriters(platformLogWriter())
    }

    AppTheme {
        val backstack = rememberSaveableBackStack(CounterScreen)

        val navigator = rememberCircuitNavigator(
            backStack = backstack,
            onRootPop = { /* no-op */ }
        )

        CircuitCompositionLocals(circuit) {
            NavigableCircuitContent(navigator, backstack)
        }
    }
}


