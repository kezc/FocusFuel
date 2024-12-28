package pl.wojtek.focusfuel.features.pomodoro

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.features.pomodoro.PomodoroTimer.Companion.LONG_BREAK_TIME_MS
import pl.wojtek.focusfuel.features.pomodoro.PomodoroTimer.Companion.SHORT_BREAK_TIME_MS
import pl.wojtek.focusfuel.features.pomodoro.PomodoroTimer.Companion.WORK_TIME_MS
import pl.wojtek.focusfuel.repository.PomodorosRepository
import pl.wojtek.focusfuel.util.datetime.TimestampProvider
import pl.wojtek.focusfuel.util.parcelize.CommonParcelable
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize

@Inject
class PomodoroTimer(
    private val pomodoroSaver: PomodoroSaver,
    private val timestampProvider: TimestampProvider,
    @Assisted private val coroutineScope: CoroutineScope,
    private val pomodorosRepository: PomodorosRepository,
) {
    private val _state = MutableStateFlow(PomodoroTimerState())
    val state: StateFlow<PomodoroTimerState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var lastUpdate: Long = timestampProvider.getTimestamp()

    fun init() {
        _state.value = pomodoroSaver.loadState().also { Logger.d { "Loaded state: $it" } }
        if (state.value.isRunning) {
            coroutineScope.startTimer()
        }
    }

    fun toggleTimer() {
        val currentState = _state.value
        if (currentState.isRunning) {
            stopTimer()
        } else {
            coroutineScope.startTimer()
        }
    }

    fun save() {
        pomodoroSaver.saveState(_state.value).also { Logger.d { "Saved state: ${_state.value}" } }
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

    private fun CoroutineScope.startTimer() {
        if (timerJob?.isActive == true) return

        _state.update { it.copy(isRunning = true) }
        lastUpdate = timestampProvider.getTimestamp()
        timerJob = launch {
            while (isActive) {
                delay(getDelay())
                updateTimer()
            }
        }
    }

    private fun getDelay(): Long {
        val remainingMsInCurrentSecond = state.value.timeRemainingMs % 1000
        return if (remainingMsInCurrentSecond < 200) 1000
        else remainingMsInCurrentSecond
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _state.update { getUpdatedState(it).copy(isRunning = false) }
    }

    private fun updateTimer() {
        _state.update { getUpdatedState(it) }
    }

    private fun getUpdatedState(currentState: PomodoroTimerState): PomodoroTimerState {
        val currentTimestamp = timestampProvider.getTimestamp()
        val newTimeRemaining = currentState.timeRemainingMs - (currentTimestamp - lastUpdate).toInt()
        lastUpdate = currentTimestamp
        return if (newTimeRemaining <= 0) {
            persistIfFinishedPomodoro(currentState)
            currentState.transitionToNextState()
        } else {
            currentState.copy(timeRemainingMs = newTimeRemaining)
        }
    }

    private fun persistIfFinishedPomodoro(currentState: PomodoroTimerState) {
        if (currentState.currentPhase == PomodoroPhase.WORK) {
            pomodorosRepository.addPomodoro()
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
//        const val WORK_TIME_MS = 25 * 60 * 1000L // 25 minutes
//        const val SHORT_BREAK_TIME_MS = 5 * 60 * 1000L // 5 minutes
//        const val LONG_BREAK_TIME_MS = 15 * 60 * 1000L // 15 minutes
const val WORK_TIME_MS = 10 * 1000L // 10 sec
        const val SHORT_BREAK_TIME_MS = 5 * 1000L // 5 sec
        const val LONG_BREAK_TIME_MS = 15 * 1000L // 15 sec
    }
}

@CommonParcelize
data class PomodoroTimerState(
    val currentPhase: PomodoroPhase = PomodoroPhase.WORK,
    val timeRemainingMs: Long = WORK_TIME_MS,
    val isRunning: Boolean = false,
    val completedPomodoros: Int = 0
) : CommonParcelable {

    fun toWork() = copy(
        currentPhase = PomodoroPhase.WORK,
        timeRemainingMs = WORK_TIME_MS,
    )

    fun toShortBreak() = copy(
        currentPhase = PomodoroPhase.SHORT_BREAK,
        timeRemainingMs = SHORT_BREAK_TIME_MS,
        completedPomodoros = completedPomodoros + 1
    )

    fun toLongBreak() = copy(
        currentPhase = PomodoroPhase.LONG_BREAK,
        timeRemainingMs = LONG_BREAK_TIME_MS,
        completedPomodoros = completedPomodoros + 1
    )
}

enum class PomodoroPhase {
    WORK,
    SHORT_BREAK,
    LONG_BREAK
}
