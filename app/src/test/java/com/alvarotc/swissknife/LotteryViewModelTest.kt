package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.LotteryError
import com.alvarotc.swissknife.viewmodel.LotteryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LotteryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state has default values and no results`() {
        val vm = LotteryViewModel()
        val state = vm.uiState.value
        assertEquals(49, state.maxNumber)
        assertEquals("49", state.maxText)
        assertEquals(6, state.count)
        assertEquals("6", state.countText)
        assertTrue(state.results.isEmpty())
        assertEquals(false, state.isDrawing)
        assertEquals(0, state.revealedCount)
        assertNull(state.error)
    }

    @Test
    fun `setMaxNumber updates text and parsed value`() {
        val vm = LotteryViewModel()
        vm.setMaxNumber("30")
        assertEquals("30", vm.uiState.value.maxText)
        assertEquals(30, vm.uiState.value.maxNumber)
    }

    @Test
    fun `setMaxNumber clears error`() {
        val vm = LotteryViewModel()
        vm.setMaxNumber("abc")
        vm.draw()
        assertNotNull(vm.uiState.value.error)
        vm.setMaxNumber("49")
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `setMaxNumber keeps previous value for invalid text`() {
        val vm = LotteryViewModel()
        vm.setMaxNumber("abc")
        assertEquals("abc", vm.uiState.value.maxText)
        assertEquals(49, vm.uiState.value.maxNumber) // previous default preserved
    }

    @Test
    fun `setCount updates text and parsed value`() {
        val vm = LotteryViewModel()
        vm.setCount("3")
        assertEquals("3", vm.uiState.value.countText)
        assertEquals(3, vm.uiState.value.count)
    }

    @Test
    fun `setCount clears error`() {
        val vm = LotteryViewModel()
        vm.setCount("abc")
        vm.draw()
        assertNotNull(vm.uiState.value.error)
        vm.setCount("6")
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `draw produces correct number of results`() =
        runTest {
            val vm = LotteryViewModel()
            vm.setMaxNumber("49")
            vm.setCount("6")
            vm.draw()
            advanceUntilIdle()
            assertEquals(6, vm.uiState.value.results.size)
        }

    @Test
    fun `draw results are sorted`() =
        runTest {
            val vm = LotteryViewModel()
            vm.draw()
            advanceUntilIdle()
            val results = vm.uiState.value.results
            assertEquals(results.sorted(), results)
        }

    @Test
    fun `draw results are within valid range`() =
        runTest {
            val vm = LotteryViewModel()
            vm.setMaxNumber("20")
            vm.setCount("5")
            vm.draw()
            advanceUntilIdle()
            vm.uiState.value.results.forEach { number ->
                assertTrue("Number $number should be >= 1", number >= 1)
                assertTrue("Number $number should be <= 20", number <= 20)
            }
        }

    @Test
    fun `draw results have no duplicates`() =
        runTest {
            val vm = LotteryViewModel()
            vm.draw()
            advanceUntilIdle()
            val results = vm.uiState.value.results
            assertEquals(results.size, results.toSet().size)
        }

    @Test
    fun `draw reveals all numbers after animation`() =
        runTest {
            val vm = LotteryViewModel()
            vm.draw()
            advanceUntilIdle()
            val state = vm.uiState.value
            assertEquals(state.results.size, state.revealedCount)
            assertEquals(false, state.isDrawing)
        }

    @Test
    fun `draw shows error for invalid max number`() {
        val vm = LotteryViewModel()
        vm.setMaxNumber("abc")
        vm.draw()
        assertEquals(LotteryError.InvalidNumbers, vm.uiState.value.error)
        assertTrue(vm.uiState.value.results.isEmpty())
    }

    @Test
    fun `draw shows error for invalid count`() {
        val vm = LotteryViewModel()
        vm.setCount("abc")
        vm.draw()
        assertEquals(LotteryError.InvalidNumbers, vm.uiState.value.error)
    }

    @Test
    fun `draw shows error when count exceeds max`() {
        val vm = LotteryViewModel()
        vm.setMaxNumber("5")
        vm.setCount("10")
        vm.draw()
        assertEquals(LotteryError.CountExceedsRange, vm.uiState.value.error)
    }

    @Test
    fun `draw shows error when max is zero`() {
        val vm = LotteryViewModel()
        vm.setMaxNumber("0")
        vm.draw()
        assertEquals(LotteryError.InvalidNumbers, vm.uiState.value.error)
    }

    @Test
    fun `draw shows error when count is zero`() {
        val vm = LotteryViewModel()
        vm.setCount("0")
        vm.draw()
        assertEquals(LotteryError.InvalidNumbers, vm.uiState.value.error)
    }

    @Test
    fun `reset clears results and error`() =
        runTest {
            val vm = LotteryViewModel()
            vm.draw()
            advanceUntilIdle()
            vm.reset()
            val state = vm.uiState.value
            assertTrue(state.results.isEmpty())
            assertEquals(0, state.revealedCount)
            assertEquals(false, state.isDrawing)
            assertNull(state.error)
        }

    @Test
    fun `draw works when count equals max`() =
        runTest {
            val vm = LotteryViewModel()
            vm.setMaxNumber("5")
            vm.setCount("5")
            vm.draw()
            advanceUntilIdle()
            val results = vm.uiState.value.results
            assertEquals(5, results.size)
            assertEquals(listOf(1, 2, 3, 4, 5), results)
        }
}
