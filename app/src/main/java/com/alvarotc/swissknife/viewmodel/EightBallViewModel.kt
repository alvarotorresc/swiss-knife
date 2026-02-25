package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val ANSWER_COUNT = 20

data class EightBallUiState(
    val question: String = "",
    val answerIndex: Int? = null,
    val isShaking: Boolean = false,
)

class EightBallViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(EightBallUiState())
    val uiState: StateFlow<EightBallUiState> = _uiState.asStateFlow()

    fun setQuestion(text: String) {
        _uiState.update { it.copy(question = text) }
    }

    fun ask() {
        if (_uiState.value.question.isBlank()) return
        if (_uiState.value.isShaking) return

        viewModelScope.launch {
            _uiState.update { it.copy(isShaking = true, answerIndex = null) }
            delay(1200L)
            val index = (0 until ANSWER_COUNT).random()
            _uiState.update { it.copy(isShaking = false, answerIndex = index) }
        }
    }

    fun reset() {
        _uiState.update { it.copy(answerIndex = null) }
    }
}
