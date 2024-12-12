package pl.wojtek.focusfuel.pomodoro

import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import com.slack.circuit.test.test
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
//
//
//class PomodoroPresenterTest {
//    private lateinit var presenter: PomodoroPresenter
//
//    @BeforeTest
//    fun setup() {
//        Logger.setLogWriters(CommonWriter())
//
//        presenter = PomodoroPresenter()
//    }
//
//    @Test
//    fun `initial state is work phase with correct duration`() = runTest {
//        presenter.test {
//            val initialState = awaitItem()
//            assertEquals(PomodoroPhase.WORK, initialState.currentPhase)
//            assertEquals(25 * 60, initialState.timeRemainingSeconds)
//            assertEquals("25:00", initialState.timerDisplay)
//            assertFalse(initialState.isRunning)
//        }
//    }
//
//    @Test
//    fun `toggle timer starts and stops the timer`() = runTest {
//        presenter.test {
//            val initialState = awaitItem()
//
//            // Start timer
//            initialState.eventSink(PomodoroEvent.ToggleTimer)
//            val runningState = awaitItem()
//            assertTrue(runningState.isRunning)
//
//            // Stop timer
//            runningState.eventSink(PomodoroEvent.ToggleTimer)
//            val stoppedState = awaitItem()
//            assertFalse(stoppedState.isRunning)
//        }
//    }
//
//    @Test
//    fun `reset returns to initial state`() = runTest {
//        presenter.test {
//            val initialState = awaitItem()
//
//            // Start timer and modify state
//            initialState.eventSink(PomodoroEvent.ToggleTimer)
//            skipItems(1) // Skip the running state
//
//            // Reset
//            initialState.eventSink(PomodoroEvent.Reset)
//            val resetState = awaitItem()
//
//            assertEquals(PomodoroPhase.WORK, resetState.currentPhase)
//            assertEquals(25 * 60, resetState.timeRemainingSeconds)
//            assertEquals("25:00", resetState.timerDisplay)
//            assertFalse(resetState.isRunning)
//        }
//    }
//
//    @Test
//    fun `skip transitions to short break`() = runTest {
//        presenter.test {
//            val initialState = awaitItem()
//
//            // Skip work phase -> should go to short break
//            initialState.eventSink(PomodoroEvent.Skip)
//            val shortBreakState = awaitItem()
//
//            assertEquals(PomodoroPhase.SHORT_BREAK, shortBreakState.currentPhase)
//            assertEquals(5 * 60, shortBreakState.timeRemainingSeconds)
//            assertEquals("05:00", shortBreakState.timerDisplay)
//        }
//    }
//}
