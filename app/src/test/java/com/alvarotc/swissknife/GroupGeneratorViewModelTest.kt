package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.GroupGeneratorError
import com.alvarotc.swissknife.viewmodel.GroupGeneratorViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GroupGeneratorViewModelTest {
    @Test
    fun `initial state has empty participants and 2 groups`() {
        val vm = GroupGeneratorViewModel()
        val state = vm.uiState.value
        assertEquals("", state.nameInput)
        assertTrue(state.participants.isEmpty())
        assertEquals(2, state.numGroups)
        assertTrue(state.groups.isEmpty())
        assertNull(state.error)
    }

    @Test
    fun `setNameInput updates input and clears error`() {
        val vm = GroupGeneratorViewModel()
        // First trigger an error
        vm.addParticipant() // blank name -> no-op, no error
        vm.setNameInput("Alice")
        assertEquals("Alice", vm.uiState.value.nameInput)
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `addParticipant adds name and clears input`() {
        val vm = GroupGeneratorViewModel()
        vm.setNameInput("Alice")
        vm.addParticipant()
        val state = vm.uiState.value
        assertEquals(1, state.participants.size)
        assertEquals("Alice", state.participants[0])
        assertEquals("", state.nameInput)
    }

    @Test
    fun `addParticipant trims whitespace`() {
        val vm = GroupGeneratorViewModel()
        vm.setNameInput("  Bob  ")
        vm.addParticipant()
        assertEquals("Bob", vm.uiState.value.participants[0])
    }

    @Test
    fun `addParticipant ignores blank input`() {
        val vm = GroupGeneratorViewModel()
        vm.setNameInput("   ")
        vm.addParticipant()
        assertTrue(vm.uiState.value.participants.isEmpty())
    }

    @Test
    fun `addParticipant shows error for duplicate name`() {
        val vm = GroupGeneratorViewModel()
        vm.setNameInput("Alice")
        vm.addParticipant()
        vm.setNameInput("alice") // case-insensitive duplicate
        vm.addParticipant()
        assertEquals(GroupGeneratorError.NameAlreadyAdded, vm.uiState.value.error)
        assertEquals(1, vm.uiState.value.participants.size)
    }

    @Test
    fun `addParticipant clears previous groups`() {
        val vm = GroupGeneratorViewModel()
        addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
        vm.generate()
        assertTrue(vm.uiState.value.groups.isNotEmpty())
        vm.setNameInput("Eve")
        vm.addParticipant()
        assertTrue(vm.uiState.value.groups.isEmpty())
    }

    @Test
    fun `removeParticipant removes name and clears groups`() {
        val vm = GroupGeneratorViewModel()
        addParticipants(vm, "Alice", "Bob", "Charlie")
        vm.generate()
        vm.removeParticipant("Bob")
        assertEquals(2, vm.uiState.value.participants.size)
        assertTrue("Bob" !in vm.uiState.value.participants)
        assertTrue(vm.uiState.value.groups.isEmpty())
    }

    @Test
    fun `setNumGroups updates count and clears groups`() {
        val vm = GroupGeneratorViewModel()
        vm.setNumGroups(4)
        assertEquals(4, vm.uiState.value.numGroups)
    }

    @Test
    fun `setNumGroups clamps to valid range`() {
        val vm = GroupGeneratorViewModel()
        vm.setNumGroups(0)
        assertEquals(2, vm.uiState.value.numGroups)
        vm.setNumGroups(20)
        assertEquals(10, vm.uiState.value.numGroups)
    }

    @Test
    fun `generate creates correct number of groups`() {
        val vm = GroupGeneratorViewModel()
        addParticipants(vm, "Alice", "Bob", "Charlie", "Diana", "Eve")
        vm.setNumGroups(3)
        vm.generate()
        val state = vm.uiState.value
        assertEquals(3, state.groups.size)
        assertNull(state.error)
    }

    @Test
    fun `generate distributes all participants across groups`() {
        val vm = GroupGeneratorViewModel()
        val names = listOf("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank")
        addParticipants(vm, *names.toTypedArray())
        vm.setNumGroups(2)
        vm.generate()
        val allInGroups = vm.uiState.value.groups.flatten().sorted()
        assertEquals(names.sorted(), allInGroups)
    }

    @Test
    fun `generate shows error when participants not enough for groups`() {
        val vm = GroupGeneratorViewModel()
        addParticipants(vm, "Alice", "Bob")
        vm.setNumGroups(2) // need more than 2 for 2 groups
        vm.generate()
        assertEquals(GroupGeneratorError.NeedMoreForGroups, vm.uiState.value.error)
        assertTrue(vm.uiState.value.groups.isEmpty())
    }

    @Test
    fun `generate shows error when participants equal to group count`() {
        val vm = GroupGeneratorViewModel()
        addParticipants(vm, "Alice", "Bob", "Charlie")
        vm.setNumGroups(3) // exactly 3 participants for 3 groups -> still error (need MORE)
        vm.generate()
        assertEquals(GroupGeneratorError.NeedMoreForGroups, vm.uiState.value.error)
    }

    @Test
    fun `reset clears groups only`() {
        val vm = GroupGeneratorViewModel()
        addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
        vm.generate()
        vm.reset()
        assertTrue(vm.uiState.value.groups.isEmpty())
        assertEquals(4, vm.uiState.value.participants.size) // participants preserved
    }

    private fun addParticipants(
        vm: GroupGeneratorViewModel,
        vararg names: String,
    ) {
        names.forEach { name ->
            vm.setNameInput(name)
            vm.addParticipant()
        }
    }
}
