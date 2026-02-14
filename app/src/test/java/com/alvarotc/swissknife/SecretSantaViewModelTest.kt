package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.SecretSantaViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecretSantaViewModelTest {
    @Test
    fun `initial state is empty`() {
        val vm = SecretSantaViewModel()
        val state = vm.uiState.value
        assertEquals("", state.nameInput)
        assertTrue(state.participants.isEmpty())
        assertTrue(state.assignments.isEmpty())
        assertEquals(0, state.revealedCount)
        assertNull(state.error)
    }

    @Test
    fun `addParticipant adds name to list`() {
        val vm = SecretSantaViewModel()
        vm.setNameInput("Alice")
        vm.addParticipant()
        assertEquals(listOf("Alice"), vm.uiState.value.participants)
        assertEquals("", vm.uiState.value.nameInput)
    }

    @Test
    fun `addParticipant trims whitespace`() {
        val vm = SecretSantaViewModel()
        vm.setNameInput("  Bob  ")
        vm.addParticipant()
        assertEquals(listOf("Bob"), vm.uiState.value.participants)
    }

    @Test
    fun `addParticipant rejects blank names`() {
        val vm = SecretSantaViewModel()
        vm.setNameInput("   ")
        vm.addParticipant()
        assertTrue(vm.uiState.value.participants.isEmpty())
    }

    @Test
    fun `addParticipant rejects duplicate names`() {
        val vm = SecretSantaViewModel()
        vm.setNameInput("Alice")
        vm.addParticipant()
        vm.setNameInput("alice")
        vm.addParticipant()
        assertEquals(1, vm.uiState.value.participants.size)
        assertEquals("Name already added", vm.uiState.value.error)
    }

    @Test
    fun `removeParticipant removes from list`() {
        val vm = SecretSantaViewModel()
        vm.setNameInput("Alice")
        vm.addParticipant()
        vm.setNameInput("Bob")
        vm.addParticipant()
        vm.removeParticipant("Alice")
        assertEquals(listOf("Bob"), vm.uiState.value.participants)
    }

    @Test
    fun `draw requires at least 3 participants`() {
        val vm = SecretSantaViewModel()
        vm.setNameInput("Alice")
        vm.addParticipant()
        vm.setNameInput("Bob")
        vm.addParticipant()
        vm.draw()
        assertNotNull(vm.uiState.value.error)
        assertTrue(vm.uiState.value.assignments.isEmpty())
    }

    @Test
    fun `draw creates valid assignments`() {
        val vm = SecretSantaViewModel()
        listOf("Alice", "Bob", "Charlie").forEach {
            vm.setNameInput(it)
            vm.addParticipant()
        }
        vm.draw()
        val state = vm.uiState.value
        assertEquals(3, state.assignments.size)
        assertNull(state.error)
        assertEquals(0, state.revealedCount)
    }

    @Test
    fun `revealNext increments revealed count`() {
        val vm = SecretSantaViewModel()
        listOf("Alice", "Bob", "Charlie").forEach {
            vm.setNameInput(it)
            vm.addParticipant()
        }
        vm.draw()
        vm.revealNext()
        assertEquals(1, vm.uiState.value.revealedCount)
        vm.revealNext()
        assertEquals(2, vm.uiState.value.revealedCount)
    }

    @Test
    fun `revealNext does not exceed assignment count`() {
        val vm = SecretSantaViewModel()
        listOf("Alice", "Bob", "Charlie").forEach {
            vm.setNameInput(it)
            vm.addParticipant()
        }
        vm.draw()
        repeat(10) { vm.revealNext() }
        assertEquals(3, vm.uiState.value.revealedCount)
    }

    @Test
    fun `reset clears everything`() {
        val vm = SecretSantaViewModel()
        listOf("Alice", "Bob", "Charlie").forEach {
            vm.setNameInput(it)
            vm.addParticipant()
        }
        vm.draw()
        vm.reset()
        val state = vm.uiState.value
        assertTrue(state.participants.isEmpty())
        assertTrue(state.assignments.isEmpty())
        assertEquals(0, state.revealedCount)
    }

    // Derangement algorithm tests
    @Test
    fun `derangement produces no fixed points`() {
        val items = listOf("A", "B", "C", "D", "E")
        repeat(50) {
            val result = SecretSantaViewModel.derangement(items)
            items.zip(result).forEach { (original, shuffled) ->
                assertTrue(
                    "Fixed point found: $original at same position",
                    original != shuffled,
                )
            }
        }
    }

    @Test
    fun `derangement is a bijection`() {
        val items = listOf("A", "B", "C", "D", "E")
        repeat(50) {
            val result = SecretSantaViewModel.derangement(items)
            assertEquals(items.size, result.size)
            assertEquals(items.toSet(), result.toSet())
        }
    }

    @Test
    fun `derangement works with minimum size`() {
        val items = listOf("A", "B")
        val result = SecretSantaViewModel.derangement(items)
        assertEquals(listOf("B", "A"), result)
    }

    @Test
    fun `buildShareText formats correctly`() {
        val vm = SecretSantaViewModel()
        listOf("Alice", "Bob", "Charlie").forEach {
            vm.setNameInput(it)
            vm.addParticipant()
        }
        vm.draw()
        val text = vm.buildShareText()
        assertTrue(text.contains("→"))
        assertEquals(3, text.lines().size)
    }
}
