package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentWheel
import com.alvarotc.swissknife.viewmodel.FortuneWheelError
import com.alvarotc.swissknife.viewmodel.FortuneWheelViewModel

@Composable
fun FortuneWheelScreen(viewModel: FortuneWheelViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            OutlinedTextField(
                value = state.itemInput,
                onValueChange = { viewModel.setItemInput(it) },
                label = { Text(stringResource(R.string.option)) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                enabled = !state.isSpinning,
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { viewModel.addItem() },
                enabled = !state.isSpinning,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.add),
                    tint = AccentWheel,
                )
            }
        }

        if (state.error != null) {
            Text(
                text =
                    when (state.error) {
                        FortuneWheelError.ItemAlreadyAdded -> stringResource(R.string.error_item_already_added)
                        FortuneWheelError.NeedMoreItems -> stringResource(R.string.error_need_more_items)
                        null -> ""
                    },
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp),
            )
        }

        if (state.items.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.items) { item ->
                    SuggestionChip(
                        onClick = { if (!state.isSpinning) viewModel.removeItem(item) },
                        label = { Text(item) },
                        icon = {
                            if (!state.isSpinning) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = stringResource(R.string.remove),
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        },
                        colors =
                            SuggestionChipDefaults.suggestionChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface,
                                iconContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.items.size >= 2) {
            MinimalistWheelCanvas(
                items = state.items,
                rotation = state.rotation,
                modifier = Modifier.size(280.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.winner != null) {
                Text(
                    text = stringResource(R.string.winner_is),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.winner ?: "",
                    style = MaterialTheme.typography.headlineLarge,
                    color = AccentWheel,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    if (state.winner != null) {
                        viewModel.clearWinner()
                    } else {
                        viewModel.spin()
                    }
                },
                enabled = !state.isSpinning,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
            ) {
                Text(
                    text =
                        if (state.winner != null) {
                            stringResource(R.string.new_spin)
                        } else {
                            stringResource(R.string.spin)
                        },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        } else {
            Text(
                text = stringResource(R.string.spin_the_wheel),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 48.dp),
            )
        }
    }
}

@Composable
private fun MinimalistWheelCanvas(
    items: List<String>,
    rotation: Float,
    modifier: Modifier = Modifier,
) {
    val segmentColors =
        listOf(
            Color(0xFFF43F5E),
            Color(0xFF3B82F6),
            Color(0xFF22C55E),
            Color(0xFFFBBF24),
            Color(0xFF8B5CF6),
            Color(0xFF14B8A6),
            Color(0xFFFB923C),
            Color(0xFFEC4899),
        )

    val surfaceColor = MaterialTheme.colorScheme.surface
    val outlineColor = MaterialTheme.colorScheme.outline

    Canvas(modifier = modifier) {
        val sweepAngle = 360f / items.size
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        drawCircle(
            color = surfaceColor,
            radius = radius,
            center = center,
        )

        rotate(rotation, pivot = center) {
            items.forEachIndexed { index, item ->
                val startAngle = index * sweepAngle
                val color = segmentColors[index % segmentColors.size]

                drawArc(
                    color = color,
                    startAngle = startAngle + 1f,
                    sweepAngle = sweepAngle - 2f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Butt),
                )

                val lineAngle = Math.toRadians(startAngle.toDouble())
                val endX = (center.x + radius * kotlin.math.cos(lineAngle)).toFloat()
                val endY = (center.y + radius * kotlin.math.sin(lineAngle)).toFloat()
                drawLine(
                    color = outlineColor,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1.5.dp.toPx(),
                )

                val textAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                val textRadius = radius * 0.55f
                val textX = (center.x + textRadius * kotlin.math.cos(textAngle)).toFloat()
                val textY = (center.y + textRadius * kotlin.math.sin(textAngle)).toFloat()

                drawContext.canvas.nativeCanvas.apply {
                    val paint =
                        android.graphics.Paint().apply {
                            this.color = color.toArgb()
                            textAlign = android.graphics.Paint.Align.CENTER
                            textSize = 13.dp.toPx()
                            isAntiAlias = true
                            isFakeBoldText = true
                        }
                    val label = if (item.length > 10) item.take(9) + "\u2026" else item
                    drawText(label, textX, textY + paint.textSize / 3, paint)
                }
            }
        }

        drawCircle(
            color = outlineColor,
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx()),
        )

        val pointerSize = 14.dp.toPx()
        drawPath(
            path =
                androidx.compose.ui.graphics.Path().apply {
                    moveTo(center.x, center.y - radius - 6.dp.toPx())
                    lineTo(center.x - pointerSize / 2, center.y - radius + pointerSize)
                    lineTo(center.x + pointerSize / 2, center.y - radius + pointerSize)
                    close()
                },
            color = AccentWheel,
        )

        drawCircle(
            color = outlineColor,
            radius = 6.dp.toPx(),
            center = center,
        )
    }
}
