package pl.wojtek.focusfuel.pomodoro

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.util.parcelize.CommonParcelable
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize

class PomodoroTimer @Inject constructor(
    private val pomodoroSaver: PomodoroSaver
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

    private fun startTimer() {
        if (timerJob?.isActive == true) return

        _state.update { it.copy(isRunning = true) }
        timerJob = CoroutineScope(Dispatchers.Default).launch {
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
            val newState = currentState.copy(
                timeRemainingSeconds = currentState.timeRemainingSeconds - 1
            )
            if (newState.timeRemainingSeconds > 0) {
                return@update newState
            }

            when (currentState.currentPhase) {
                PomodoroPhase.WORK -> {
                    val newCompletedPomodoros = currentState.completedPomodoros + 1
                    if (newCompletedPomodoros % 4 == 0) {
                        currentState.copy(
                            currentPhase = PomodoroPhase.LONG_BREAK,
                            timeRemainingSeconds = LONG_BREAK_TIME,
                            completedPomodoros = newCompletedPomodoros
                        )
                    } else {
                        currentState.copy(
                            currentPhase = PomodoroPhase.SHORT_BREAK,
                            timeRemainingSeconds = SHORT_BREAK_TIME,
                            completedPomodoros = newCompletedPomodoros
                        )
                    }
                }

                PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> {
                    currentState.copy(
                        currentPhase = PomodoroPhase.WORK,
                        timeRemainingSeconds = WORK_TIME,
                    )
                }
            }
        }
    }

    fun reset() {
        stopTimer()
        _state.update {
            PomodoroTimerState(
                currentPhase = PomodoroPhase.WORK,
                timeRemainingSeconds = WORK_TIME,
                isRunning = false,
                completedPomodoros = 0
            )
        }
    }

    fun skip() {
        stopTimer()
        _state.update { currentState ->
            when (currentState.currentPhase) {
                PomodoroPhase.WORK -> {
                    val newCompletedPomodoros = currentState.completedPomodoros + 1
                    if (newCompletedPomodoros % 4 == 0) {
                        currentState.copy(
                            currentPhase = PomodoroPhase.LONG_BREAK,
                            timeRemainingSeconds = LONG_BREAK_TIME,
                            completedPomodoros = newCompletedPomodoros
                        )
                    } else {
                        currentState.copy(
                            currentPhase = PomodoroPhase.SHORT_BREAK,
                            timeRemainingSeconds = SHORT_BREAK_TIME,
                            completedPomodoros = newCompletedPomodoros
                        )
                    }
                }

                PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> {
                    currentState.copy(
                        currentPhase = PomodoroPhase.WORK,
                        timeRemainingSeconds = WORK_TIME,
                    )
                }
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
    val timeRemainingSeconds: Int = PomodoroTimer.WORK_TIME,
    val isRunning: Boolean = false,
    val completedPomodoros: Int = 0
) : CommonParcelable

enum class PomodoroPhase {
    WORK,
    SHORT_BREAK,
    LONG_BREAK
}
