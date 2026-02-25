package com.alvarotc.swissknife.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

data class RandomColorUiState(
    val color: Color? = null,
    val hexString: String = "",
    val rgbString: String = "",
    val history: List<Pair<Color, String>> = emptyList(),
)

class RandomColorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RandomColorUiState())
    val uiState: StateFlow<RandomColorUiState> = _uiState.asStateFlow()

    fun generate() {
        val r = Random.nextInt(256)
        val g = Random.nextInt(256)
        val b = Random.nextInt(256)
        val color = Color(r, g, b)
        val hex = "#%02X%02X%02X".format(r, g, b)
        val rgb = "$r, $g, $b"

        _uiState.update { current ->
            val updatedHistory = (listOf(color to hex) + current.history).take(5)
            current.copy(
                color = color,
                hexString = hex,
                rgbString = rgb,
                history = updatedHistory,
            )
        }
    }

    fun reshow(
        color: Color,
        hex: String,
    ) {
        val r = (color.red * 255).toInt()
        val g = (color.green * 255).toInt()
        val b = (color.blue * 255).toInt()
        val rgb = "$r, $g, $b"
        _uiState.update { it.copy(color = color, hexString = hex, rgbString = rgb) }
    }

    fun reset() {
        _uiState.update { RandomColorUiState() }
    }
}
