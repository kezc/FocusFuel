package pl.wojtek.focusfuel.pomodoro

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.pomodoro.PomodoroTimer.Companion.LONG_BREAK_TIME
import pl.wojtek.focusfuel.pomodoro.PomodoroTimer.Companion.SHORT_BREAK_TIME
import pl.wojtek.focusfuel.pomodoro.PomodoroTimer.Companion.WORK_TIME
import pl.wojtek.focusfuel.util.coroutines.DispatchersProvider
import pl.wojtek.focusfuel.util.parcelize.CommonParcelable
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize

class PomodoroTimer @Inject constructor(
    private val pomodoroSaver: PomodoroSaver,
    private val dispatchers: DispatchersProvider,
) {
    private val _state = MutableStateFlow(pomodoroSaver.loadState())
    val state: StateFlow<PomodoroTimerState> = _state.asStateFlow()

    private var timerJob: Job? = null

    init {
        if (state.value.isRunning) {
            startTimer()
        }
    }

    fun toggleTimer() {
        val currentState = _state.value
        if (currentState.isRunning) {
            stopTimer()
        } else {
            startTimer()
        }
    }

    fun save() {
        pomodoroSaver.saveState(_state.value)
    }

    fun reset() {
        stopTimer()
        _state.update { PomodoroTimerState() }
    }

    fun skip() {
        stopTimer()
        _state.update { currentState ->
            currentState.transitionToNextState()
        }
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return

        _state.update { it.copy(isRunning = true) }
        timerJob = CoroutineScope(dispatchers.default).launch {
            while (isActive) {
                delay(1000)
                updateTimer()
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _state.update { it.copy(isRunning = false) }
    }

    private fun updateTimer() {
        _state.update { currentState ->
            val newTimeRemainingSeconds= currentState.timeRemainingSeconds - 1
            if (newTimeRemainingSeconds <= 0) {
                currentState.transitionToNextState()
            } else {
                currentState.copy(timeRemainingSeconds = newTimeRemainingSeconds)
            }
        }
    }

    private fun PomodoroTimerState.transitionToNextState() =
        when (currentPhase) {
            PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> toWork()
            PomodoroPhase.WORK -> {
                val newCompletedPomodoros = completedPomodoros + 1
                if (newCompletedPomodoros % 4 == 0) {
                    toLongBreak()
                } else {
                    toShortBreak()
                }
            }
        }

    companion object {
        const val WORK_TIME = 25 * 60 // 25 minutes
        const val SHORT_BREAK_TIME = 5 * 60 // 5 minutes
        const val LONG_BREAK_TIME = 15 * 60 // 15 minutes
    }
}

@CommonParcelize
data class PomodoroTimerState(
    val currentPhase: PomodoroPhase = PomodoroPhase.WORK,
    val timeRemainingSeconds: Int = WORK_TIME,
    val isRunning: Boolean = false,
    val completedPomodoros: Int = 0
) : CommonParcelable {
    fun toWork() = copy(
        currentPhase = PomodoroPhase.WORK,
        timeRemainingSeconds = WORK_TIME,
    )

    fun toShortBreak() = copy(
        currentPhase = PomodoroPhase.SHORT_BREAK,
        timeRemainingSeconds = SHORT_BREAK_TIME,
        completedPomodoros = completedPomodoros + 1
    )

    fun toLongBreak() = copy(
        currentPhase = PomodoroPhase.LONG_BREAK,
        timeRemainingSeconds = LONG_BREAK_TIME,
        completedPomodoros = completedPomodoros + 1
    )
}

enum class PomodoroPhase {
    WORK,
    SHORT_BREAK,
    LONG_BREAK
}
