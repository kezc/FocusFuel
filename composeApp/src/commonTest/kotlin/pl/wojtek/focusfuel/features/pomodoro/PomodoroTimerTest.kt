package pl.wojtek.focusfuel.features.pomodoro

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import co.touchlab.kermit.Logger
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import pl.wojtek.focusfuel.repository.PomodorosRepository
import pl.wojtek.focusfuel.util.BaseTest
import pl.wojtek.focusfuel.util.datetime.TimestampProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class PomodoroTimerTest : BaseTest() {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val timestampProvider = object : TimestampProvider {
        override fun getTimestamp(): Long = testDispatcher.scheduler.currentTime
    }
    private val pomodorosRepository: PomodorosRepository = mockk(relaxUnitFun = true)

    private fun createSut(): PomodoroTimer {
        return PomodoroTimer(
            timestampProvider = timestampProvider,
            coroutineScope = TestScope(testDispatcher),
            pomodorosRepository = pomodorosRepository
        )
    }

    @Test
    fun `Given initial state, when initialized, then it is work phase with correct duration`() = runTest {
        createSut().state.logTest {
            val initialState = awaitItem()
            assertEquals(PomodoroPhase.WORK, initialState.currentPhase)
            assertEquals(PomodoroTimer.WORK_TIME_MS, initialState.timeRemainingMs)
            assertFalse(initialState.isRunning)
            assertEquals(0, initialState.completedPomodoros)
        }
    }

    @Test
    fun `Given timer is stopped, when toggleTimer is called, then it starts and stops correctly`() =
        runTest {
            val pomodoroTimer = createSut()
            pomodoroTimer.state.logTest {
                pomodoroTimer.toggleTimer()
                assertTrue(expectMostRecentItem().isRunning)

                pomodoroTimer.toggleTimer()
                assertFalse(expectMostRecentItem().isRunning)
            }
        }

    @Test
    fun `Given timer is running, when reset is called, then it returns to initial state`() = runTest {
        val pomodoroTimer = createSut()
        pomodoroTimer.state.logTest {
            pomodoroTimer.toggleTimer()

            pomodoroTimer.reset()
            val state = expectMostRecentItem()
            assertEquals(PomodoroPhase.WORK, state.currentPhase)
            assertEquals(PomodoroTimer.WORK_TIME_MS, state.timeRemainingMs)
            assertFalse(state.isRunning)
        }
    }

    @Test
    fun `Given work phase, when skip is called, then it transitions to short break`() = runTest {
        val pomodoroTimer = createSut()
        pomodoroTimer.state.logTest {
            pomodoroTimer.skip()
            val state = expectMostRecentItem()
            assertEquals(PomodoroPhase.SHORT_BREAK, state.currentPhase)
            assertEquals(PomodoroTimer.SHORT_BREAK_TIME_MS, state.timeRemainingMs)
        }
    }

    @Test
    fun `Given short break, when skip is called, then it transitions to work`() = runTest {
        val pomodoroTimer = createSut()
        pomodoroTimer.state.logTest {
            pomodoroTimer.skip() // Move to SHORT_BREAK

            pomodoroTimer.skip() // Back to WORK
            val state = expectMostRecentItem()
            assertEquals(PomodoroPhase.WORK, state.currentPhase)
            assertEquals(PomodoroTimer.WORK_TIME_MS, state.timeRemainingMs)
        }
    }

    @Test
    fun `Given 4 completed work phases, when skip is called, then it transitions to long break`() =
        runTest {
            val pomodoroTimer = createSut()
            pomodoroTimer.state.logTest {
                repeat(3) {
                    pomodoroTimer.skip() // WORK -> SHORT_BREAK
                    pomodoroTimer.skip() // SHORT_BREAK -> WORK
                }
                pomodoroTimer.skip() // WORK -> LONG_BREAK

                val state = expectMostRecentItem()
                assertEquals(PomodoroPhase.LONG_BREAK, state.currentPhase)
                assertEquals(PomodoroTimer.LONG_BREAK_TIME_MS, state.timeRemainingMs)
            }
        }

    @Test
    fun `Given timer is running, when time advances, then it decrements correctly`() = runTest {
        val pomodoroTimer = createSut()
        pomodoroTimer.state.logTest {
            pomodoroTimer.toggleTimer() // Start timer

            testDispatcher.scheduler.advanceTimeBy(2001) // Simulate 2 seconds
            val state = expectMostRecentItem()
            assertTrue(state.isRunning)
            assertEquals(PomodoroTimer.WORK_TIME_MS - 2000, state.timeRemainingMs)
        }
    }

    @Test
    fun `Given timer is running, when time runs out, then it transitions phase`() = runTest {
        val pomodoroTimer = createSut()
        pomodoroTimer.state.logTest {
            pomodoroTimer.skip() // Move to SHORT_BREAK

            pomodoroTimer.toggleTimer()

            testDispatcher.scheduler.advanceTimeBy(PomodoroTimer.SHORT_BREAK_TIME_MS + 1)
            val newState = expectMostRecentItem()
            assertEquals(PomodoroPhase.WORK, newState.currentPhase)
            assertEquals(PomodoroTimer.WORK_TIME_MS, newState.timeRemainingMs)
            assertTrue(newState.isRunning)
        }
    }

}

suspend fun <T> Flow<T>.logTest(
    timeout: Duration? = null,
    name: String? = null,
    validate: suspend TurbineTestContext<T>.() -> Unit,
) {
    onEach { Logger.d("TEST") { it.toString() } }.test(timeout, name, validate)
}
