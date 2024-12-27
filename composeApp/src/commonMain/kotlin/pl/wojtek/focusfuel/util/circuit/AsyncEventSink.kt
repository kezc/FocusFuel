package pl.wojtek.focusfuel.util.circuit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.slack.circuit.runtime.CircuitUiEvent
import kotlinx.coroutines.CoroutineScope

@Composable
inline fun <T : CircuitUiEvent> asyncEventSink(crossinline eventSink: CoroutineScope.(T) -> Unit): (T) -> Unit {
    val scope = rememberCoroutineScope()
    return { event -> scope.eventSink(event) }
}
