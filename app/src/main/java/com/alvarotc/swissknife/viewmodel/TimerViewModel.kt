package com.alvarotc.swissknife.viewmodel

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TimerMode {
    STOPWATCH,
    COUNTDOWN,
}

data class TimerUiState(
    val mode: TimerMode = TimerMode.STOPWATCH,
    val isRunning: Boolean = false,
    val elapsedMillis: Long = 0,
    val targetMinutes: Int = 1,
    val targetSeconds: Int = 0,
)

class TimerViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long = 0

    fun setMode(mode: TimerMode) {
        reset()
        _uiState.update { it.copy(mode = mode) }
    }

    fun setTargetMinutes(minutes: Int) {
        _uiState.update { it.copy(targetMinutes = minutes.coerceIn(0, 99)) }
    }

    fun setTargetSeconds(seconds: Int) {
        _uiState.update { it.copy(targetSeconds = seconds.coerceIn(0, 59)) }
    }

    fun startPause() {
        if (_uiState.value.isRunning) {
            pause()
        } else {
            start()
        }
    }

    private fun start() {
        val state = _uiState.value
        startTime = SystemClock.elapsedRealtime() - state.elapsedMillis
        _uiState.update { it.copy(isRunning = true) }

        timerJob =
            viewModelScope.launch {
                while (true) {
                    val elapsed = SystemClock.elapsedRealtime() - startTime
                    _uiState.update { it.copy(elapsedMillis = elapsed) }

                    // Check countdown completion
                    if (state.mode == TimerMode.COUNTDOWN) {
                        val targetMillis =
                            (state.targetMinutes * 60 + state.targetSeconds) * 1000L
                        if (elapsed >= targetMillis) {
                            pause()
                            break
                        }
                    }

                    delay(10)
                }
            }
    }

    private fun pause() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun reset() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                isRunning = false,
                elapsedMillis = 0,
            )
        }
        startTime = 0
    }

    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val centiseconds = (millis % 1000) / 10
        return String.format("%02d:%02d.%02d", minutes, seconds, centiseconds)
    }

    fun getRemainingMillis(): Long {
        val state = _uiState.value
        val targetMillis = (state.targetMinutes * 60 + state.targetSeconds) * 1000L
        return (targetMillis - state.elapsedMillis).coerceAtLeast(0)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
