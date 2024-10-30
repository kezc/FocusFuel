package pl.wojtek.focusfuel.counter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CommonParcelize
object CounterScreen : Screen

@CircuitInject(CounterScreen::class, AppScope::class)
class CounterUI : Ui<CounterState> {
    @Composable
    override fun Content(state: CounterState, modifier: Modifier) {
        CounterUI(modifier, state)
    }
}

@Composable
private fun CounterUI(modifier: Modifier = Modifier, state: CounterState) {
    Column(
        modifier = modifier.systemBarsPadding()
    ) {
        Button(
            onClick = { state.eventSink(CounterEvent.Decrease) },
            content = { Text("+") }
        )
        Text(text = "Count ${state.count}")
        Button(
            onClick = { state.eventSink(CounterEvent.Increase) },
            content = { Text("-") }
        )
        Button(
            onClick = { state.eventSink(CounterEvent.Pop) },
            content = { Text("pop") }
        )
    }
}
