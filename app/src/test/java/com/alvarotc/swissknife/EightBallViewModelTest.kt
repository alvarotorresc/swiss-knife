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

    private val knownAnswers =
        listOf(
            "It is certain",
            "Without a doubt",
            "Yes definitely",
            "You may rely on it",
            "As I see it yes",
            "Most likely",
            "Outlook good",
            "Yes",
            "Signs point to yes",
            "Reply hazy try again",
            "Ask again later",
            "Better not tell you now",
            "Cannot predict now",
            "Concentrate and ask again",
            "Don't count on it",
            "My reply is no",
            "My sources say no",
            "Outlook not so good",
            "Very doubtful",
            "No way",
        )

    @Test
    fun `initial state has empty question and no answer`() {
        val vm = EightBallViewModel()
        val state = vm.uiState.value
        assertEquals("", state.question)
        assertNull(state.answer)
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
            assertNotNull(state.answer)
            assertEquals(false, state.isShaking)
        }

    @Test
    fun `ask answer is from known answers list`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("Will I pass?")
            vm.ask()
            advanceUntilIdle()
            assertTrue(
                "Answer should be from known list",
                vm.uiState.value.answer in knownAnswers,
            )
        }

    @Test
    fun `ask does nothing when question is blank`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("   ")
            vm.ask()
            advanceUntilIdle()
            assertNull(vm.uiState.value.answer)
        }

    @Test
    fun `ask does nothing when question is empty`() =
        runTest {
            val vm = EightBallViewModel()
            vm.ask()
            advanceUntilIdle()
            assertNull(vm.uiState.value.answer)
        }

    @Test
    fun `ask is ignored while shaking`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("First question?")
            vm.ask()
            // While shaking, try to ask again
            assertTrue(vm.uiState.value.isShaking)
            vm.setQuestion("Second question?")
            vm.ask() // should be ignored
            advanceUntilIdle()
            // Only the first ask should have completed
            assertEquals("Second question?", vm.uiState.value.question)
            assertNotNull(vm.uiState.value.answer)
        }

    @Test
    fun `ask sets isShaking to true during animation`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("Am I shaking?")
            vm.ask()
            assertTrue(vm.uiState.value.isShaking)
            assertNull(vm.uiState.value.answer) // answer cleared during shake
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
            assertNull(vm.uiState.value.answer)
            assertEquals("Test question?", vm.uiState.value.question)
        }

    @Test
    fun `multiple asks produce answers`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("First?")
            vm.ask()
            advanceUntilIdle()
            val first = vm.uiState.value.answer
            assertNotNull(first)

            vm.ask()
            advanceUntilIdle()
            val second = vm.uiState.value.answer
            assertNotNull(second)
        }

    @Test
    fun `ask produces varied answers over many calls`() =
        runTest {
            val vm = EightBallViewModel()
            vm.setQuestion("Varied?")
            val answers = mutableSetOf<String>()
            repeat(50) {
                vm.ask()
                advanceUntilIdle()
                vm.uiState.value.answer?.let { answers.add(it) }
            }
            assertTrue("Expected varied answers, got ${answers.size}", answers.size > 1)
        }
}
