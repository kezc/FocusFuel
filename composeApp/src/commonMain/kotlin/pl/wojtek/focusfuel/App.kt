package pl.wojtek.focusfuel

import AppTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import com.russhwolf.settings.Settings
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.counter.CounterScreen
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

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
