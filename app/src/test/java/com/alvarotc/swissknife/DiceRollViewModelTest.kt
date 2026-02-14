package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.DiceRollViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiceRollViewModelTest {
    @Test
    fun `initial state has 1 die and no results`() {
        val vm = DiceRollViewModel()
        val state = vm.uiState.value
        assertEquals(1, state.diceCount)
        assertTrue(state.results.isEmpty())
        assertEquals(false, state.isRolling)
    }

    @Test
    fun `setDiceCount updates count`() {
        val vm = DiceRollViewModel()
        vm.setDiceCount(3)
        assertEquals(3, vm.uiState.value.diceCount)
    }

    @Test
    fun `setDiceCount clamps to valid range`() {
        val vm = DiceRollViewModel()
        vm.setDiceCount(0)
        assertEquals(1, vm.uiState.value.diceCount)
        vm.setDiceCount(5)
        assertEquals(4, vm.uiState.value.diceCount)
    }

    @Test
    fun `setDiceCount clears previous results`() {
        val vm = DiceRollViewModel()
        // Simulate some results existing
        vm.setDiceCount(2)
        assertTrue(vm.uiState.value.results.isEmpty())
    }
}
