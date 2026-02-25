package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.FortuneWheelError
import com.alvarotc.swissknife.viewmodel.FortuneWheelViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FortuneWheelViewModelTest {
    @Test
    fun `initial state is empty`() {
        val vm = FortuneWheelViewModel()
        val state = vm.uiState.value
        assertEquals("", state.itemInput)
        assertTrue(state.items.isEmpty())
        assertEquals(false, state.isSpinning)
        assertEquals(0f, state.rotation)
        assertNull(state.winner)
        assertNull(state.error)
    }

    @Test
    fun `addItem adds item and clears input`() {
        val vm = FortuneWheelViewModel()
        vm.setItemInput("Option A")
        vm.addItem()
        val state = vm.uiState.value
        assertEquals(listOf("Option A"), state.items)
        assertEquals("", state.itemInput)
    }

    @Test
    fun `addItem trims whitespace`() {
        val vm = FortuneWheelViewModel()
        vm.setItemInput("  Option A  ")
        vm.addItem()
        assertEquals(listOf("Option A"), vm.uiState.value.items)
    }

    @Test
    fun `addItem ignores blank input`() {
        val vm = FortuneWheelViewModel()
        vm.setItemInput("   ")
        vm.addItem()
        assertTrue(vm.uiState.value.items.isEmpty())
    }

    @Test
    fun `addItem shows error for duplicate`() {
        val vm = FortuneWheelViewModel()
        vm.setItemInput("Option A")
        vm.addItem()
        vm.setItemInput("option a")
        vm.addItem()
        assertEquals(FortuneWheelError.ItemAlreadyAdded, vm.uiState.value.error)
        assertEquals(1, vm.uiState.value.items.size)
    }

    @Test
    fun `removeItem removes item`() {
        val vm = FortuneWheelViewModel()
        vm.setItemInput("A")
        vm.addItem()
        vm.setItemInput("B")
        vm.addItem()
        vm.removeItem("A")
        assertEquals(listOf("B"), vm.uiState.value.items)
    }

    @Test
    fun `spin with fewer than 2 items shows error`() {
        val vm = FortuneWheelViewModel()
        vm.setItemInput("A")
        vm.addItem()
        vm.spin()
        assertEquals(FortuneWheelError.NeedMoreItems, vm.uiState.value.error)
    }

    @Test
    fun `clearWinner clears winner`() {
        val vm = FortuneWheelViewModel()
        vm.clearWinner()
        assertNull(vm.uiState.value.winner)
    }

    @Test
    fun `reset clears all state`() {
        val vm = FortuneWheelViewModel()
        vm.setItemInput("A")
        vm.addItem()
        vm.setItemInput("B")
        vm.addItem()
        vm.reset()
        val state = vm.uiState.value
        assertTrue(state.items.isEmpty())
        assertEquals("", state.itemInput)
        assertNull(state.winner)
    }

    @Test
    fun `setItemInput clears error`() {
        val vm = FortuneWheelViewModel()
        vm.setItemInput("A")
        vm.addItem()
        vm.setItemInput("a")
        vm.addItem()
        assertEquals(FortuneWheelError.ItemAlreadyAdded, vm.uiState.value.error)
        vm.setItemInput("B")
        assertNull(vm.uiState.value.error)
    }
}
