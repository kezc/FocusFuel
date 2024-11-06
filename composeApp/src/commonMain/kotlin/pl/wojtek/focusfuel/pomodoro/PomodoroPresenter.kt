package pl.wojtek.focusfuel.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import co.touchlab.kermit.Logger
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

sealed interface PomodoroEvent : CircuitUiEvent {
    data object ToggleTimer : PomodoroEvent
    data object Reset : PomodoroEvent
    data object Skip : PomodoroEvent
}

data class PomodoroState(
    val currentPhase: PomodoroPhase,
    val timeRemainingSeconds: Int,
    val totalSeconds: Int,
    val isRunning: Boolean,
    val timerDisplay: String,
    val eventSink: (PomodoroEvent) -> Unit
) : CircuitUiState

@CircuitInject(PomodoroScreen::class, AppScope::class)
class PomodoroPresenter : FocusPresenter<PomodoroState>() {
    private val pomodoroTimer = PomodoroTimer()

    @Composable
    override fun presentState(): PomodoroState {
        val timerState by pomodoroTimer.state.collectAsState()

        fun formatTime(seconds: Int): String {
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return "${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}"
        }

        return PomodoroState(
            currentPhase = timerState.currentPhase,
            timeRemainingSeconds = timerState.timeRemainingSeconds,
            totalSeconds = timerState.totalSeconds,
            isRunning = timerState.isRunning,
            timerDisplay = formatTime(timerState.timeRemainingSeconds),
            eventSink = { event ->
                when (event) {
                    PomodoroEvent.ToggleTimer -> pomodoroTimer.toggleTimer()
                    PomodoroEvent.Reset -> pomodoroTimer.reset()
                    PomodoroEvent.Skip -> pomodoroTimer.skip()
                }
            }
        )
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
