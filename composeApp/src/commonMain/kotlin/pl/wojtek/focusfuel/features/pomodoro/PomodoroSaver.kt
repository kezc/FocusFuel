package pl.wojtek.focusfuel.features.pomodoro

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.repository.PomodorosRepository
import pl.wojtek.focusfuel.util.datetime.TimestampProvider
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

private const val CURRENT_PHASE = "CURRENT_PHASE"
private const val TIME_REMAINING_MS = "TIME_REMAINING_MS"
private const val IS_RUNNING = "IS_RUNNING"
private const val COMPLETED_POMODOROS = "COMPLETED_POMODOROS"
private const val LAST_UPDATED_TIMESTAMP = "LAST_UPDATED_TIMESTAMP"

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class PomodoroSaverImpl(
    private val settings: Settings,
    private val timestampProvider: TimestampProvider,
    private val pomodorosRepository: PomodorosRepository,
) : PomodoroSaver {
    override fun loadState(): PomodoroTimerState {
        val default = PomodoroTimerState()
        val currentPhase = PomodoroPhase.valueOf(settings.getString(CURRENT_PHASE, default.currentPhase.name))
        val savedTimeRemainingMs = settings.getLong(TIME_REMAINING_MS, default.timeRemainingMs)
        val savedIsRunning = settings.getBoolean(IS_RUNNING, default.isRunning)
        val completedPomodoros = settings.getInt(COMPLETED_POMODOROS, default.completedPomodoros)
        val lastUpdatedTimestamp = settings.getLong(LAST_UPDATED_TIMESTAMP, timestampProvider.getTimestamp())

        val elapsedTime = timestampProvider.getTimestamp() - lastUpdatedTimestamp
        val timeRemainingMs = if (savedIsRunning) {
            (savedTimeRemainingMs - elapsedTime).coerceAtLeast(0)
        } else {
            savedTimeRemainingMs
        }
        val (newPhase, newCompletedPomodoros, timeRemainingMsAdjusted) = newPhaseIfPreviousCompleted(
            currentPhase = currentPhase,
            completedPomodoros = completedPomodoros,
            timeRemainingSeconds = timeRemainingMs
        )
        checkIfFinishedPomodoro(
            isPomodoroFinished = newCompletedPomodoros > completedPomodoros,
            lastUpdatedTimestamp = lastUpdatedTimestamp,
            timeRemainingSeconds = timeRemainingMsAdjusted,
        )
        val isRunning = savedIsRunning && timeRemainingMsAdjusted > 0

        return PomodoroTimerState(
            currentPhase = newPhase,
            timeRemainingMs = timeRemainingMsAdjusted,
            isRunning = isRunning,
            completedPomodoros = newCompletedPomodoros
        )
    }

    private fun checkIfFinishedPomodoro(isPomodoroFinished: Boolean, lastUpdatedTimestamp: Long, timeRemainingSeconds: Long) {
        val date = Instant
            .fromEpochMilliseconds(lastUpdatedTimestamp + timeRemainingSeconds)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        if (isPomodoroFinished) {
            pomodorosRepository.addPomodoro(date)
        }
    }

    private fun newPhaseIfPreviousCompleted(
        currentPhase: PomodoroPhase,
        completedPomodoros: Int,
        timeRemainingSeconds: Long
    ): Triple<PomodoroPhase, Int, Long> {

        if (timeRemainingSeconds > 0) {
            return Triple(currentPhase, completedPomodoros, timeRemainingSeconds)
        }

        return when (currentPhase) {
            PomodoroPhase.WORK -> {
                val plusOneCompleted = completedPomodoros + 1
                if (plusOneCompleted % 4 == 0) {
                    Triple(PomodoroPhase.LONG_BREAK, plusOneCompleted, PomodoroTimer.LONG_BREAK_TIME_MS)
                } else {
                    Triple(PomodoroPhase.SHORT_BREAK, plusOneCompleted, PomodoroTimer.SHORT_BREAK_TIME_MS)
                }
            }

            PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK ->
                Triple(PomodoroPhase.WORK, completedPomodoros, PomodoroTimer.WORK_TIME_MS)
        }
    }

    override fun saveState(state: PomodoroTimerState) {
        settings[CURRENT_PHASE] = state.currentPhase.name
        settings[TIME_REMAINING_MS] = state.timeRemainingMs
        settings[IS_RUNNING] = state.isRunning
        settings[COMPLETED_POMODOROS] = state.completedPomodoros
        settings[LAST_UPDATED_TIMESTAMP] = timestampProvider.getTimestamp()
    }
}

interface PomodoroSaver {
    fun loadState(): PomodoroTimerState
    fun saveState(state: PomodoroTimerState)
}
