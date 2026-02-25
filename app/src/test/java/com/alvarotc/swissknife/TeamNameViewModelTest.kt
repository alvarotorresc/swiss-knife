package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.TeamNameViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TeamNameViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val testAdjectives = listOf("Thunder", "Shadow", "Iron", "Phantom", "Blazing")
    private val testNouns = listOf("Wolves", "Dragons", "Hawks", "Panthers", "Vipers")

    @Test
    fun `initial state has empty name and no history`() {
        val vm = TeamNameViewModel()
        val state = vm.uiState.value
        assertEquals("", state.adjective)
        assertEquals("", state.noun)
        assertEquals("", state.fullName)
        assertEquals(false, state.isGenerating)
        assertEquals(0, state.revealedChars)
        assertTrue(state.history.isEmpty())
    }

    @Test
    fun `generate produces a team name with adjective and noun`() =
        runTest {
            val vm = TeamNameViewModel()
            vm.generate(testAdjectives, testNouns)
            advanceUntilIdle()
            val state = vm.uiState.value
            assertTrue(state.adjective.isNotEmpty())
            assertTrue(state.noun.isNotEmpty())
            assertEquals("${state.adjective} ${state.noun}", state.fullName)
        }

    @Test
    fun `generate completes revealing animation`() =
        runTest {
            val vm = TeamNameViewModel()
            vm.generate(testAdjectives, testNouns)
            advanceUntilIdle()
            val state = vm.uiState.value
            assertEquals(state.fullName.length, state.revealedChars)
            assertEquals(false, state.isGenerating)
        }

    @Test
    fun `generate adds name to history`() =
        runTest {
            val vm = TeamNameViewModel()
            vm.generate(testAdjectives, testNouns)
            advanceUntilIdle()
            val state = vm.uiState.value
            assertEquals(1, state.history.size)
            assertEquals(state.fullName, state.history[0])
        }

    @Test
    fun `history is capped at 8 entries`() =
        runTest {
            val vm = TeamNameViewModel()
            repeat(12) {
                vm.generate(testAdjectives, testNouns)
                advanceUntilIdle()
            }
            assertEquals(8, vm.uiState.value.history.size)
        }

    @Test
    fun `history has most recent name first`() =
        runTest {
            val vm = TeamNameViewModel()
            vm.generate(testAdjectives, testNouns)
            advanceUntilIdle()

            vm.generate(testAdjectives, testNouns)
            advanceUntilIdle()
            val secondName = vm.uiState.value.fullName

            assertEquals(secondName, vm.uiState.value.history[0])
        }

    @Test
    fun `generate produces varied names over many calls`() =
        runTest {
            val vm = TeamNameViewModel()
            val names = mutableSetOf<String>()
            repeat(20) {
                vm.generate(testAdjectives, testNouns)
                advanceUntilIdle()
                names.add(vm.uiState.value.fullName)
            }
            assertTrue("Expected varied names, got ${names.size}", names.size > 1)
        }

    @Test
    fun `reset clears name but not history`() =
        runTest {
            val vm = TeamNameViewModel()
            vm.generate(testAdjectives, testNouns)
            advanceUntilIdle()
            vm.reset()
            val state = vm.uiState.value
            assertEquals("", state.adjective)
            assertEquals("", state.noun)
            assertEquals("", state.fullName)
            assertEquals(false, state.isGenerating)
            assertEquals(0, state.revealedChars)
        }

    @Test
    fun `generate sets isGenerating during animation`() =
        runTest {
            val vm = TeamNameViewModel()
            vm.generate(testAdjectives, testNouns)
            assertTrue(vm.uiState.value.isGenerating)
            advanceUntilIdle()
            assertEquals(false, vm.uiState.value.isGenerating)
        }

    @Test
    fun `fullName matches adjective and noun`() =
        runTest {
            val vm = TeamNameViewModel()
            repeat(10) {
                vm.generate(testAdjectives, testNouns)
                advanceUntilIdle()
                val state = vm.uiState.value
                assertEquals("${state.adjective} ${state.noun}", state.fullName)
            }
        }
}
