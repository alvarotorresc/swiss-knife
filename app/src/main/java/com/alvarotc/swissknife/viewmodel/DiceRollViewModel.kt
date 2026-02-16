package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class DiceType(val sides: Int, val label: String) {
    D4(4, "D4"),
    D6(6, "D6"),
    D8(8, "D8"),
    D10(10, "D10"),
    D12(12, "D12"),
    D20(20, "D20"),
}

data class DiceRollUiState(
    val diceType: DiceType = DiceType.D6,
    val diceCount: Int = 1,
    val results: List<Int> = emptyList(),
    val isRolling: Boolean = false,
)

class DiceRollViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DiceRollUiState())
    val uiState: StateFlow<DiceRollUiState> = _uiState.asStateFlow()

    fun setDiceType(type: DiceType) {
        _uiState.update { it.copy(diceType = type, results = emptyList()) }
    }

    fun setDiceCount(count: Int) {
        _uiState.update { it.copy(diceCount = count.coerceIn(1, 4), results = emptyList()) }
    }

    fun roll() {
        if (_uiState.value.isRolling) return
        _uiState.update { it.copy(isRolling = true) }

        viewModelScope.launch {
            val count = _uiState.value.diceCount
            val sides = _uiState.value.diceType.sides
            // Rapid random cycle animation
            repeat(8) {
                val tempResults = List(count) { (1..sides).random() }
                _uiState.update { it.copy(results = tempResults) }
                delay(60L)
            }
            // Final result
            val finalResults = List(count) { (1..sides).random() }
            _uiState.update { it.copy(results = finalResults, isRolling = false) }
        }
    }
}
