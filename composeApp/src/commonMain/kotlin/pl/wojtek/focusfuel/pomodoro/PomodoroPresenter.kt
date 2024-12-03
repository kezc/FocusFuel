package pl.wojtek.focusfuel.pomodoro

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.rememberEventListener
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.pomodoro.PomodoroTimer.Companion.WORK_TIME
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

sealed interface PomodoroEvent : CircuitUiEvent {
    data object ToggleTimer : PomodoroEvent
    data object Reset : PomodoroEvent
    data object Skip : PomodoroEvent
}

data class PomodoroState(
    val currentPhase: PomodoroPhase,
    val timeRemainingSeconds: Int,
    val isRunning: Boolean,
    val timerDisplay: String,
    val eventSink: (PomodoroEvent) -> Unit
) : CircuitUiState

@CircuitInject(PomodoroScreen::class, AppScope::class)
class PomodoroPresenter @Inject constructor(
    private val pomodoroTimer: PomodoroTimer
): FocusPresenter<PomodoroState>() {
    @Composable
    override fun presentState(): PomodoroState {
        val timerState by pomodoroTimer.state.collectAsState()
        LaunchedEffect(timerState) {
            // workaround bc DisposableEffect here doesn't work on iOS
            pomodoroTimer.save()
        }

        fun formatTime(seconds: Int): String {
            val minutes = seconds / 60
            val remainingSeconds = seconds % 60
            return "${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}"
        }

        return PomodoroState(
            currentPhase = timerState.currentPhase,
            timeRemainingSeconds = timerState.timeRemainingSeconds,
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
