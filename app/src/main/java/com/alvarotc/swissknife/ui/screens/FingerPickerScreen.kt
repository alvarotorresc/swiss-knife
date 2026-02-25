package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentFinger
import com.alvarotc.swissknife.viewmodel.FingerPickerViewModel

@Composable
fun FingerPickerScreen(viewModel: FingerPickerViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Pulse animation for winners
    val infiniteTransition = rememberInfiniteTransition(label = "winnerPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(600),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "pulseScale",
    )
    val pulseGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(600),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "pulseGlowAlpha",
    )

    // Full-screen flash when winners are picked
    val flashAlpha = remember { Animatable(0f) }
    val winnerFingerColors = state.winners.mapNotNull { id -> state.fingers[id]?.color }
    val flashColor = winnerFingerColors.firstOrNull() ?: AccentFinger

    LaunchedEffect(state.winners) {
        if (state.winners.isNotEmpty()) {
            // Two quick flashes
            flashAlpha.snapTo(0.5f)
            flashAlpha.animateTo(0f, animationSpec = tween(300))
            flashAlpha.animateTo(0.3f, animationSpec = tween(150))
            flashAlpha.animateTo(0f, animationSpec = tween(500))
        }
    }

    // Loser fade out
    val loserAlpha by animateFloatAsState(
        targetValue = if (state.winners.isNotEmpty()) 0.1f else 0.8f,
        animationSpec = tween(durationMillis = 800),
        label = "loserAlpha",
    )

    // Countdown scale pulse
    val countdownScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(500),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "countdownScale",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.number_of_winners),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { viewModel.setNumWinners(state.numWinners - 1) },
                        enabled = !state.isCountingDown && state.winners.isEmpty(),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Remove,
                            contentDescription = stringResource(R.string.remove),
                            tint = AccentFinger,
                        )
                    }
                    Text(
                        text = state.numWinners.toString(),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                    IconButton(
                        onClick = { viewModel.setNumWinners(state.numWinners + 1) },
                        enabled = !state.isCountingDown && state.winners.isEmpty(),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.add),
                            tint = AccentFinger,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                if (state.isCountingDown) {
                    Text(
                        text = state.countdown.toString(),
                        fontSize = (96f * countdownScale).sp,
                        style = MaterialTheme.typography.displayLarge,
                        color = AccentFinger,
                    )
                } else if (state.winners.isEmpty() && state.fingers.isEmpty()) {
                    Text(
                        text = stringResource(R.string.place_fingers),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                Canvas(modifier = Modifier.fillMaxSize()) {
                    state.fingers.values.forEach { finger ->
                        if (finger.isWinner) {
                            // Outer glow ring (pulsing)
                            val glowRadius = 80.dp.toPx() * pulseScale
                            drawCircle(
                                color = finger.color.copy(alpha = pulseGlowAlpha),
                                radius = glowRadius + 15.dp.toPx(),
                                center = finger.position,
                            )
                            // Pulsing glow stroke ring
                            drawCircle(
                                color = finger.color.copy(alpha = 0.6f),
                                radius = glowRadius,
                                center = finger.position,
                                style = Stroke(width = 4.dp.toPx()),
                            )
                            // Winner circle
                            drawCircle(
                                color = finger.color,
                                radius = 80.dp.toPx(),
                                center = finger.position,
                            )
                        } else {
                            // Loser — faded
                            val alpha = if (state.winners.isNotEmpty()) loserAlpha else 0.8f
                            drawCircle(
                                color = finger.color.copy(alpha = alpha),
                                radius = 60.dp.toPx(),
                                center = finger.position,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.winners.isNotEmpty()) {
                Text(
                    text =
                        if (state.winners.size == 1) {
                            stringResource(R.string.winner_selected)
                        } else {
                            stringResource(R.string.winners_selected)
                        },
                    color = AccentFinger,
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { viewModel.reset() }) {
                    Text(
                        text = stringResource(R.string.reset),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Full-screen color flash overlay
        if (flashAlpha.value > 0f) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(flashColor.copy(alpha = flashAlpha.value)),
            )
        }
    }
}
