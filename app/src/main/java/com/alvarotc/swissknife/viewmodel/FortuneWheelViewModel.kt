package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class FortuneWheelError {
    data object ItemAlreadyAdded : FortuneWheelError()

    data object NeedMoreItems : FortuneWheelError()
}

data class FortuneWheelUiState(
    val itemInput: String = "",
    val items: List<String> = emptyList(),
    val isSpinning: Boolean = false,
    val rotation: Float = 0f,
    val winner: String? = null,
    val error: FortuneWheelError? = null,
)

class FortuneWheelViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FortuneWheelUiState())
    val uiState: StateFlow<FortuneWheelUiState> = _uiState.asStateFlow()

    fun setItemInput(item: String) {
        _uiState.update { it.copy(itemInput = item, error = null) }
    }

    fun addItem() {
        val item = _uiState.value.itemInput.trim()
        when {
            item.isBlank() -> return
            _uiState.value.items.any { it.equals(item, ignoreCase = true) } -> {
                _uiState.update { it.copy(error = FortuneWheelError.ItemAlreadyAdded) }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        items = it.items + item,
                        itemInput = "",
                        winner = null,
                        error = null,
                    )
                }
            }
        }
    }

    fun removeItem(item: String) {
        _uiState.update {
            it.copy(
                items = it.items - item,
                winner = null,
            )
        }
    }

    fun spin() {
        val items = _uiState.value.items
        if (items.size < 2) {
            _uiState.update { it.copy(error = FortuneWheelError.NeedMoreItems) }
            return
        }

        viewModelScope.launch {
            val initialRotation = _uiState.value.rotation
            _uiState.update { it.copy(isSpinning = true, winner = null, error = null) }

            val spins = Random.nextInt(5, 8)
            val totalSpin = spins * 360f + Random.nextFloat() * 360f

            val duration = 3000L
            val steps = 60
            val stepDuration = duration / steps

            var currentRotation = initialRotation

            repeat(steps) { step ->
                val progress = (step + 1).toFloat() / steps
                val easedProgress = 1f - (1f - progress) * (1f - progress) * (1f - progress)
                currentRotation = initialRotation + totalSpin * easedProgress
                _uiState.update { it.copy(rotation = currentRotation % 360f) }
                delay(stepDuration)
            }

            // Pointer is at top (270° in arc coordinates)
            // Find which segment is under the pointer
            val finalRotation = currentRotation % 360f
            val degreesPerItem = 360f / items.size
            val pointerAngle = ((270f - finalRotation) % 360f + 360f) % 360f
            val winnerIndex = (pointerAngle / degreesPerItem).toInt() % items.size

            _uiState.update {
                it.copy(
                    isSpinning = false,
                    rotation = finalRotation,
                    winner = items[winnerIndex],
                )
            }
        }
    }

    fun clearWinner() {
        _uiState.update { it.copy(winner = null) }
    }

    fun reset() {
        _uiState.value = FortuneWheelUiState()
    }
}
