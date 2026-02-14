package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.CoinFlipViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CoinFlipViewModelTest {
    @Test
    fun `initial state has no result and zero counters`() {
        val vm = CoinFlipViewModel()
        val state = vm.uiState.value
        assertNull(state.result)
        assertEquals(0, state.totalFlips)
        assertEquals(0, state.headsCount)
        assertEquals(0, state.tailsCount)
    }

    @Test
    fun `flip updates counters correctly`() {
        val vm = CoinFlipViewModel()
        vm.flip()
        val state = vm.uiState.value
        assertEquals(1, state.totalFlips)
        assertNotNull(state.result)
        assertEquals(1, state.headsCount + state.tailsCount)
    }

    @Test
    fun `multiple flips increment total`() {
        val vm = CoinFlipViewModel()
        repeat(10) { vm.flip() }
        val state = vm.uiState.value
        assertEquals(10, state.totalFlips)
        assertEquals(10, state.headsCount + state.tailsCount)
    }

    @Test
    fun `reset clears all state`() {
        val vm = CoinFlipViewModel()
        repeat(5) { vm.flip() }
        vm.reset()
        val state = vm.uiState.value
        assertNull(state.result)
        assertEquals(0, state.totalFlips)
        assertEquals(0, state.headsCount)
        assertEquals(0, state.tailsCount)
    }

    @Test
    fun `flip produces both heads and tails over many flips`() {
        val vm = CoinFlipViewModel()
        repeat(100) { vm.flip() }
        val state = vm.uiState.value
        assertTrue("Expected some heads", state.headsCount > 0)
        assertTrue("Expected some tails", state.tailsCount > 0)
    }

    @Test
    fun `flip sets isFlipping to true`() {
        val vm = CoinFlipViewModel()
        vm.flip()
        assertTrue(vm.uiState.value.isFlipping)
    }

    @Test
    fun `onAnimationFinished sets isFlipping to false`() {
        val vm = CoinFlipViewModel()
        vm.flip()
        vm.onAnimationFinished()
        assertEquals(false, vm.uiState.value.isFlipping)
    }
}
