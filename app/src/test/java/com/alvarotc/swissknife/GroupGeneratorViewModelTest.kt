package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.GroupGeneratorError
import com.alvarotc.swissknife.viewmodel.GroupGeneratorViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GroupGeneratorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testAdjectives = listOf("Thunder", "Shadow", "Iron", "Phantom", "Blazing")
    private val testNouns = listOf("Wolves", "Dragons", "Hawks", "Panthers", "Vipers")

    // --- Existing tests ---

    @Test
    fun `initial state has empty participants and 2 groups`() {
        val vm = GroupGeneratorViewModel()
        val state = vm.uiState.value
        assertEquals("", state.nameInput)
        assertTrue(state.participants.isEmpty())
        assertEquals(2, state.numGroups)
        assertTrue(state.groups.isEmpty())
        assertNull(state.error)
        assertTrue(state.groupNames.isEmpty())
        assertEquals(false, state.isNaming)
    }

    @Test
    fun `setNameInput updates input and clears error`() {
        val vm = GroupGeneratorViewModel()
        vm.addParticipant()
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
        vm.setNameInput("alice")
        vm.addParticipant()
        assertEquals(GroupGeneratorError.NameAlreadyAdded, vm.uiState.value.error)
        assertEquals(1, vm.uiState.value.participants.size)
    }

    @Test
    fun `addParticipant clears previous groups and names`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            assertTrue(vm.uiState.value.groups.isNotEmpty())
            vm.setNameInput("Eve")
            vm.addParticipant()
            assertTrue(vm.uiState.value.groups.isEmpty())
            assertTrue(vm.uiState.value.groupNames.isEmpty())
        }

    @Test
    fun `removeParticipant removes name and clears groups and names`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            vm.removeParticipant("Bob")
            assertEquals(3, vm.uiState.value.participants.size)
            assertTrue("Bob" !in vm.uiState.value.participants)
            assertTrue(vm.uiState.value.groups.isEmpty())
            assertTrue(vm.uiState.value.groupNames.isEmpty())
        }

    @Test
    fun `setNumGroups updates count and clears groups and names`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            vm.setNumGroups(3)
            assertEquals(3, vm.uiState.value.numGroups)
            assertTrue(vm.uiState.value.groups.isEmpty())
            assertTrue(vm.uiState.value.groupNames.isEmpty())
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
    fun `generate creates correct number of groups`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana", "Eve")
            vm.setNumGroups(3)
            vm.generate()
            advanceUntilIdle()
            val state = vm.uiState.value
            assertEquals(3, state.groups.size)
            assertNull(state.error)
        }

    @Test
    fun `generate distributes all participants across groups`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            val names = listOf("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank")
            addParticipants(vm, *names.toTypedArray())
            vm.setNumGroups(2)
            vm.generate()
            advanceUntilIdle()
            val allInGroups = vm.uiState.value.groups.flatten().sorted()
            assertEquals(names.sorted(), allInGroups)
        }

    @Test
    fun `generate shows error when participants not enough for groups`() {
        val vm = GroupGeneratorViewModel()
        addParticipants(vm, "Alice", "Bob")
        vm.setNumGroups(2)
        vm.generate()
        assertEquals(GroupGeneratorError.NeedMoreForGroups, vm.uiState.value.error)
        assertTrue(vm.uiState.value.groups.isEmpty())
    }

    @Test
    fun `generate shows error when participants equal to group count`() {
        val vm = GroupGeneratorViewModel()
        addParticipants(vm, "Alice", "Bob", "Charlie")
        vm.setNumGroups(3)
        vm.generate()
        assertEquals(GroupGeneratorError.NeedMoreForGroups, vm.uiState.value.error)
    }

    @Test
    fun `reset clears groups and names`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            vm.reset()
            assertTrue(vm.uiState.value.groups.isEmpty())
            assertTrue(vm.uiState.value.groupNames.isEmpty())
            assertEquals(false, vm.uiState.value.isNaming)
            assertEquals(4, vm.uiState.value.participants.size)
        }

    // --- Team naming tests ---

    @Test
    fun `generateNames produces one name per group`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana", "Eve")
            vm.setNumGroups(3)
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            assertEquals(3, vm.uiState.value.groupNames.size)
        }

    @Test
    fun `generateNames produces adjective-noun format`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            vm.uiState.value.groupNames.forEach { name ->
                val parts = name.split(" ")
                assertEquals(2, parts.size)
                assertTrue("Adjective ${parts[0]} not in list", parts[0] in testAdjectives)
                assertTrue("Noun ${parts[1]} not in list", parts[1] in testNouns)
            }
        }

    @Test
    fun `generateNames sets isNaming during animation`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            assertTrue(vm.uiState.value.isNaming)
            advanceUntilIdle()
            assertEquals(false, vm.uiState.value.isNaming)
        }

    @Test
    fun `generateNames completes revealing all chars`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            val state = vm.uiState.value
            val lastGroupName = state.groupNames.last()
            assertEquals(lastGroupName.length, state.revealedChars)
        }

    @Test
    fun `generateNames does nothing when no groups exist`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            assertTrue(vm.uiState.value.groupNames.isEmpty())
            assertEquals(false, vm.uiState.value.isNaming)
        }

    @Test
    fun `clearNames resets naming state`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            vm.clearNames()
            val state = vm.uiState.value
            assertTrue(state.groupNames.isEmpty())
            assertEquals(false, state.isNaming)
            assertEquals(0, state.namingGroupIndex)
            assertEquals(0, state.revealedChars)
        }

    @Test
    fun `generate clears previous names`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            assertTrue(vm.uiState.value.groupNames.isNotEmpty())
            vm.generate()
            advanceUntilIdle()
            assertTrue(vm.uiState.value.groupNames.isEmpty())
        }

    @Test
    fun `clearNames during active animation stops naming`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            assertTrue(vm.uiState.value.isNaming)
            vm.clearNames()
            advanceUntilIdle()
            assertEquals(false, vm.uiState.value.isNaming)
            assertTrue(vm.uiState.value.groupNames.isEmpty())
        }

    @Test
    fun `generateNames cancels previous animation`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()
            vm.generateNames(testAdjectives, testNouns)
            assertTrue(vm.uiState.value.isNaming)
            val firstNames = vm.uiState.value.groupNames
            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            val finalNames = vm.uiState.value.groupNames
            assertEquals(false, vm.uiState.value.isNaming)
            assertEquals(2, finalNames.size)
        }

    @Test
    fun `setNameInput truncates at 50 characters`() {
        val vm = GroupGeneratorViewModel()
        val longInput = "A".repeat(100)
        vm.setNameInput(longInput)
        assertEquals(50, vm.uiState.value.nameInput.length)
    }

    @Test
    fun `addParticipant caps at 100 participants`() {
        val vm = GroupGeneratorViewModel()
        repeat(100) { i ->
            vm.setNameInput("Person$i")
            vm.addParticipant()
        }
        assertEquals(100, vm.uiState.value.participants.size)
        vm.setNameInput("Person100")
        vm.addParticipant()
        assertEquals(100, vm.uiState.value.participants.size)
    }

    @Test
    fun `generateNames can be called multiple times for fresh names`() =
        runTest {
            val vm = GroupGeneratorViewModel()
            addParticipants(vm, "Alice", "Bob", "Charlie", "Diana")
            vm.generate()
            advanceUntilIdle()

            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            val firstNames = vm.uiState.value.groupNames

            vm.generateNames(testAdjectives, testNouns)
            advanceUntilIdle()
            val secondNames = vm.uiState.value.groupNames

            assertEquals(firstNames.size, secondNames.size)
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
