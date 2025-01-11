package pl.wojtek.focusfuel.features.pomodoro

import com.slack.circuit.test.FakeNavigator
import com.slack.circuit.test.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import pl.wojtek.focusfuel.repository.AppSettings
import pl.wojtek.focusfuel.util.BaseTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PomodoroPresenterTest : BaseTest() {
    private val timerState = MutableStateFlow(PomodoroTimerState())
    private val navigator = FakeNavigator(PomodoroScreen)
    private val pomodoroTimer = mockk<PomodoroTimer>(relaxed = true) {
        coEvery { state } returns timerState
    }
    private val appSettings = mockk<AppSettings> {
        coEvery { isSoundEnabled() } returns true
        coEvery { setSoundEnabled(any()) } returns Unit
    }

    private fun createSut() = PomodoroPresenter(
        pomodoroTimer = pomodoroTimer,
        appSettings = appSettings,
        navigator = navigator
    )

    @Test
    fun `Given presenter is created, when initial state is observed, then shows correct timer state`() = runTest {
        createSut().test {
            val state = expectMostRecentItem()
            assertEquals(PomodoroPhase.WORK, state.currentPhase)
            assertEquals("25:00", state.timerDisplay)
            assertFalse(state.isRunning)
            assertTrue(state.isSoundOn)
        }
    }

    @Test
    fun `Given timer is stopped, when toggle timer clicked, then timer starts`() = runTest {
        createSut().test {
            expectMostRecentItem().eventSink(PomodoroEvent.ToggleTimer)
            timerState.update { it.copy(isRunning = true) }

            assertTrue(expectMostRecentItem().isRunning)
            coVerify { pomodoroTimer.toggleTimer() }
        }
    }

    @Test
    fun `Given timer is running, when toggle timer clicked, then timer stops`() = runTest {
        timerState.update { it.copy(isRunning = true) }

        createSut().test {
            expectMostRecentItem().eventSink(PomodoroEvent.ToggleTimer)
            timerState.update { it.copy(isRunning = false) }

            assertFalse(expectMostRecentItem().isRunning)
            coVerify { pomodoroTimer.toggleTimer() }
        }
    }

    @Test
    fun `Given timer is running, when reset clicked, then timer resets`() = runTest {
        timerState.update { it.copy(isRunning = true, timeRemainingMs = 1000000) }

        createSut().test {
            expectMostRecentItem().eventSink(PomodoroEvent.Reset)
            timerState.update { it.copy(timeRemainingMs = 1500000, isRunning = false) }

            val updatedState = expectMostRecentItem()
            assertEquals("25:00", updatedState.timerDisplay)
            assertFalse(updatedState.isRunning)
            coVerify { pomodoroTimer.reset() }
        }
    }

    @Test
    fun `Given work phase, when skip clicked, then moves to break phase`() = runTest {
        createSut().test {
            expectMostRecentItem().eventSink(PomodoroEvent.Skip)
            timerState.update { it.copy(currentPhase = PomodoroPhase.SHORT_BREAK) }

            assertEquals(PomodoroPhase.SHORT_BREAK, expectMostRecentItem().currentPhase)
            coVerify { pomodoroTimer.skip() }
        }
    }

    @Test
    fun `Given sound is on, when toggle sound clicked, then sound is disabled`() = runTest {
        createSut().test {
            val initialState = expectMostRecentItem()
            assertTrue(initialState.isSoundOn)

            initialState.eventSink(PomodoroEvent.ToggleSound)

            coVerify { appSettings.setSoundEnabled(false) }
            assertFalse(expectMostRecentItem().isSoundOn)
        }
    }

    @Test
    fun `Given sound is off, when toggle sound clicked, then sound is enabled`() = runTest {
        coEvery { appSettings.isSoundEnabled() } returns false

        createSut().test {
            val initialState = expectMostRecentItem()
            assertFalse(initialState.isSoundOn)

            initialState.eventSink(PomodoroEvent.ToggleSound)

            coVerify { appSettings.setSoundEnabled(true) }
            assertTrue(expectMostRecentItem().isSoundOn)
        }
    }

    @Test
    fun `Given timer is running, when back clicked, then navigates back`() = runTest {
        createSut().test {
            expectMostRecentItem().eventSink(PomodoroEvent.Back)
            navigator.awaitPop()
        }
    }

    @Test
    fun `Given timer state changes, when time updates, then shows formatted time`() = runTest {
        createSut().test {
            // Test different time formats
            val testCases = listOf(
                1500000L to "25:00", // 25 minutes
                60000L to "01:00",   // 1 minute
                30000L to "00:30",   // 30 seconds
                0L to "00:00"        // 0 seconds
            )

            testCases.forEach { (timeMs, expected) ->
                timerState.update { it.copy(timeRemainingMs = timeMs) }
                assertEquals(expected, expectMostRecentItem().timerDisplay)
            }
        }
    }
} 
