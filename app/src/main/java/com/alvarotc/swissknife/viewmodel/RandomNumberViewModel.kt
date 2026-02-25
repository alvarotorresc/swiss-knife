package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class RandomNumberError {
    data object InvalidNumbers : RandomNumberError()

    data object MinNotLessThanMax : RandomNumberError()
}

data class RandomNumberUiState(
    val minText: String = "1",
    val maxText: String = "100",
    val result: Int? = null,
    val error: RandomNumberError? = null,
    val isGenerating: Boolean = false,
    val displayText: String? = null,
    val lockedDigits: Int = 0,
)

class RandomNumberViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RandomNumberUiState())
    val uiState: StateFlow<RandomNumberUiState> = _uiState.asStateFlow()

    fun setMin(value: String) {
        _uiState.update { it.copy(minText = value, error = null) }
    }

    fun setMax(value: String) {
        _uiState.update { it.copy(maxText = value, error = null) }
    }

    fun generate() {
        val min = _uiState.value.minText.toIntOrNull()
        val max = _uiState.value.maxText.toIntOrNull()

        when {
            min == null || max == null -> {
                _uiState.update { it.copy(error = RandomNumberError.InvalidNumbers) }
            }
            min >= max -> {
                _uiState.update { it.copy(error = RandomNumberError.MinNotLessThanMax) }
            }
            else -> {
                val result = (min..max).random()
                val resultStr = result.toString()
                val totalDigits = resultStr.length

                _uiState.update {
                    it.copy(
                        result = result,
                        error = null,
                        isGenerating = true,
                        displayText = resultStr,
                        lockedDigits = 0,
                    )
                }

                viewModelScope.launch {
                    // Spinning phase — all digits cycle
                    repeat(15) {
                        val spinning =
                            buildString {
                                repeat(totalDigits) { append((0..9).random()) }
                            }
                        _uiState.update { it.copy(displayText = spinning, lockedDigits = 0) }
                        delay(50L)
                    }

                    // Lock digits one by one from left to right
                    for (digitIndex in 0 until totalDigits) {
                        val cycleCount = 6 + digitIndex * 3
                        repeat(cycleCount) { i ->
                            val locked = resultStr.substring(0, digitIndex)
                            val spinning =
                                buildString {
                                    repeat(totalDigits - digitIndex) { append((0..9).random()) }
                                }
                            val delayMs = 50L + (i.toLong() * 15L / cycleCount)
                            _uiState.update {
                                it.copy(
                                    displayText = locked + spinning,
                                    lockedDigits = digitIndex,
                                )
                            }
                            delay(delayMs)
                        }
                        _uiState.update { it.copy(lockedDigits = digitIndex + 1) }
                    }

                    // Final reveal
                    _uiState.update {
                        it.copy(
                            displayText = resultStr,
                            lockedDigits = totalDigits,
                            isGenerating = false,
                        )
                    }
                }
            }
        }
    }
}
