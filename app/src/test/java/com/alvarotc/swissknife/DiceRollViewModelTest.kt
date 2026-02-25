package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.DiceRollViewModel
import com.alvarotc.swissknife.viewmodel.DiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DiceRollViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state has D6 1 die and no results`() {
        val vm = DiceRollViewModel()
        val state = vm.uiState.value
        assertEquals(DiceType.D6, state.diceType)
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
        vm.setDiceCount(2)
        assertTrue(vm.uiState.value.results.isEmpty())
    }

    @Test
    fun `setDiceType updates type and clears results`() {
        val vm = DiceRollViewModel()
        vm.setDiceType(DiceType.D20)
        assertEquals(DiceType.D20, vm.uiState.value.diceType)
        assertTrue(vm.uiState.value.results.isEmpty())
    }

    @Test
    fun `setDiceType preserves dice count`() {
        val vm = DiceRollViewModel()
        vm.setDiceCount(3)
        vm.setDiceType(DiceType.D12)
        assertEquals(3, vm.uiState.value.diceCount)
        assertEquals(DiceType.D12, vm.uiState.value.diceType)
    }
}
