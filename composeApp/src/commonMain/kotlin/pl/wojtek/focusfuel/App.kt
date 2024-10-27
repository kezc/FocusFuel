package pl.wojtek.focusfuel

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.presenter.presenterOf
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import com.slack.circuit.runtime.ui.ui
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.counter.CounterPresenter
import pl.wojtek.focusfuel.counter.CounterScreen
import pl.wojtek.focusfuel.counter.CounterState
import pl.wojtek.focusfuel.counter.CounterUI

@Composable
@Preview
fun App() {
    MaterialTheme {


        val circuit = Circuit.Builder()
            .addUiFactory(UiFactory())
            .addPresenterFactory(CounterPresenter.PresenterFactory())
            .build()

        // SaveableBackStack instance
        val backstack = rememberSaveableBackStack(CounterScreen)

        // Navigator instance
        val navigator = rememberCircuitNavigator(
            backStack = backstack,
            onRootPop = { /* no-op */ }
        )

        CircuitCompositionLocals(circuit) {
            NavigableCircuitContent(navigator, backstack)
        }
    }
}

class UiFactory : Ui.Factory {
    override fun create(screen: Screen, context: CircuitContext): Ui<*>? {
        return when (screen) {
            is CounterScreen -> ui<CounterState> { state, modifier ->
                CounterUI(state = state, modifier = modifier)
            }

            else -> null
        }
    }
}
