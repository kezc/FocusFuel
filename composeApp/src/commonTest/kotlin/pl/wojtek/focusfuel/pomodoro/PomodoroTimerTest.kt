package pl.wojtek.focusfuel.pomodoro

import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Logger
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import pl.wojtek.focusfuel.features.pomodoro.PomodoroPhase
import pl.wojtek.focusfuel.features.pomodoro.PomodoroSaver
import pl.wojtek.focusfuel.features.pomodoro.PomodoroTimer
import pl.wojtek.focusfuel.features.pomodoro.PomodoroTimerState
import pl.wojtek.focusfuel.repository.PomodorosRepository
import pl.wojtek.focusfuel.util.datetime.TimestampProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class PomodoroTimerTest {

    private lateinit var pomodoroSaver: FakePomodoroSaver
    private val testDispatcher = UnconfinedTestDispatcher()
    private val timestampProvider = object : TimestampProvider {
        override fun getTimestamp(): Long = testDispatcher.scheduler.currentTime
    }
    private val fakePomodoroDao = object : PomodorosRepository {
        override fun addPomodoro(date: LocalDateTime) {}

        override suspend fun getTotalPomodorosFinished(): Int {
            return 0
        }
    }

    private fun createSut(): PomodoroTimer {
        Logger.setLogWriters(CommonWriter())
        pomodoroSaver = FakePomodoroSaver()
        return PomodoroTimer(
            pomodoroSaver = pomodoroSaver,
            timestampProvider = timestampProvider,
            coroutineScope = TestScope(testDispatcher),
            pomodorosRepository = fakePomodoroDao
        ).also { it.init() }
    }

    @Test
    fun `GIVEN initial state WHEN initialized THEN it is work phase with correct duration`() = runTest {
        createSut().state.logTest {
            val initialState = awaitItem()
            assertEquals(PomodoroPhase.WORK, initialState.currentPhase)
            assertEquals(PomodoroTimer.WORK_TIME_MS, initialState.timeRemainingMs)
            assertFalse(initialState.isRunning)
            assertEquals(0, initialState.completedPomodoros)
        }
    }

    @Test
    fun `GIVEN timer is stopped WHEN toggleTimer is called THEN it starts and stops correctly`() =
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
    fun `GIVEN timer is running WHEN reset is called THEN it returns to initial state`() = runTest {
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
    fun `GIVEN work phase WHEN skip is called THEN it transitions to short break`() = runTest {
        val pomodoroTimer = createSut()
        pomodoroTimer.state.logTest {
            pomodoroTimer.skip()
            val state = expectMostRecentItem()
            assertEquals(PomodoroPhase.SHORT_BREAK, state.currentPhase)
            assertEquals(PomodoroTimer.SHORT_BREAK_TIME_MS, state.timeRemainingMs)
        }
    }

    @Test
    fun `GIVEN short break WHEN skip is called THEN it transitions to work`() = runTest {
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
    fun `GIVEN 4 completed work phases WHEN skip is called THEN it transitions to long break`() =
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
    fun `GIVEN timer is running WHEN time advances THEN it decrements correctly`() = runTest {
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
    fun `GIVEN timer is running WHEN time runs out THEN it transitions phase`() = runTest {
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

    @Test
    fun `GIVEN state is running WHEN save is called THEN it persists the state`() = runTest {
        val pomodoroTimer = createSut()
        pomodoroTimer.toggleTimer()
        pomodoroTimer.save()

        val savedState = pomodoroSaver.loadState()
        assertTrue(savedState.isRunning)
    }
}

class FakePomodoroSaver : PomodoroSaver {
    private var state: PomodoroTimerState = PomodoroTimerState()

    override fun saveState(state: PomodoroTimerState) {
        this.state = state
    }

    override fun loadState(): PomodoroTimerState = state
}

suspend fun <T> Flow<T>.logTest(
    timeout: Duration? = null,
    name: String? = null,
    validate: suspend TurbineTestContext<T>.() -> Unit,
) {
    onEach { Logger.d("TEST") { it.toString() } }.test(timeout, name, validate)
}
