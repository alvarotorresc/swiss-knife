package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.EightBallViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EightBallViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state has empty question and no answer`() {
        val vm = EightBallViewModel()
        val state = vm.uiState.value
        assertEquals("", state.question)
        assertNull(state.answerIndex)
        assertEquals(false, state.isShaking)
    }

    @Test
    fun `setQuestion updates question text`() {
        val vm = EightBallViewModel()
        vm.setQuestion("Will it rain?")
        assertEquals("Will it rain?", vm.uiState.value.question)
    }

    @Test
    fun `ask produces an answer after delay`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("Is this a test?")
            vm.ask()
            advanceUntilIdle()
            val state = vm.uiState.value
            assertNotNull(state.answerIndex)
            assertEquals(false, state.isShaking)
        }

    @Test
    fun `ask answer index is within valid range`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("Will I pass?")
            vm.ask()
            advanceUntilIdle()
            val index = vm.uiState.value.answerIndex
            assertNotNull(index)
            assertTrue("Index should be 0-19, got $index", index!! in 0..19)
        }

    @Test
    fun `ask does nothing when question is blank`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("   ")
            vm.ask()
            advanceUntilIdle()
            assertNull(vm.uiState.value.answerIndex)
        }

    @Test
    fun `ask does nothing when question is empty`() =
        runTest {
            val vm = EightBallViewModel()
            vm.ask()
            advanceUntilIdle()
            assertNull(vm.uiState.value.answerIndex)
        }

    @Test
    fun `ask is ignored while shaking`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("First question?")
            vm.ask()
            assertTrue(vm.uiState.value.isShaking)
            vm.setQuestion("Second question?")
            vm.ask()
            advanceUntilIdle()
            assertEquals("Second question?", vm.uiState.value.question)
            assertNotNull(vm.uiState.value.answerIndex)
        }

    @Test
    fun `ask sets isShaking to true during animation`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("Am I shaking?")
            vm.ask()
            assertTrue(vm.uiState.value.isShaking)
            assertNull(vm.uiState.value.answerIndex)
            advanceUntilIdle()
        }

    @Test
    fun `reset clears answer but preserves question`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("Test question?")
            vm.ask()
            advanceUntilIdle()
            vm.reset()
            assertNull(vm.uiState.value.answerIndex)
            assertEquals("Test question?", vm.uiState.value.question)
        }

    @Test
    fun `multiple asks produce answers`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("First?")
            vm.ask()
            advanceUntilIdle()
            assertNotNull(vm.uiState.value.answerIndex)

            vm.ask()
            advanceUntilIdle()
            assertNotNull(vm.uiState.value.answerIndex)
        }

    @Test
    fun `ask produces varied answers over many calls`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("Varied?")
            val indices = mutableSetOf<Int>()
            repeat(50) {
                vm.ask()
                advanceUntilIdle()
                vm.uiState.value.answerIndex?.let { indices.add(it) }
            }
            assertTrue("Expected varied answers, got ${indices.size}", indices.size > 1)
        }
}
