package com.alvarotc.swissknife.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.alvarotc.swissknife.ui.theme.AccentDice
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant

private val dotPositions =
    mapOf(
        1 to listOf(Pair(0.5f, 0.5f)),
        2 to listOf(Pair(0.25f, 0.25f), Pair(0.75f, 0.75f)),
        3 to listOf(Pair(0.25f, 0.25f), Pair(0.5f, 0.5f), Pair(0.75f, 0.75f)),
        4 to
            listOf(
                Pair(0.25f, 0.25f),
                Pair(0.75f, 0.25f),
                Pair(0.25f, 0.75f),
                Pair(0.75f, 0.75f),
            ),
        5 to
            listOf(
                Pair(0.25f, 0.25f),
                Pair(0.75f, 0.25f),
                Pair(0.5f, 0.5f),
                Pair(0.25f, 0.75f),
                Pair(0.75f, 0.75f),
            ),
        6 to
            listOf(
                Pair(0.25f, 0.25f),
                Pair(0.75f, 0.25f),
                Pair(0.25f, 0.5f),
                Pair(0.75f, 0.5f),
                Pair(0.25f, 0.75f),
                Pair(0.75f, 0.75f),
            ),
    )

@Composable
fun DiceFace(
    value: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkSurfaceVariant,
    dotColor: Color = AccentDice,
) {
    Canvas(modifier = modifier.size(80.dp)) {
        val canvasSize = size.minDimension
        val cornerRadius = canvasSize * 0.15f
        val dotRadius = canvasSize * 0.08f
        val padding = canvasSize * 0.1f

        drawRoundRect(
            color = backgroundColor,
            cornerRadius = CornerRadius(cornerRadius),
        )

        val positions = dotPositions[value.coerceIn(1, 6)] ?: return@Canvas
        for ((xFraction, yFraction) in positions) {
            val x = padding + xFraction * (canvasSize - 2 * padding)
            val y = padding + yFraction * (canvasSize - 2 * padding)
            drawCircle(
                color = dotColor,
                radius = dotRadius,
                center = Offset(x, y),
            )
        }
    }
}
