package pl.wojtek.focusfuel.counter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.presenter.presenterOf
import com.slack.circuit.runtime.screen.Screen


sealed interface CounterEvent : CircuitUiEvent {
    data object Increase : CounterEvent
    data object Decrease : CounterEvent
    data object Pop : CounterEvent
}

// State
data class CounterState(
    val count: Int,
    val eventSink: (CounterEvent) -> Unit
) : CircuitUiState


class CounterPresenter(
    private val navigator: Navigator
) : Presenter<CounterState> {
    @Composable
    override fun present(): CounterState {
        var count by remember { mutableStateOf(0) }
        return CounterState(
            count = count,
            eventSink = { event ->
                when (event) {
                    is CounterEvent.Increase -> count++
                    is CounterEvent.Decrease -> count--
                    CounterEvent.Pop -> navigator.pop()
                }
            }
        )
    }

    class PresenterFactory : Presenter.Factory {
        override fun create(
            screen: Screen,
            navigator: Navigator,
            context: CircuitContext
        ): Presenter<*>? {
            return when (screen) {
                is CounterScreen -> CounterPresenter(navigator)
                else -> null
            }
        }
    }
}
