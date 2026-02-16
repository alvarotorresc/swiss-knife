package com.alvarotc.swissknife.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.alvarotc.swissknife.ui.theme.AccentDice
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.DiceType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PolyDiceFace(
    value: Int,
    diceType: DiceType,
    modifier: Modifier = Modifier,
    accentColor: Color = AccentDice,
) {
    val sides = polygonSides(diceType)

    Canvas(modifier = modifier.size(80.dp)) {
        val canvasSize = size.minDimension
        val center = Offset(canvasSize / 2f, canvasSize / 2f)
        val radius = canvasSize * 0.42f
        val strokeWidth = 2.dp.toPx()

        // Dark background fill
        val bgPath = polygonPath(center, radius + strokeWidth, sides)
        drawPath(bgPath, color = DarkSurfaceVariant)

        // Accent border
        val borderPath = polygonPath(center, radius, sides)
        drawPath(borderPath, color = accentColor, style = Stroke(width = strokeWidth))

        // Result number in center
        drawContext.canvas.nativeCanvas.apply {
            val valuePaint =
                android.graphics.Paint().apply {
                    color = Color.White.toArgb()
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = canvasSize * 0.32f
                    isAntiAlias = true
                    isFakeBoldText = true
                }
            drawText(
                value.toString(),
                center.x,
                center.y + valuePaint.textSize * 0.35f,
                valuePaint,
            )

            // Die type label at bottom
            val labelPaint =
                android.graphics.Paint().apply {
                    color = accentColor.toArgb()
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = canvasSize * 0.13f
                    isAntiAlias = true
                }
            drawText(
                diceType.label,
                center.x,
                canvasSize * 0.92f,
                labelPaint,
            )
        }
    }
}

private fun polygonSides(diceType: DiceType): Int =
    when (diceType) {
        DiceType.D4 -> 3
        DiceType.D6 -> 4
        DiceType.D8 -> 4
        DiceType.D10 -> 5
        DiceType.D12 -> 6
        DiceType.D20 -> 3
    }

private fun polygonPath(
    center: Offset,
    radius: Float,
    sides: Int,
): Path {
    val path = Path()
    val angleOffset =
        when (sides) {
            3 -> -Math.PI / 2.0 // Triangle pointing up
            4 -> -Math.PI / 4.0 // Diamond (rotated square)
            else -> -Math.PI / 2.0
        }

    for (i in 0 until sides) {
        val angle = angleOffset + 2.0 * Math.PI * i / sides
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}
