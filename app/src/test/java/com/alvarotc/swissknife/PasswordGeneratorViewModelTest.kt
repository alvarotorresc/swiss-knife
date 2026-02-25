package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.PasswordError
import com.alvarotc.swissknife.viewmodel.PasswordGeneratorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordGeneratorViewModelTest {
    @Test
    fun `initial state has default values and no password`() {
        val vm = PasswordGeneratorViewModel()
        val state = vm.uiState.value
        assertEquals(16, state.length)
        assertTrue(state.includeUppercase)
        assertTrue(state.includeLowercase)
        assertTrue(state.includeNumbers)
        assertEquals(false, state.includeSymbols)
        assertNull(state.password)
        assertNull(state.error)
    }

    @Test
    fun `generate produces password of correct length`() {
        val vm = PasswordGeneratorViewModel()
        vm.generate()
        val state = vm.uiState.value
        assertNotNull(state.password)
        assertEquals(16, state.password!!.length)
    }

    @Test
    fun `setLength updates length within bounds`() {
        val vm = PasswordGeneratorViewModel()
        vm.setLength(32)
        assertEquals(32, vm.uiState.value.length)
        vm.setLength(4)
        assertEquals(8, vm.uiState.value.length)
        vm.setLength(100)
        assertEquals(64, vm.uiState.value.length)
    }

    @Test
    fun `generate with only uppercase produces uppercase password`() {
        val vm = PasswordGeneratorViewModel()
        vm.toggleLowercase()
        vm.toggleNumbers()
        vm.generate()
        val password = vm.uiState.value.password!!
        assertTrue(password.all { it.isUpperCase() })
    }

    @Test
    fun `generate with only symbols produces symbol password`() {
        val vm = PasswordGeneratorViewModel()
        vm.toggleUppercase()
        vm.toggleLowercase()
        vm.toggleNumbers()
        vm.toggleSymbols()
        vm.generate()
        val password = vm.uiState.value.password!!
        val symbols = "!@#\$%^&*()_+-=[]{}|;:,.<>?"
        assertTrue(password.all { it in symbols })
    }

    @Test
    fun `generate with no character types shows error`() {
        val vm = PasswordGeneratorViewModel()
        vm.toggleUppercase()
        vm.toggleLowercase()
        vm.toggleNumbers()
        vm.generate()
        assertEquals(PasswordError.NoCharacterTypeSelected, vm.uiState.value.error)
        assertNull(vm.uiState.value.password)
    }

    @Test
    fun `toggle clears error`() {
        val vm = PasswordGeneratorViewModel()
        vm.toggleUppercase()
        vm.toggleLowercase()
        vm.toggleNumbers()
        vm.generate()
        assertNotNull(vm.uiState.value.error)
        vm.toggleUppercase()
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `generate produces different passwords`() {
        val vm = PasswordGeneratorViewModel()
        vm.generate()
        val first = vm.uiState.value.password
        vm.generate()
        val second = vm.uiState.value.password
        // Extremely unlikely to be equal with 16 chars
        assertTrue("Expected different passwords", first != second)
    }
}
