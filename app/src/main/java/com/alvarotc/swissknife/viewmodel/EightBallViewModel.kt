package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EightBallUiState(
    val question: String = "",
    val answer: String? = null,
    val isShaking: Boolean = false,
)

class EightBallViewModel : ViewModel() {
    private val answers =
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

    private val _uiState = MutableStateFlow(EightBallUiState())
    val uiState: StateFlow<EightBallUiState> = _uiState.asStateFlow()

    fun setQuestion(text: String) {
        _uiState.update { it.copy(question = text) }
    }

    fun ask() {
        if (_uiState.value.question.isBlank()) return
        if (_uiState.value.isShaking) return

        viewModelScope.launch {
            _uiState.update { it.copy(isShaking = true, answer = null) }
            delay(1200L)
            val answer = answers.random()
            _uiState.update { it.copy(isShaking = false, answer = answer) }
        }
    }

    fun reset() {
        _uiState.update { it.copy(answer = null) }
    }
}
