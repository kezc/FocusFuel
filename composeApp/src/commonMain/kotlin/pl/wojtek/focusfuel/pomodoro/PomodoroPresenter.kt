package pl.wojtek.focusfuel.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import co.touchlab.kermit.Logger
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.util.asyncEventSink
import pl.wojtek.focusfuel.util.rememberRetainedCoroutineScope
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
    val timeRemainingSeconds: Int,
    val isRunning: Boolean,
    val timerDisplay: String,
    val eventSink: (PomodoroEvent) -> Unit
) : CircuitUiState

@CircuitInject(PomodoroScreen::class, AppScope::class)
@Inject
class PomodoroPresenter(
    private val pomodoroTimerFactory: (CoroutineScope) -> PomodoroTimer,
    @Assisted private val navigator: Navigator,
) : FocusPresenter<PomodoroState>() {
    @Composable
    override fun presentState(): PomodoroState {
        val coroutineScope = rememberRetainedCoroutineScope()
        val pomodoroTimer = rememberRetained { pomodoroTimerFactory(coroutineScope) }
        val timerState by pomodoroTimer.state.collectAsState()

        LaunchedEffect(Unit) {
            pomodoroTimer.init()
        }

        LaunchedEffect(timerState) {
            pomodoroTimer.save()
        }

        val remainingSeconds = getRemainingSeconds(timerState)

        return PomodoroState(
            currentPhase = timerState.currentPhase,
            timeRemainingSeconds = remainingSeconds,
            isRunning = timerState.isRunning,
            timerDisplay = formatTime(remainingSeconds),
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

    private fun getRemainingSeconds(timerState: PomodoroTimerState) =
        (timerState.timeRemainingMs / 1000f).roundToInt()

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}"
    }
}

abstract class FocusPresenter<UiState : CircuitUiState> : Presenter<UiState> {
    @Composable
    final override fun present(): UiState {
        return presentState().also { Logger.d { it.toString() } }
    }

    @Composable
    abstract fun presentState(): UiState
}
