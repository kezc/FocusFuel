package pl.wojtek.focusfuel.pomodoro

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject

private const val CURRENT_PHASE = "currentPhase"
private const val TIME_REMAINING_SECONDS = "timeRemainingSeconds"
private const val IS_RUNNING = "isRunning"
private const val COMPLETED_POMODOROS = "completedPomodoros"
private const val LAST_UPDATED_TIMESTAMP = "lastUpdatedTimestamp"

class PomodoroSaver @Inject constructor(
    private val settings: Settings
) {
    fun loadState(): PomodoroTimerState {
        val default = PomodoroTimerState()
        val currentPhase = PomodoroPhase.valueOf(settings.getString(CURRENT_PHASE, default.currentPhase.name))
        val savedTimeRemainingSeconds = settings.getInt(TIME_REMAINING_SECONDS, default.timeRemainingSeconds)
        val savedIsRunning = settings.getBoolean(IS_RUNNING, default.isRunning)
        val completedPomodoros = settings.getInt(COMPLETED_POMODOROS, default.completedPomodoros)
        val lastUpdatedTimestamp = settings.getLong(LAST_UPDATED_TIMESTAMP, currentTimestamp())

        val elapsedTime = (currentTimestamp() - lastUpdatedTimestamp) / 1000
        val timeRemainingSeconds = if (savedIsRunning) {
            (savedTimeRemainingSeconds - elapsedTime).toInt().coerceAtLeast(0)
        } else {
            savedTimeRemainingSeconds
        }
        val (newPhase, newCompletedPomodoros, timeRemainingSecondsAdjusted) = newPhaseIfPreviousCompleted(
            currentPhase,
            completedPomodoros,
            timeRemainingSeconds
        )
        val isRunning = savedIsRunning && timeRemainingSeconds > 0

        return PomodoroTimerState(newPhase, timeRemainingSecondsAdjusted, isRunning, newCompletedPomodoros)
    }

    private fun newPhaseIfPreviousCompleted(
        currentPhase: PomodoroPhase,
        completedPomodoros: Int,
        timeRemainingSeconds: Int
    ): Triple<PomodoroPhase, Int, Int> {

        if (timeRemainingSeconds > 0) {
            return Triple(currentPhase, completedPomodoros, timeRemainingSeconds)
        }

        return when (currentPhase) {
            PomodoroPhase.WORK -> {
                val plusOneCompleted = completedPomodoros + 1
                if (plusOneCompleted % 4 == 0) {
                    Triple(PomodoroPhase.LONG_BREAK, plusOneCompleted, PomodoroTimer.LONG_BREAK_TIME)
                } else {
                    Triple(PomodoroPhase.SHORT_BREAK, plusOneCompleted, PomodoroTimer.SHORT_BREAK_TIME)
                }
            }

            PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK ->
                Triple(PomodoroPhase.WORK, completedPomodoros, PomodoroTimer.WORK_TIME)
        }
    }

    fun saveState(state: PomodoroTimerState) {
        settings[CURRENT_PHASE] = state.currentPhase.name
        settings[TIME_REMAINING_SECONDS] = state.timeRemainingSeconds
        settings[IS_RUNNING] = state.isRunning
        settings[COMPLETED_POMODOROS] = state.completedPomodoros
        settings[LAST_UPDATED_TIMESTAMP] = currentTimestamp()
    }

    private fun currentTimestamp() = Clock.System.now().toEpochMilliseconds()
}
