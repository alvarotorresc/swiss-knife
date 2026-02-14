package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiceRollUiState(
    val diceCount: Int = 1,
    val results: List<Int> = emptyList(),
    val isRolling: Boolean = false,
)

class DiceRollViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DiceRollUiState())
    val uiState: StateFlow<DiceRollUiState> = _uiState.asStateFlow()

    fun setDiceCount(count: Int) {
        _uiState.update { it.copy(diceCount = count.coerceIn(1, 4), results = emptyList()) }
    }

    fun roll() {
        if (_uiState.value.isRolling) return
        _uiState.update { it.copy(isRolling = true) }

        viewModelScope.launch {
            val count = _uiState.value.diceCount
            // Rapid random cycle animation
            repeat(8) {
                val tempResults = List(count) { (1..6).random() }
                _uiState.update { it.copy(results = tempResults) }
                delay(60L)
            }
            // Final result
            val finalResults = List(count) { (1..6).random() }
            _uiState.update { it.copy(results = finalResults, isRolling = false) }
        }
    }
}
