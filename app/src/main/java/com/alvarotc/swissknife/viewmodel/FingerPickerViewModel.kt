package com.alvarotc.swissknife.viewmodel

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Finger(
    val id: Int,
    val position: Offset,
    val color: Color,
    val isWinner: Boolean = false,
)

data class FingerPickerUiState(
    val fingers: Map<Int, Finger> = emptyMap(),
    val numWinners: Int = 1,
    val isCountingDown: Boolean = false,
    val countdown: Int = 5,
    val winners: List<Int> = emptyList(),
)

class FingerPickerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FingerPickerUiState())
    val uiState: StateFlow<FingerPickerUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    private val fingerColors =
        listOf(
            Color(0xFF42A5F5),
            Color(0xFFEF5350),
            Color(0xFF66BB6A),
            Color(0xFFFFC107),
            Color(0xFFAB47BC),
            Color(0xFF26A69A),
            Color(0xFFFF7043),
            Color(0xFFEC407A),
            Color(0xFF5C6BC0),
            Color(0xFF8D6E63),
        )

    fun addFinger(
        id: Int,
        position: Offset,
    ) {
        if (_uiState.value.winners.isNotEmpty()) return

        _uiState.update { state ->
            // Skip if finger already exists (Press events include all active pointers)
            if (state.fingers.containsKey(id)) return@update state

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

        // Auto-start countdown when 2+ fingers are present
        if (_uiState.value.fingers.size >= 2 && !_uiState.value.isCountingDown) {
            startCountdown()
        }
    }

    fun removeFinger(id: Int) {
        if (_uiState.value.winners.isNotEmpty()) return

        // Cancel countdown if a finger is lifted
        if (_uiState.value.isCountingDown) {
            cancelCountdown()
        }

        _uiState.update { state ->
            state.copy(fingers = state.fingers - id)
        }

        // Restart countdown if still 2+ fingers
        if (_uiState.value.fingers.size >= 2) {
            startCountdown()
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

    private fun startCountdown() {
        countdownJob?.cancel()
        countdownJob =
            viewModelScope.launch {
                _uiState.update { it.copy(isCountingDown = true, countdown = 5) }

                for (i in 4 downTo 0) {
                    delay(1000)
                    _uiState.update { state -> state.copy(countdown = i) }
                }

                selectWinners()
            }
    }

    private fun cancelCountdown() {
        countdownJob?.cancel()
        countdownJob = null
        _uiState.update { it.copy(isCountingDown = false, countdown = 5) }
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
        countdownJob?.cancel()
        countdownJob = null
        _uiState.value = FingerPickerUiState(numWinners = _uiState.value.numWinners)
    }
}
