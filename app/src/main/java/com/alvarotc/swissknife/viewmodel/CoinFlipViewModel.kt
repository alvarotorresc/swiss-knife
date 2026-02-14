package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class CoinSide { HEADS, TAILS }

data class CoinFlipUiState(
    val result: CoinSide? = null,
    val totalFlips: Int = 0,
    val headsCount: Int = 0,
    val tailsCount: Int = 0,
    val isFlipping: Boolean = false,
)

class CoinFlipViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CoinFlipUiState())
    val uiState: StateFlow<CoinFlipUiState> = _uiState.asStateFlow()

    fun flip() {
        val side = if (Math.random() < 0.5) CoinSide.HEADS else CoinSide.TAILS
        _uiState.update { state ->
            state.copy(
                result = side,
                totalFlips = state.totalFlips + 1,
                headsCount = state.headsCount + if (side == CoinSide.HEADS) 1 else 0,
                tailsCount = state.tailsCount + if (side == CoinSide.TAILS) 1 else 0,
                isFlipping = true,
            )
        }
    }

    fun onAnimationFinished() {
        _uiState.update { it.copy(isFlipping = false) }
    }

    fun reset() {
        _uiState.value = CoinFlipUiState()
    }
}
