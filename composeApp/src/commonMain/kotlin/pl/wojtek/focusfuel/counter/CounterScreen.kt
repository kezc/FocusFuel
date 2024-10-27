package pl.wojtek.focusfuel.counter

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.runtime.screen.Screen
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize

@CommonParcelize
object CounterScreen : Screen

@Composable
fun CounterUI(modifier: Modifier = Modifier, state: CounterState) {
    Column {
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
