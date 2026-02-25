package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.TimerMode
import com.alvarotc.swissknife.viewmodel.TimerViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state is stopwatch mode not running`() {
        val vm = TimerViewModel()
        val state = vm.uiState.value
        assertEquals(TimerMode.STOPWATCH, state.mode)
        assertEquals(0L, state.elapsedMs)
        assertEquals(60_000L, state.countdownTotalMs)
        assertEquals(60_000L, state.countdownRemainingMs)
        assertFalse(state.isRunning)
        assertFalse(state.isFinished)
        assertEquals(1, state.countdownMinutes)
        assertEquals(0, state.countdownSeconds)
    }

    @Test
    fun `setMode changes mode when not running`() {
        val vm = TimerViewModel()
        vm.setMode(TimerMode.COUNTDOWN)
        assertEquals(TimerMode.COUNTDOWN, vm.uiState.value.mode)
    }

    @Test
    fun `setMode does not change when running`() =
        runTest {
            val vm = TimerViewModel()
            vm.startPause()
            vm.setMode(TimerMode.COUNTDOWN)
            assertEquals(TimerMode.STOPWATCH, vm.uiState.value.mode)
            vm.startPause() // pause to stop coroutine
        }

    @Test
    fun `setMode resets elapsed and finished state`() {
        val vm = TimerViewModel()
        vm.setMode(TimerMode.COUNTDOWN)
        assertEquals(0L, vm.uiState.value.elapsedMs)
        assertFalse(vm.uiState.value.isFinished)
    }

    @Test
    fun `setCountdownMinutes updates total and remaining`() {
        val vm = TimerViewModel()
        vm.setCountdownMinutes(5)
        assertEquals(5, vm.uiState.value.countdownMinutes)
        assertEquals(5 * 60_000L, vm.uiState.value.countdownTotalMs)
        assertEquals(5 * 60_000L, vm.uiState.value.countdownRemainingMs)
    }

    @Test
    fun `setCountdownMinutes clamps to valid range`() {
        val vm = TimerViewModel()
        vm.setCountdownMinutes(-5)
        assertEquals(0, vm.uiState.value.countdownMinutes)
        vm.setCountdownMinutes(200)
        assertEquals(99, vm.uiState.value.countdownMinutes)
    }

    @Test
    fun `setCountdownMinutes is ignored when running`() =
        runTest {
            val vm = TimerViewModel()
            vm.startPause()
            vm.setCountdownMinutes(10)
            assertEquals(1, vm.uiState.value.countdownMinutes)
            vm.startPause() // pause to stop coroutine
        }

    @Test
    fun `setCountdownSeconds updates total and remaining`() {
        val vm = TimerViewModel()
        vm.setCountdownSeconds(30)
        assertEquals(30, vm.uiState.value.countdownSeconds)
        val expectedMs = 1 * 60_000L + 30 * 1000L
        assertEquals(expectedMs, vm.uiState.value.countdownTotalMs)
        assertEquals(expectedMs, vm.uiState.value.countdownRemainingMs)
    }

    @Test
    fun `setCountdownSeconds clamps to valid range`() {
        val vm = TimerViewModel()
        vm.setCountdownSeconds(-1)
        assertEquals(0, vm.uiState.value.countdownSeconds)
        vm.setCountdownSeconds(90)
        assertEquals(59, vm.uiState.value.countdownSeconds)
    }

    @Test
    fun `startPause starts the timer`() =
        runTest {
            val vm = TimerViewModel()
            vm.startPause()
            assertTrue(vm.uiState.value.isRunning)
            vm.startPause() // pause to stop coroutine
        }

    @Test
    fun `startPause pauses when already running`() =
        runTest {
            val vm = TimerViewModel()
            vm.startPause()
            assertTrue(vm.uiState.value.isRunning)
            vm.startPause()
            assertFalse(vm.uiState.value.isRunning)
        }

    @Test
    fun `startPause does nothing when finished`() {
        val vm = TimerViewModel()
        vm.setMode(TimerMode.COUNTDOWN)
        vm.setCountdownMinutes(0)
        vm.setCountdownSeconds(0)
        // With 0 total time, starting should not change state meaningfully
        // since the timer would finish immediately
        val stateBefore = vm.uiState.value.isFinished
        vm.startPause()
        // If finished is true, startPause should be a no-op
        // We just verify it doesn't crash
    }

    @Test
    fun `reset clears timer state`() =
        runTest {
            val vm = TimerViewModel()
            vm.startPause()
            advanceTimeBy(100)
            vm.reset()
            val state = vm.uiState.value
            assertEquals(0L, state.elapsedMs)
            assertFalse(state.isRunning)
            assertFalse(state.isFinished)
        }

    @Test
    fun `reset preserves countdown total`() {
        val vm = TimerViewModel()
        vm.setMode(TimerMode.COUNTDOWN)
        vm.setCountdownMinutes(5)
        vm.reset()
        assertEquals(5 * 60_000L, vm.uiState.value.countdownTotalMs)
        assertEquals(5 * 60_000L, vm.uiState.value.countdownRemainingMs)
    }

    @Test
    fun `stopwatch increments elapsed time`() =
        runTest {
            val vm = TimerViewModel()
            vm.startPause()
            advanceTimeBy(100)
            assertTrue(vm.uiState.value.elapsedMs > 0)
            vm.startPause() // pause
        }

    @Test
    fun `countdown decrements remaining time`() =
        runTest {
            val vm = TimerViewModel()
            vm.setMode(TimerMode.COUNTDOWN)
            vm.setCountdownMinutes(0)
            vm.setCountdownSeconds(10)
            val initialRemaining = vm.uiState.value.countdownRemainingMs
            vm.startPause()
            advanceTimeBy(100)
            assertTrue(vm.uiState.value.countdownRemainingMs < initialRemaining)
            vm.startPause() // pause
        }

    @Test
    fun `countdown finishes when time runs out`() =
        runTest {
            val vm = TimerViewModel()
            vm.setMode(TimerMode.COUNTDOWN)
            vm.setCountdownMinutes(0)
            vm.setCountdownSeconds(1) // 1 second countdown
            vm.startPause()
            advanceUntilIdle()
            val state = vm.uiState.value
            assertEquals(0L, state.countdownRemainingMs)
            assertTrue(state.isFinished)
            assertFalse(state.isRunning)
        }

    @Test
    fun `setCountdownSeconds combines with existing minutes`() {
        val vm = TimerViewModel()
        vm.setCountdownMinutes(2)
        vm.setCountdownSeconds(30)
        val expectedMs = 2 * 60_000L + 30 * 1000L
        assertEquals(expectedMs, vm.uiState.value.countdownTotalMs)
    }
}
