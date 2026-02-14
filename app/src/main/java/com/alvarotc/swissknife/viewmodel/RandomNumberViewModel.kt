package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class RandomNumberError {
    data object InvalidNumbers : RandomNumberError()
    data object MinNotLessThanMax : RandomNumberError()
}

data class RandomNumberUiState(
    val minText: String = "1",
    val maxText: String = "100",
    val result: Int? = null,
    val error: RandomNumberError? = null,
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
                _uiState.update { it.copy(result = result, error = null) }
            }
        }
    }
}
