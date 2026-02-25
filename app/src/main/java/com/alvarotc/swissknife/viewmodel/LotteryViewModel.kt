package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class LotteryError {
    data object CountExceedsRange : LotteryError()

    data object InvalidNumbers : LotteryError()
}

data class LotteryUiState(
    val maxNumber: Int = 49,
    val maxText: String = "49",
    val count: Int = 6,
    val countText: String = "6",
    val results: List<Int> = emptyList(),
    val isDrawing: Boolean = false,
    val revealedCount: Int = 0,
    val error: LotteryError? = null,
)

class LotteryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LotteryUiState())
    val uiState: StateFlow<LotteryUiState> = _uiState.asStateFlow()

    fun setMaxNumber(text: String) {
        val parsed = text.toIntOrNull()
        _uiState.update {
            it.copy(
                maxText = text,
                maxNumber = parsed ?: it.maxNumber,
                error = null,
            )
        }
    }

    fun setCount(text: String) {
        val parsed = text.toIntOrNull()
        _uiState.update {
            it.copy(
                countText = text,
                count = parsed ?: it.count,
                error = null,
            )
        }
    }

    fun draw() {
        val maxNumber = _uiState.value.maxText.toIntOrNull()
        val count = _uiState.value.countText.toIntOrNull()

        when {
            maxNumber == null || count == null || maxNumber < 1 || count < 1 -> {
                _uiState.update { it.copy(error = LotteryError.InvalidNumbers) }
            }
            count > maxNumber -> {
                _uiState.update { it.copy(error = LotteryError.CountExceedsRange) }
            }
            else -> {
                val numbers = (1..maxNumber).toMutableList()
                numbers.shuffle()
                val drawn = numbers.take(count).sorted()

                _uiState.update {
                    it.copy(
                        results = drawn,
                        revealedCount = 0,
                        isDrawing = true,
                        error = null,
                    )
                }

                viewModelScope.launch {
                    for (i in 1..drawn.size) {
                        delay(400L)
                        _uiState.update { it.copy(revealedCount = i) }
                    }
                    _uiState.update { it.copy(isDrawing = false) }
                }
            }
        }
    }

    fun reset() {
        _uiState.update {
            it.copy(
                results = emptyList(),
                revealedCount = 0,
                isDrawing = false,
                error = null,
            )
        }
    }
}
