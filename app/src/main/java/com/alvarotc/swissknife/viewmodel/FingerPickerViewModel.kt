package com.alvarotc.swissknife.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Finger(
    val id: Int,
    val position: Offset,
    val color: androidx.compose.ui.graphics.Color,
    val isWinner: Boolean = false,
)

data class FingerPickerUiState(
    val fingers: Map<Int, Finger> = emptyMap(),
    val numWinners: Int = 1,
    val isCountingDown: Boolean = false,
    val countdown: Int = 3,
    val winners: List<Int> = emptyList(),
)

class FingerPickerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FingerPickerUiState())
    val uiState: StateFlow<FingerPickerUiState> = _uiState.asStateFlow()

    private val fingerColors =
        listOf(
            androidx.compose.ui.graphics.Color(0xFF42A5F5),
            androidx.compose.ui.graphics.Color(0xFFEF5350),
            androidx.compose.ui.graphics.Color(0xFF66BB6A),
            androidx.compose.ui.graphics.Color(0xFFFFC107),
            androidx.compose.ui.graphics.Color(0xFFAB47BC),
            androidx.compose.ui.graphics.Color(0xFF26A69A),
            androidx.compose.ui.graphics.Color(0xFFFF7043),
            androidx.compose.ui.graphics.Color(0xFFEC407A),
            androidx.compose.ui.graphics.Color(0xFF5C6BC0),
            androidx.compose.ui.graphics.Color(0xFF8D6E63),
        )

    fun addFinger(
        id: Int,
        position: Offset,
    ) {
        if (_uiState.value.isCountingDown || _uiState.value.winners.isNotEmpty()) return

        _uiState.update { state ->
            val colorIndex = state.fingers.size % fingerColors.size
            state.copy(
                fingers =
                    state.fingers +
                        (
                            id to
                                Finger(
                                    id = id,
                                    position = position,
                                    color = fingerColors[colorIndex],
                                )
                        ),
            )
        }
    }

    fun removeFinger(id: Int) {
        if (_uiState.value.isCountingDown) {
            reset()
            return
        }

        _uiState.update { state ->
            state.copy(fingers = state.fingers - id)
        }
    }

    fun updateFingerPosition(
        id: Int,
        position: Offset,
    ) {
        _uiState.update { state ->
            val finger = state.fingers[id] ?: return@update state
            state.copy(
                fingers = state.fingers + (id to finger.copy(position = position)),
            )
        }
    }

    fun setNumWinners(num: Int) {
        _uiState.update { it.copy(numWinners = num.coerceIn(1, 10)) }
    }

    fun startCountdown() {
        if (_uiState.value.fingers.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isCountingDown = true, countdown = 3) }

            repeat(3) {
                delay(1000)
                _uiState.update { state -> state.copy(countdown = state.countdown - 1) }
            }

            selectWinners()
        }
    }

    private fun selectWinners() {
        val fingerIds = _uiState.value.fingers.keys.toList()
        val numWinners = _uiState.value.numWinners.coerceAtMost(fingerIds.size)
        val winners = fingerIds.shuffled().take(numWinners)

        _uiState.update { state ->
            state.copy(
                winners = winners,
                fingers =
                    state.fingers.mapValues { (id, finger) ->
                        finger.copy(isWinner = id in winners)
                    },
                isCountingDown = false,
            )
        }
    }

    fun reset() {
        _uiState.value = FingerPickerUiState(numWinners = _uiState.value.numWinners)
    }
}
