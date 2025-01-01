package pl.wojtek.focusfuel.features.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.util.circuit.FocusPresenter
import pl.wojtek.focusfuel.util.circuit.asyncEventSink
import pl.wojtek.focusfuel.util.circuit.rememberRetainedCoroutineScope
import pl.wojtek.focusfuel.util.datetime.PomodoroTimeFormat.formatPomodoroTime
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import kotlin.math.roundToInt

sealed interface PomodoroEvent : CircuitUiEvent {
    data object ToggleTimer : PomodoroEvent
    data object Reset : PomodoroEvent
    data object Skip : PomodoroEvent
    data object Back : PomodoroEvent
}

data class PomodoroState(
    val currentPhase: PomodoroPhase,
    val isRunning: Boolean,
    val timerDisplay: String,
    val eventSink: (PomodoroEvent) -> Unit
) : CircuitUiState

@CircuitInject(PomodoroScreen::class, AppScope::class)
@Inject
class PomodoroPresenter(
    private val pomodoroTimer: PomodoroTimer,
    @Assisted private val navigator: Navigator,
) : FocusPresenter<PomodoroState>() {
    @Composable
    override fun presentState(): PomodoroState {
        val timerState by pomodoroTimer.state.collectAsState()

        LaunchedEffect(Unit) {
            pomodoroTimer.init()
        }

        LaunchedEffect(timerState) {
            pomodoroTimer.save()
        }


        return PomodoroState(
            currentPhase = timerState.currentPhase,
            isRunning = timerState.isRunning,
            timerDisplay = formatPomodoroTime(timerState.timeRemainingMs),
            eventSink = asyncEventSink {
                when (it) {
                    PomodoroEvent.ToggleTimer -> launch { pomodoroTimer.toggleTimer() }
                    PomodoroEvent.Reset -> pomodoroTimer.reset()
                    PomodoroEvent.Skip -> pomodoroTimer.skip()
                    PomodoroEvent.Back -> navigator.pop()
                }
            }
        )
    }

}

