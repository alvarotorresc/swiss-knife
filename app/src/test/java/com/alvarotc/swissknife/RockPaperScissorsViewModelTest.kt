package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.RPSChoice
import com.alvarotc.swissknife.viewmodel.RPSResult
import com.alvarotc.swissknife.viewmodel.RPSScore
import com.alvarotc.swissknife.viewmodel.RockPaperScissorsViewModel
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
class RockPaperScissorsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial state has no choices and zero score`() {
        val vm = RockPaperScissorsViewModel()
        val state = vm.uiState.value
        assertNull(state.playerChoice)
        assertNull(state.cpuChoice)
        assertNull(state.result)
        assertEquals(false, state.isRevealing)
        assertEquals(RPSScore(), state.score)
    }

    @Test
    fun `play sets player choice and reveals after delay`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.play(RPSChoice.ROCK)
            advanceUntilIdle()
            val state = vm.uiState.value
            assertEquals(RPSChoice.ROCK, state.playerChoice)
            assertNotNull(state.cpuChoice)
            assertNotNull(state.result)
            assertEquals(false, state.isRevealing)
        }

    @Test
    fun `play updates score on win`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            // Play many times - statistically we should get at least one of each result
            repeat(50) {
                vm.play(RPSChoice.ROCK)
                advanceUntilIdle()
            }
            val score = vm.uiState.value.score
            assertEquals(50, score.wins + score.losses + score.draws)
        }

    @Test
    fun `play increments total games correctly`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            repeat(5) {
                vm.play(RPSChoice.SCISSORS)
                advanceUntilIdle()
            }
            val score = vm.uiState.value.score
            assertEquals(5, score.wins + score.losses + score.draws)
        }

    @Test
    fun `play produces all three result types over many games`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            repeat(100) {
                vm.play(RPSChoice.entries.random())
                advanceUntilIdle()
            }
            val score = vm.uiState.value.score
            assertTrue("Expected some wins", score.wins > 0)
            assertTrue("Expected some losses", score.losses > 0)
            assertTrue("Expected some draws", score.draws > 0)
        }

    @Test
    fun `play is ignored while revealing`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.play(RPSChoice.ROCK)
            // isRevealing should be true before advancing time
            assertTrue(vm.uiState.value.isRevealing)
            vm.play(RPSChoice.PAPER) // should be ignored
            assertEquals(RPSChoice.ROCK, vm.uiState.value.playerChoice)
            advanceUntilIdle()
        }

    @Test
    fun `reset clears choices and result but preserves score`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.play(RPSChoice.PAPER)
            advanceUntilIdle()
            val scoreBefore = vm.uiState.value.score
            vm.reset()
            val state = vm.uiState.value
            assertNull(state.playerChoice)
            assertNull(state.cpuChoice)
            assertNull(state.result)
            assertEquals(false, state.isRevealing)
            assertEquals(scoreBefore, state.score)
        }

    @Test
    fun `cpu choice is always a valid RPSChoice`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            repeat(20) {
                vm.play(RPSChoice.ROCK)
                advanceUntilIdle()
                assertTrue(vm.uiState.value.cpuChoice in RPSChoice.entries)
            }
        }

    @Test
    fun `result is consistent with choices`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            repeat(50) {
                vm.play(RPSChoice.ROCK)
                advanceUntilIdle()
                val state = vm.uiState.value
                when (state.cpuChoice) {
                    RPSChoice.ROCK -> assertEquals(RPSResult.DRAW, state.result)
                    RPSChoice.SCISSORS -> assertEquals(RPSResult.WIN, state.result)
                    RPSChoice.PAPER -> assertEquals(RPSResult.LOSE, state.result)
                    null -> throw AssertionError("cpuChoice should not be null after play")
                }
            }
        }
}
