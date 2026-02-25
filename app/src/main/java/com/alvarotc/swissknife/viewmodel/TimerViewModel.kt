package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TimerMode { STOPWATCH, COUNTDOWN }

data class TimerUiState(
    val mode: TimerMode = TimerMode.STOPWATCH,
    val elapsedMs: Long = 0L,
    val countdownTotalMs: Long = 60_000L,
    val countdownRemainingMs: Long = 60_000L,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
    val countdownMinutes: Int = 1,
    val countdownSeconds: Int = 0,
)

class TimerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun setMode(mode: TimerMode) {
        if (_uiState.value.isRunning) return
        _uiState.update {
            it.copy(
                mode = mode,
                elapsedMs = 0L,
                isFinished = false,
                countdownRemainingMs = it.countdownTotalMs,
            )
        }
    }

    fun setCountdownMinutes(minutes: Int) {
        if (_uiState.value.isRunning) return
        val m = minutes.coerceIn(0, 99)
        val totalMs = (m * 60_000L) + (_uiState.value.countdownSeconds * 1000L)
        _uiState.update {
            it.copy(
                countdownMinutes = m,
                countdownTotalMs = totalMs,
                countdownRemainingMs = totalMs,
            )
        }
    }

    fun setCountdownSeconds(seconds: Int) {
        if (_uiState.value.isRunning) return
        val s = seconds.coerceIn(0, 59)
        val totalMs = (_uiState.value.countdownMinutes * 60_000L) + (s * 1000L)
        _uiState.update {
            it.copy(
                countdownSeconds = s,
                countdownTotalMs = totalMs,
                countdownRemainingMs = totalMs,
            )
        }
    }

    fun startPause() {
        if (_uiState.value.isFinished) return
        if (_uiState.value.isRunning) {
            pause()
        } else {
            start()
        }
    }

    private fun start() {
        _uiState.update { it.copy(isRunning = true) }
        timerJob =
            viewModelScope.launch {
                val tickInterval = 10L
                while (_uiState.value.isRunning) {
                    delay(tickInterval)
                    _uiState.update { state ->
                        when (state.mode) {
                            TimerMode.STOPWATCH -> state.copy(elapsedMs = state.elapsedMs + tickInterval)
                            TimerMode.COUNTDOWN -> {
                                val remaining = (state.countdownRemainingMs - tickInterval).coerceAtLeast(0L)
                                if (remaining <= 0L) {
                                    state.copy(
                                        countdownRemainingMs = 0L,
                                        isRunning = false,
                                        isFinished = true,
                                    )
                                } else {
                                    state.copy(countdownRemainingMs = remaining)
                                }
                            }
                        }
                    }
                    if (_uiState.value.isFinished) break
                }
            }
    }

    private fun pause() {
        _uiState.update { it.copy(isRunning = false) }
        timerJob?.cancel()
    }

    fun reset() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                elapsedMs = 0L,
                countdownRemainingMs = it.countdownTotalMs,
                isRunning = false,
                isFinished = false,
            )
        }
    }
}
