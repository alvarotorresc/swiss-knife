package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class RPSChoice(val emoji: String) {
    ROCK("🪨"),
    PAPER("📄"),
    SCISSORS("✂️"),
}

enum class RPSResult { WIN, LOSE, DRAW }

data class RPSScore(
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
)

data class RPSUiState(
    val playerChoice: RPSChoice? = null,
    val cpuChoice: RPSChoice? = null,
    val result: RPSResult? = null,
    val isRevealing: Boolean = false,
    val score: RPSScore = RPSScore(),
)

class RockPaperScissorsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RPSUiState())
    val uiState: StateFlow<RPSUiState> = _uiState.asStateFlow()

    fun play(choice: RPSChoice) {
        if (_uiState.value.isRevealing) return

        _uiState.update { it.copy(playerChoice = choice, cpuChoice = null, result = null, isRevealing = true) }

        viewModelScope.launch {
            delay(800L)

            val cpuChoice = RPSChoice.entries.random()
            val result = determineResult(choice, cpuChoice)

            _uiState.update { state ->
                state.copy(
                    cpuChoice = cpuChoice,
                    result = result,
                    isRevealing = false,
                    score =
                        when (result) {
                            RPSResult.WIN -> state.score.copy(wins = state.score.wins + 1)
                            RPSResult.LOSE -> state.score.copy(losses = state.score.losses + 1)
                            RPSResult.DRAW -> state.score.copy(draws = state.score.draws + 1)
                        },
                )
            }
        }
    }

    fun reset() {
        _uiState.update { it.copy(playerChoice = null, cpuChoice = null, result = null, isRevealing = false) }
    }

    private fun determineResult(
        player: RPSChoice,
        cpu: RPSChoice,
    ): RPSResult =
        when {
            player == cpu -> RPSResult.DRAW
            player == RPSChoice.ROCK && cpu == RPSChoice.SCISSORS -> RPSResult.WIN
            player == RPSChoice.PAPER && cpu == RPSChoice.ROCK -> RPSResult.WIN
            player == RPSChoice.SCISSORS && cpu == RPSChoice.PAPER -> RPSResult.WIN
            else -> RPSResult.LOSE
        }
}
