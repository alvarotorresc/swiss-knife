package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TeamNameUiState(
    val adjective: String = "",
    val noun: String = "",
    val fullName: String = "",
    val isGenerating: Boolean = false,
    val revealedChars: Int = 0,
    val history: List<String> = emptyList(),
)

class TeamNameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TeamNameUiState())
    val uiState: StateFlow<TeamNameUiState> = _uiState.asStateFlow()

    fun generate(
        adjectives: List<String>,
        nouns: List<String>,
    ) {
        val adjective = adjectives.random()
        val noun = nouns.random()
        val fullName = "$adjective $noun"

        _uiState.update {
            it.copy(
                adjective = adjective,
                noun = noun,
                fullName = fullName,
                isGenerating = true,
                revealedChars = 0,
            )
        }

        viewModelScope.launch {
            for (i in 1..fullName.length) {
                _uiState.update { it.copy(revealedChars = i) }
                delay(50L)
            }

            val previousHistory = _uiState.value.history
            val updatedHistory = (listOf(fullName) + previousHistory).take(8)

            _uiState.update {
                it.copy(
                    isGenerating = false,
                    revealedChars = fullName.length,
                    history = updatedHistory,
                )
            }
        }
    }

    fun reset() {
        _uiState.update {
            it.copy(
                adjective = "",
                noun = "",
                fullName = "",
                isGenerating = false,
                revealedChars = 0,
            )
        }
    }
}
