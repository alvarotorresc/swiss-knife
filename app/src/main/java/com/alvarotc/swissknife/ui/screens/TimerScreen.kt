package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentTimer
import com.alvarotc.swissknife.ui.theme.AccentTimerContainer
import com.alvarotc.swissknife.viewmodel.TimerMode
import com.alvarotc.swissknife.viewmodel.TimerViewModel

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val displayMs =
        when (state.mode) {
            TimerMode.STOPWATCH -> state.elapsedMs
            TimerMode.COUNTDOWN -> state.countdownRemainingMs
        }

    val minutes = (displayMs / 60_000).toInt()
    val seconds = ((displayMs % 60_000) / 1000).toInt()
    val centis = ((displayMs % 1000) / 10).toInt()
    val timeText = "%02d:%02d.%02d".format(minutes, seconds, centis)

    val progress =
        when (state.mode) {
            TimerMode.STOPWATCH -> (displayMs % 60_000).toFloat() / 60_000f
            TimerMode.COUNTDOWN -> {
                if (state.countdownTotalMs > 0) {
                    state.countdownRemainingMs.toFloat() / state.countdownTotalMs.toFloat()
                } else {
                    0f
                }
            }
        }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 50, easing = LinearEasing),
        label = "progress",
    )

    val infiniteTransition = rememberInfiniteTransition(label = "finished")
    val finishedPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(500),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "finishedPulse",
    )

    val arcColor = if (state.isFinished) AccentTimer.copy(alpha = finishedPulse) else AccentTimer
    val trackColor = AccentTimerContainer

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TimerMode.entries.forEach { mode ->
                FilterChip(
                    selected = state.mode == mode,
                    onClick = { viewModel.setMode(mode) },
                    label = {
                        Text(
                            when (mode) {
                                TimerMode.STOPWATCH -> stringResource(R.string.stopwatch)
                                TimerMode.COUNTDOWN -> stringResource(R.string.countdown)
                            },
                        )
                    },
                    enabled = !state.isRunning,
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentTimerContainer,
                            selectedLabelColor = AccentTimer,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
            }
        }

        if (state.mode == TimerMode.COUNTDOWN && !state.isRunning && !state.isFinished) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                CounterControl(
                    label = stringResource(R.string.timer_minutes),
                    value = state.countdownMinutes,
                    onIncrement = { viewModel.setCountdownMinutes(state.countdownMinutes + 1) },
                    onDecrement = { viewModel.setCountdownMinutes(state.countdownMinutes - 1) },
                )
                Spacer(modifier = Modifier.width(24.dp))
                CounterControl(
                    label = stringResource(R.string.timer_seconds),
                    value = state.countdownSeconds,
                    onIncrement = { viewModel.setCountdownSeconds(state.countdownSeconds + 1) },
                    onDecrement = { viewModel.setCountdownSeconds(state.countdownSeconds - 1) },
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(240.dp),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 10.dp.toPx()
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
                drawArc(
                    color = arcColor,
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                )
            }

            Text(
                text = timeText,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color =
                    if (state.isFinished) {
                        AccentTimer.copy(alpha = finishedPulse)
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    },
                letterSpacing = 2.sp,
            )
        }

        if (state.isFinished) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.timer_finished),
                style = MaterialTheme.typography.titleLarge,
                color = AccentTimer,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.startPause() },
            enabled = !state.isFinished,
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
                    when {
                        state.isRunning -> stringResource(R.string.pause)
                        state.elapsedMs > 0 || state.countdownRemainingMs < state.countdownTotalMs ->
                            stringResource(R.string.resume)
                        else -> stringResource(R.string.start)
                    },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        if (state.isRunning || state.elapsedMs > 0 || state.isFinished ||
            state.countdownRemainingMs < state.countdownTotalMs
        ) {
            TextButton(onClick = { viewModel.reset() }) {
                Text(
                    text = stringResource(R.string.reset),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun CounterControl(
    label: String,
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement) {
                Icon(
                    imageVector = Icons.Outlined.Remove,
                    contentDescription = null,
                    tint = AccentTimer,
                )
            }
            Text(
                text = "%02d".format(value),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            IconButton(onClick = onIncrement) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    tint = AccentTimer,
                )
            }
        }
    }
}
