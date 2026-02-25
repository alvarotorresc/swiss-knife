package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.RandomListError
import com.alvarotc.swissknife.viewmodel.RandomListViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RandomListViewModelTest {
    @Test
    fun `initial state is empty`() {
        val vm = RandomListViewModel()
        val state = vm.uiState.value
        assertEquals("", state.itemInput)
        assertTrue(state.items.isEmpty())
        assertNull(state.result)
        assertNull(state.error)
    }

    @Test
    fun `addItem adds item and clears input`() {
        val vm = RandomListViewModel()
        vm.setItemInput("Apple")
        vm.addItem()
        val state = vm.uiState.value
        assertEquals(listOf("Apple"), state.items)
        assertEquals("", state.itemInput)
    }

    @Test
    fun `addItem trims whitespace`() {
        val vm = RandomListViewModel()
        vm.setItemInput("  Apple  ")
        vm.addItem()
        assertEquals(listOf("Apple"), vm.uiState.value.items)
    }

    @Test
    fun `addItem ignores blank input`() {
        val vm = RandomListViewModel()
        vm.setItemInput("   ")
        vm.addItem()
        assertTrue(vm.uiState.value.items.isEmpty())
    }

    @Test
    fun `addItem shows error for duplicate`() {
        val vm = RandomListViewModel()
        vm.setItemInput("Apple")
        vm.addItem()
        vm.setItemInput("apple")
        vm.addItem()
        assertEquals(RandomListError.ItemAlreadyAdded, vm.uiState.value.error)
        assertEquals(1, vm.uiState.value.items.size)
    }

    @Test
    fun `removeItem removes item`() {
        val vm = RandomListViewModel()
        vm.setItemInput("Apple")
        vm.addItem()
        vm.setItemInput("Banana")
        vm.addItem()
        vm.removeItem("Apple")
        assertEquals(listOf("Banana"), vm.uiState.value.items)
    }

    @Test
    fun `pick with fewer than 2 items shows error`() {
        val vm = RandomListViewModel()
        vm.setItemInput("Apple")
        vm.addItem()
        vm.pick()
        assertEquals(RandomListError.NeedMoreItems, vm.uiState.value.error)
    }

    @Test
    fun `pick selects an item from the list`() {
        val vm = RandomListViewModel()
        vm.setItemInput("Apple")
        vm.addItem()
        vm.setItemInput("Banana")
        vm.addItem()
        vm.pick()
        val result = vm.uiState.value.result
        assertNotNull(result)
        assertTrue(result in listOf("Apple", "Banana"))
    }

    @Test
    fun `reset clears all state`() {
        val vm = RandomListViewModel()
        vm.setItemInput("Apple")
        vm.addItem()
        vm.setItemInput("Banana")
        vm.addItem()
        vm.pick()
        vm.reset()
        val state = vm.uiState.value
        assertTrue(state.items.isEmpty())
        assertNull(state.result)
        assertEquals("", state.itemInput)
    }
}
