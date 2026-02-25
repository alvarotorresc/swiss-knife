package com.alvarotc.swissknife

import com.alvarotc.swissknife.viewmodel.RPSChoice
import com.alvarotc.swissknife.viewmodel.RPSMode
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

    // --- CPU mode tests (existing behavior) ---

    @Test
    fun `initial state has no choices and zero score`() {
        val vm = RockPaperScissorsViewModel()
        val state = vm.uiState.value
        assertNull(state.playerChoice)
        assertNull(state.opponentChoice)
        assertNull(state.result)
        assertEquals(false, state.isRevealing)
        assertEquals(false, state.isWaitingForP2)
        assertEquals(RPSMode.CPU, state.mode)
        assertEquals(RPSScore(), state.score)
    }

    @Test
    fun `play sets player choice and reveals after delay in CPU mode`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.play(RPSChoice.ROCK)
            advanceUntilIdle()
            val state = vm.uiState.value
            assertEquals(RPSChoice.ROCK, state.playerChoice)
            assertNotNull(state.opponentChoice)
            assertNotNull(state.result)
            assertEquals(false, state.isRevealing)
        }

    @Test
    fun `play updates score on win in CPU mode`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
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
    fun `play is ignored while revealing in CPU mode`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.play(RPSChoice.ROCK)
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
            assertNull(state.opponentChoice)
            assertNull(state.result)
            assertEquals(false, state.isRevealing)
            assertEquals(false, state.isWaitingForP2)
            assertEquals(scoreBefore, state.score)
        }

    @Test
    fun `opponent choice is always a valid RPSChoice in CPU mode`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            repeat(20) {
                vm.play(RPSChoice.ROCK)
                advanceUntilIdle()
                assertTrue(vm.uiState.value.opponentChoice in RPSChoice.entries)
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
                when (state.opponentChoice) {
                    RPSChoice.ROCK -> assertEquals(RPSResult.DRAW, state.result)
                    RPSChoice.SCISSORS -> assertEquals(RPSResult.WIN, state.result)
                    RPSChoice.PAPER -> assertEquals(RPSResult.LOSE, state.result)
                    null -> throw AssertionError("opponentChoice should not be null after play")
                }
            }
        }

    // --- Mode switching tests ---

    @Test
    fun `setMode changes mode and resets state`() {
        val vm = RockPaperScissorsViewModel()
        vm.setMode(RPSMode.LOCAL)
        val state = vm.uiState.value
        assertEquals(RPSMode.LOCAL, state.mode)
        assertNull(state.playerChoice)
        assertNull(state.opponentChoice)
        assertNull(state.result)
        assertEquals(RPSScore(), state.score)
    }

    @Test
    fun `setMode resets score when switching modes`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.play(RPSChoice.ROCK)
            advanceUntilIdle()
            assertTrue(vm.uiState.value.score.wins + vm.uiState.value.score.losses + vm.uiState.value.score.draws > 0)
            vm.setMode(RPSMode.LOCAL)
            assertEquals(RPSScore(), vm.uiState.value.score)
        }

    // --- LOCAL mode tests ---

    @Test
    fun `local mode - P1 choice sets playerChoice and triggers waiting`() {
        val vm = RockPaperScissorsViewModel()
        vm.setMode(RPSMode.LOCAL)
        vm.play(RPSChoice.ROCK)
        val state = vm.uiState.value
        assertEquals(RPSChoice.ROCK, state.playerChoice)
        assertTrue(state.isWaitingForP2)
        assertNull(state.opponentChoice)
        assertNull(state.result)
    }

    @Test
    fun `local mode - play is ignored while waiting for P2`() {
        val vm = RockPaperScissorsViewModel()
        vm.setMode(RPSMode.LOCAL)
        vm.play(RPSChoice.ROCK)
        assertTrue(vm.uiState.value.isWaitingForP2)
        vm.play(RPSChoice.PAPER) // should be ignored
        assertEquals(RPSChoice.ROCK, vm.uiState.value.playerChoice)
        assertTrue(vm.uiState.value.isWaitingForP2)
    }

    @Test
    fun `local mode - confirmHandoff clears waiting state`() {
        val vm = RockPaperScissorsViewModel()
        vm.setMode(RPSMode.LOCAL)
        vm.play(RPSChoice.ROCK)
        assertTrue(vm.uiState.value.isWaitingForP2)
        vm.confirmHandoff()
        assertEquals(false, vm.uiState.value.isWaitingForP2)
        assertEquals(RPSChoice.ROCK, vm.uiState.value.playerChoice)
    }

    @Test
    fun `local mode - P2 choice triggers reveal and shows result`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.setMode(RPSMode.LOCAL)
            vm.play(RPSChoice.ROCK) // P1
            vm.confirmHandoff()
            vm.play(RPSChoice.SCISSORS) // P2
            advanceUntilIdle()
            val state = vm.uiState.value
            assertEquals(RPSChoice.ROCK, state.playerChoice)
            assertEquals(RPSChoice.SCISSORS, state.opponentChoice)
            assertEquals(RPSResult.WIN, state.result)
            assertEquals(false, state.isRevealing)
        }

    @Test
    fun `local mode - P1 wins tracked correctly`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.setMode(RPSMode.LOCAL)
            vm.play(RPSChoice.ROCK) // P1
            vm.confirmHandoff()
            vm.play(RPSChoice.SCISSORS) // P2
            advanceUntilIdle()
            assertEquals(1, vm.uiState.value.score.wins)
            assertEquals(0, vm.uiState.value.score.losses)
        }

    @Test
    fun `local mode - P2 wins tracked as loss`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.setMode(RPSMode.LOCAL)
            vm.play(RPSChoice.ROCK) // P1
            vm.confirmHandoff()
            vm.play(RPSChoice.PAPER) // P2
            advanceUntilIdle()
            assertEquals(0, vm.uiState.value.score.wins)
            assertEquals(1, vm.uiState.value.score.losses)
        }

    @Test
    fun `local mode - draw tracked correctly`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.setMode(RPSMode.LOCAL)
            vm.play(RPSChoice.ROCK) // P1
            vm.confirmHandoff()
            vm.play(RPSChoice.ROCK) // P2
            advanceUntilIdle()
            assertEquals(1, vm.uiState.value.score.draws)
        }

    @Test
    fun `local mode - reset clears for next round but preserves score`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.setMode(RPSMode.LOCAL)
            vm.play(RPSChoice.ROCK) // P1
            vm.confirmHandoff()
            vm.play(RPSChoice.SCISSORS) // P2
            advanceUntilIdle()
            val scoreBefore = vm.uiState.value.score
            vm.reset()
            val state = vm.uiState.value
            assertNull(state.playerChoice)
            assertNull(state.opponentChoice)
            assertNull(state.result)
            assertEquals(false, state.isWaitingForP2)
            assertEquals(scoreBefore, state.score)
        }

    @Test
    fun `local mode - multiple rounds accumulate score`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.setMode(RPSMode.LOCAL)

            // Round 1: P1 wins
            vm.play(RPSChoice.ROCK)
            vm.confirmHandoff()
            vm.play(RPSChoice.SCISSORS)
            advanceUntilIdle()
            vm.reset()

            // Round 2: P2 wins
            vm.play(RPSChoice.ROCK)
            vm.confirmHandoff()
            vm.play(RPSChoice.PAPER)
            advanceUntilIdle()
            vm.reset()

            // Round 3: Draw
            vm.play(RPSChoice.PAPER)
            vm.confirmHandoff()
            vm.play(RPSChoice.PAPER)
            advanceUntilIdle()

            val score = vm.uiState.value.score
            assertEquals(1, score.wins)
            assertEquals(1, score.losses)
            assertEquals(1, score.draws)
        }

    @Test
    fun `local mode - P2 reveal triggers isRevealing`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.setMode(RPSMode.LOCAL)
            vm.play(RPSChoice.ROCK) // P1
            vm.confirmHandoff()
            vm.play(RPSChoice.PAPER) // P2
            assertTrue(vm.uiState.value.isRevealing)
            advanceUntilIdle()
            assertEquals(false, vm.uiState.value.isRevealing)
        }

    @Test
    fun `local mode - play ignored during P2 reveal`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.setMode(RPSMode.LOCAL)
            vm.play(RPSChoice.ROCK) // P1
            vm.confirmHandoff()
            vm.play(RPSChoice.PAPER) // P2
            assertTrue(vm.uiState.value.isRevealing)
            vm.play(RPSChoice.SCISSORS) // should be ignored
            assertEquals(RPSChoice.ROCK, vm.uiState.value.playerChoice)
            advanceUntilIdle()
        }

    @Test
    fun `confirmHandoff when not waiting is a no-op`() {
        val vm = RockPaperScissorsViewModel()
        vm.confirmHandoff()
        assertEquals(false, vm.uiState.value.isWaitingForP2)
        assertNull(vm.uiState.value.playerChoice)
    }

    @Test
    fun `setMode mid-round in local mode resets everything`() {
        val vm = RockPaperScissorsViewModel()
        vm.setMode(RPSMode.LOCAL)
        vm.play(RPSChoice.ROCK) // P1 plays
        assertTrue(vm.uiState.value.isWaitingForP2)
        vm.setMode(RPSMode.CPU)
        assertEquals(RPSMode.CPU, vm.uiState.value.mode)
        assertNull(vm.uiState.value.playerChoice)
        assertEquals(false, vm.uiState.value.isWaitingForP2)
    }

    @Test
    fun `reset preserves mode`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.setMode(RPSMode.LOCAL)
            vm.play(RPSChoice.ROCK) // P1
            vm.confirmHandoff()
            vm.play(RPSChoice.SCISSORS) // P2
            advanceUntilIdle()
            vm.reset()
            assertEquals(RPSMode.LOCAL, vm.uiState.value.mode)
        }

    @Test
    fun `local mode - reset during reveal does not crash`() =
        runTest {
            val vm = RockPaperScissorsViewModel()
            vm.setMode(RPSMode.LOCAL)
            vm.play(RPSChoice.ROCK) // P1
            vm.confirmHandoff()
            vm.play(RPSChoice.PAPER) // P2 — triggers 800ms reveal
            assertTrue(vm.uiState.value.isRevealing)
            vm.reset() // reset during reveal
            advanceUntilIdle()
            // Should not crash — P1 choice was captured before delay
        }

    // --- determineResult logic tests ---

    @Test
    fun `determineResult - all win combinations`() {
        val vm = RockPaperScissorsViewModel()
        assertEquals(RPSResult.WIN, vm.determineResult(RPSChoice.ROCK, RPSChoice.SCISSORS))
        assertEquals(RPSResult.WIN, vm.determineResult(RPSChoice.PAPER, RPSChoice.ROCK))
        assertEquals(RPSResult.WIN, vm.determineResult(RPSChoice.SCISSORS, RPSChoice.PAPER))
    }

    @Test
    fun `determineResult - all lose combinations`() {
        val vm = RockPaperScissorsViewModel()
        assertEquals(RPSResult.LOSE, vm.determineResult(RPSChoice.ROCK, RPSChoice.PAPER))
        assertEquals(RPSResult.LOSE, vm.determineResult(RPSChoice.PAPER, RPSChoice.SCISSORS))
        assertEquals(RPSResult.LOSE, vm.determineResult(RPSChoice.SCISSORS, RPSChoice.ROCK))
    }

    @Test
    fun `determineResult - all draw combinations`() {
        val vm = RockPaperScissorsViewModel()
        RPSChoice.entries.forEach { choice ->
            assertEquals(RPSResult.DRAW, vm.determineResult(choice, choice))
        }
    }
}
