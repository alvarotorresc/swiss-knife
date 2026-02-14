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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentWheel
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkOutline
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.FortuneWheelError
import com.alvarotc.swissknife.viewmodel.FortuneWheelViewModel

@Composable
fun FortuneWheelScreen(viewModel: FortuneWheelViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = DarkOutline,
            focusedBorderColor = AccentWheel,
            unfocusedLabelColor = DarkOnSurfaceVariant,
            focusedLabelColor = AccentWheel,
            cursorColor = AccentWheel,
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedContainerColor = DarkSurfaceVariant,
            focusedContainerColor = DarkSurfaceVariant,
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (state.items.isEmpty() || state.winner != null) {
            // Input mode
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
                IconButton(onClick = { viewModel.addItem() }, enabled = !state.isSpinning) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add),
                        tint = AccentWheel,
                    )
                }
            }

            if (state.error != null) {
                Text(
                    text = when (state.error) {
                        FortuneWheelError.ItemAlreadyAdded -> stringResource(R.string.error_item_already_added)
                        FortuneWheelError.NeedMoreItems -> stringResource(R.string.error_need_more_items)
                        null -> ""
                    },
                    color = Color(0xFFEF5350),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Items list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                itemsIndexed(state.items) { _, item ->
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = item,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = { viewModel.removeItem(item) }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = stringResource(R.string.remove),
                                    tint = DarkOnSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Wheel display
            Spacer(modifier = Modifier.height(32.dp))

            WheelCanvas(
                items = state.items,
                rotation = state.rotation,
                modifier = Modifier.size(300.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.spin_the_wheel),
                color = DarkOnSurfaceVariant,
                fontSize = 14.sp,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result
        if (state.winner != null) {
            Text(
                text = stringResource(R.string.winner_is),
                color = DarkOnSurfaceVariant,
                fontSize = 16.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.winner ?: "",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = AccentWheel,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Action buttons
        if (state.winner == null && state.items.isNotEmpty()) {
            Button(
                onClick = { viewModel.spin() },
                enabled = !state.isSpinning && state.items.size >= 2,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentWheel),
            ) {
                Text(
                    text = stringResource(R.string.spin),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                )
            }
        }

        if (state.winner != null) {
            Button(
                onClick = { viewModel.reset() },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentWheel),
            ) {
                Text(
                    text = stringResource(R.string.new_spin),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                )
            }
            TextButton(onClick = { viewModel.reset() }) {
                Text(stringResource(R.string.cancel), color = DarkOnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun WheelCanvas(
    items: List<String>,
    rotation: Float,
    modifier: Modifier = Modifier,
) {
    val colors =
        listOf(
            Color(0xFFEF5350),
            Color(0xFF42A5F5),
            Color(0xFF66BB6A),
            Color(0xFFFFC107),
            Color(0xFFAB47BC),
            Color(0xFF26A69A),
        )

    Canvas(modifier = modifier) {
        val sweepAngle = 360f / items.size

        rotate(rotation) {
            items.forEachIndexed { index, _ ->
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = index * sweepAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = Offset.Zero,
                    size = Size(size.width, size.height),
                )
            }
        }

        // Pointer at top
        val pointerSize = 30f
        drawPath(
            path =
                androidx.compose.ui.graphics.Path().apply {
                    moveTo(size.width / 2, 0f)
                    lineTo(size.width / 2 - pointerSize / 2, pointerSize)
                    lineTo(size.width / 2 + pointerSize / 2, pointerSize)
                    close()
                },
            color = Color.White,
        )
    }
}
