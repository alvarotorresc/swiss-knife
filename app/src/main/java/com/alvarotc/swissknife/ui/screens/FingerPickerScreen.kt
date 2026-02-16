package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentFinger
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.viewmodel.FingerPickerViewModel

@Composable
fun FingerPickerScreen(viewModel: FingerPickerViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Winners selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.number_of_winners),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.setNumWinners(state.numWinners - 1) },
                    enabled = !state.isCountingDown && state.winners.isEmpty(),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = stringResource(R.string.decrement),
                        tint = AccentFinger,
                    )
                }
                Text(
                    text = state.numWinners.toString(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                IconButton(
                    onClick = { viewModel.setNumWinners(state.numWinners + 1) },
                    enabled = !state.isCountingDown && state.winners.isEmpty(),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.increment),
                        tint = AccentFinger,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Touch area
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                when (event.type) {
                                    PointerEventType.Press -> {
                                        event.changes.forEach { change ->
                                            viewModel.addFinger(
                                                change.id.value.toInt(),
                                                change.position,
                                            )
                                        }
                                    }
                                    PointerEventType.Release -> {
                                        event.changes.forEach { change ->
                                            viewModel.removeFinger(change.id.value.toInt())
                                        }
                                    }
                                    PointerEventType.Move -> {
                                        event.changes.forEach { change ->
                                            viewModel.updateFingerPosition(
                                                change.id.value.toInt(),
                                                change.position,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
            contentAlignment = Alignment.Center,
        ) {
            // Countdown display
            if (state.isCountingDown) {
                Text(
                    text = state.countdown.toString(),
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentFinger,
                )
            } else if (state.winners.isEmpty() && state.fingers.isEmpty()) {
                Text(
                    text = stringResource(R.string.place_fingers),
                    color = DarkOnSurfaceVariant,
                    fontSize = 18.sp,
                )
            }

            // Draw finger circles
            Canvas(modifier = Modifier.fillMaxSize()) {
                state.fingers.values.forEach { finger ->
                    val alpha =
                        if (finger.isWinner) {
                            1f
                        } else if (state.winners.isNotEmpty()) {
                            0.2f
                        } else {
                            0.8f
                        }
                    val radius = if (finger.isWinner) 80.dp.toPx() else 60.dp.toPx()

                    drawCircle(
                        color = finger.color.copy(alpha = alpha),
                        radius = radius,
                        center = finger.position,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status / Result
        if (state.winners.isNotEmpty()) {
            Text(
                text =
                    if (state.winners.size == 1) {
                        stringResource(R.string.winner_selected)
                    } else {
                        stringResource(R.string.winners_selected)
                    },
                color = AccentFinger,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { viewModel.reset() }) {
                Text(stringResource(R.string.reset), color = DarkOnSurfaceVariant)
            }
        }
    }
}
