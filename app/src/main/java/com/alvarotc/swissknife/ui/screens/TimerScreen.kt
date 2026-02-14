package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentTimer
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkOutline
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.TimerMode
import com.alvarotc.swissknife.viewmodel.TimerViewModel

@Composable
fun TimerScreen(viewModel: TimerViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = DarkOutline,
            focusedBorderColor = AccentTimer,
            unfocusedLabelColor = DarkOnSurfaceVariant,
            focusedLabelColor = AccentTimer,
            cursorColor = AccentTimer,
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
        // Mode selector
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = state.mode == TimerMode.STOPWATCH,
                onClick = { viewModel.setMode(TimerMode.STOPWATCH) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                colors =
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = AccentTimer,
                        activeContentColor = Color.White,
                        inactiveContainerColor = DarkSurfaceVariant,
                        inactiveContentColor = DarkOnSurfaceVariant,
                    ),
            ) {
                Text(stringResource(R.string.stopwatch))
            }
            SegmentedButton(
                selected = state.mode == TimerMode.COUNTDOWN,
                onClick = { viewModel.setMode(TimerMode.COUNTDOWN) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                colors =
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = AccentTimer,
                        activeContentColor = Color.White,
                        inactiveContainerColor = DarkSurfaceVariant,
                        inactiveContentColor = DarkOnSurfaceVariant,
                    ),
            ) {
                Text(stringResource(R.string.countdown))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Target time input (countdown only)
        if (state.mode == TimerMode.COUNTDOWN && !state.isRunning && state.elapsedMillis == 0L) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                OutlinedTextField(
                    value = state.targetMinutes.toString(),
                    onValueChange = { viewModel.setTargetMinutes(it.toIntOrNull() ?: 0) },
                    label = { Text(stringResource(R.string.minutes_short)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.width(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = state.targetSeconds.toString(),
                    onValueChange = { viewModel.setTargetSeconds(it.toIntOrNull() ?: 0) },
                    label = { Text(stringResource(R.string.seconds_short)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.width(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Time display
        val displayMillis =
            if (state.mode == TimerMode.STOPWATCH) {
                state.elapsedMillis
            } else {
                viewModel.getRemainingMillis()
            }

        Text(
            text = viewModel.formatTime(displayMillis),
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontFamily = FontFamily.Monospace,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Controls
        Button(
            onClick = { viewModel.startPause() },
            enabled =
                if (state.mode == TimerMode.COUNTDOWN) {
                    state.targetMinutes > 0 || state.targetSeconds > 0
                } else {
                    true
                },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentTimer),
        ) {
            Text(
                text = if (state.isRunning) stringResource(R.string.pause) else stringResource(R.string.start),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }

        if (state.elapsedMillis > 0) {
            TextButton(onClick = { viewModel.reset() }) {
                Text(stringResource(R.string.reset), color = DarkOnSurfaceVariant)
            }
        }
    }
}
