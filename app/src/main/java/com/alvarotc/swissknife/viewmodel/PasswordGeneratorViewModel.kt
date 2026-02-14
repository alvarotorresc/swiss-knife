package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class PasswordError {
    data object NoCharacterTypeSelected : PasswordError()
}

data class PasswordGeneratorUiState(
    val length: Int = 16,
    val includeUppercase: Boolean = true,
    val includeLowercase: Boolean = true,
    val includeNumbers: Boolean = true,
    val includeSymbols: Boolean = false,
    val password: String? = null,
    val error: PasswordError? = null,
)

class PasswordGeneratorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PasswordGeneratorUiState())
    val uiState: StateFlow<PasswordGeneratorUiState> = _uiState.asStateFlow()

    private val uppercaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val lowercaseChars = "abcdefghijklmnopqrstuvwxyz"
    private val numberChars = "0123456789"
    private val symbolChars = "!@#\$%^&*()_+-=[]{}|;:,.<>?"

    fun setLength(length: Int) {
        _uiState.update { it.copy(length = length.coerceIn(8, 64), error = null) }
    }

    fun toggleUppercase() {
        _uiState.update { it.copy(includeUppercase = !it.includeUppercase, error = null) }
    }

    fun toggleLowercase() {
        _uiState.update { it.copy(includeLowercase = !it.includeLowercase, error = null) }
    }

    fun toggleNumbers() {
        _uiState.update { it.copy(includeNumbers = !it.includeNumbers, error = null) }
    }

    fun toggleSymbols() {
        _uiState.update { it.copy(includeSymbols = !it.includeSymbols, error = null) }
    }

    fun generate() {
        val state = _uiState.value
        val charset = buildString {
            if (state.includeUppercase) append(uppercaseChars)
            if (state.includeLowercase) append(lowercaseChars)
            if (state.includeNumbers) append(numberChars)
            if (state.includeSymbols) append(symbolChars)
        }

        if (charset.isEmpty()) {
            _uiState.update { it.copy(error = PasswordError.NoCharacterTypeSelected) }
            return
        }

        val password = (1..state.length)
            .map { charset.random() }
            .joinToString("")

        _uiState.update { it.copy(password = password, error = null) }
    }
}
