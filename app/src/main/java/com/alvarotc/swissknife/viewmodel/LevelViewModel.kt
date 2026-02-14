package com.alvarotc.swissknife.viewmodel

import android.app.Application
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs

data class LevelUiState(
    val pitch: Float = 0f,
    val roll: Float = 0f,
    val isLevel: Boolean = false,
    val sensorAvailable: Boolean = true,
)

class LevelViewModel(
    application: Application,
) : AndroidViewModel(application), SensorEventListener {
    private val _uiState = MutableStateFlow(LevelUiState())
    val uiState: StateFlow<LevelUiState> = _uiState.asStateFlow()

    private val sensorManager = application.getSystemService(SensorManager::class.java)
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val levelThreshold = 2.0f

    init {
        if (accelerometer == null) {
            _uiState.update { it.copy(sensorAvailable = false) }
        }
    }

    fun startListening() {
        accelerometer?.let {
            sensorManager?.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_UI,
            )
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]

            viewModelScope.launch {
                _uiState.update { state ->
                    state.copy(
                        pitch = x,
                        roll = y,
                        isLevel = abs(x) < levelThreshold && abs(y) < levelThreshold,
                    )
                }
            }
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int,
    ) {
        // Not needed
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
    }
}
