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
    ROCK("✊"),
    PAPER("✋"),
    SCISSORS("✌️"),
}

enum class RPSResult { WIN, LOSE, DRAW }

enum class RPSMode { CPU, LOCAL }

data class RPSScore(
    val wins: Int = 0,
    val losses: Int = 0,
    val draws: Int = 0,
)

data class RPSUiState(
    val mode: RPSMode = RPSMode.CPU,
    val playerChoice: RPSChoice? = null,
    val opponentChoice: RPSChoice? = null,
    val result: RPSResult? = null,
    val isRevealing: Boolean = false,
    val isWaitingForP2: Boolean = false,
    val score: RPSScore = RPSScore(),
)

class RockPaperScissorsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RPSUiState())
    val uiState: StateFlow<RPSUiState> = _uiState.asStateFlow()

    fun setMode(mode: RPSMode) {
        _uiState.update {
            RPSUiState(mode = mode)
        }
    }

    fun play(choice: RPSChoice) {
        val state = _uiState.value
        if (state.isRevealing || state.isWaitingForP2) return

        when (state.mode) {
            RPSMode.CPU -> playCpu(choice)
            RPSMode.LOCAL -> playLocal(choice)
        }
    }

    fun confirmHandoff() {
        _uiState.update { it.copy(isWaitingForP2 = false) }
    }

    fun reset() {
        _uiState.update {
            it.copy(
                playerChoice = null,
                opponentChoice = null,
                result = null,
                isRevealing = false,
                isWaitingForP2 = false,
            )
        }
    }

    private fun playCpu(choice: RPSChoice) {
        _uiState.update {
            it.copy(playerChoice = choice, opponentChoice = null, result = null, isRevealing = true)
        }

        viewModelScope.launch {
            delay(800L)

            val cpuChoice = RPSChoice.entries.random()
            val result = determineResult(choice, cpuChoice)

            _uiState.update { state ->
                state.copy(
                    opponentChoice = cpuChoice,
                    result = result,
                    isRevealing = false,
                    score = updateScore(state.score, result),
                )
            }
        }
    }

    private fun playLocal(choice: RPSChoice) {
        val state = _uiState.value

        if (state.playerChoice == null) {
            _uiState.update {
                it.copy(playerChoice = choice, isWaitingForP2 = true)
            }
        } else {
            val p1Choice = state.playerChoice
            _uiState.update {
                it.copy(opponentChoice = null, isRevealing = true, isWaitingForP2 = false)
            }

            viewModelScope.launch {
                delay(800L)

                val result = determineResult(p1Choice, choice)

                _uiState.update { s ->
                    s.copy(
                        opponentChoice = choice,
                        result = result,
                        isRevealing = false,
                        score = updateScore(s.score, result),
                    )
                }
            }
        }
    }

    private fun updateScore(
        score: RPSScore,
        result: RPSResult,
    ): RPSScore =
        when (result) {
            RPSResult.WIN -> score.copy(wins = score.wins + 1)
            RPSResult.LOSE -> score.copy(losses = score.losses + 1)
            RPSResult.DRAW -> score.copy(draws = score.draws + 1)
        }

    internal fun determineResult(
        player: RPSChoice,
        opponent: RPSChoice,
    ): RPSResult =
        when {
            player == opponent -> RPSResult.DRAW
            player == RPSChoice.ROCK && opponent == RPSChoice.SCISSORS -> RPSResult.WIN
            player == RPSChoice.PAPER && opponent == RPSChoice.ROCK -> RPSResult.WIN
            player == RPSChoice.SCISSORS && opponent == RPSChoice.PAPER -> RPSResult.WIN
            else -> RPSResult.LOSE
        }
}
