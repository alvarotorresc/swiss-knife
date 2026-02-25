package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.RandomNumberError
import com.alvarotc.swissknife.viewmodel.RandomNumberViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RandomNumberViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state has default range and no result`() {
        val vm = RandomNumberViewModel()
        val state = vm.uiState.value
        assertEquals("1", state.minText)
        assertEquals("100", state.maxText)
        assertNull(state.result)
        assertNull(state.error)
    }

    @Test
    fun `generate produces number in range`() {
        val vm = RandomNumberViewModel()
        vm.setMin("10")
        vm.setMax("20")
        vm.generate()
        val state = vm.uiState.value
        assertNotNull(state.result)
        assertTrue(state.result!! in 10..20)
        assertNull(state.error)
    }

    @Test
    fun `generate shows error when min equals max`() {
        val vm = RandomNumberViewModel()
        vm.setMin("5")
        vm.setMax("5")
        vm.generate()
        assertNotNull(vm.uiState.value.error)
        assertNull(vm.uiState.value.result)
    }

    @Test
    fun `generate shows error when min greater than max`() {
        val vm = RandomNumberViewModel()
        vm.setMin("50")
        vm.setMax("10")
        vm.generate()
        assertEquals(RandomNumberError.MinNotLessThanMax, vm.uiState.value.error)
    }

    @Test
    fun `generate shows error for invalid input`() {
        val vm = RandomNumberViewModel()
        vm.setMin("abc")
        vm.setMax("100")
        vm.generate()
        assertEquals(RandomNumberError.InvalidNumbers, vm.uiState.value.error)
    }

    @Test
    fun `setMin clears error`() {
        val vm = RandomNumberViewModel()
        vm.setMin("abc")
        vm.generate()
        assertNotNull(vm.uiState.value.error)
        vm.setMin("1")
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `generate with negative numbers works`() {
        val vm = RandomNumberViewModel()
        vm.setMin("-10")
        vm.setMax("10")
        vm.generate()
        assertNotNull(vm.uiState.value.result)
        assertTrue(vm.uiState.value.result!! in -10..10)
    }
}
