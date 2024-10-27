package pl.wojtek.focusfuel.counter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

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

@CircuitInject(CounterScreen::class, AppScope::class)
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
}
