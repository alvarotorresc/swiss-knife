package com.alvarotc.swissknife.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class QRToolUiState(
    val inputText: String = "",
    val qrBitmap: Bitmap? = null,
)

class QRToolViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(QRToolUiState())
    val uiState: StateFlow<QRToolUiState> = _uiState.asStateFlow()

    fun setInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun generateQR() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return

        try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE,
                    )
                }
            }

            _uiState.update { it.copy(qrBitmap = bitmap) }
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun clear() {
        _uiState.value = QRToolUiState()
    }
}
