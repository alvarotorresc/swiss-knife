package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentLevel
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.viewmodel.LevelViewModel
import kotlin.math.min

@Composable
fun LevelScreen(viewModel: LevelViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> viewModel.startListening()
                    Lifecycle.Event.ON_PAUSE -> viewModel.stopListening()
                    else -> {}
                }
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.stopListening()
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!state.sensorAvailable) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.sensor_not_available),
                    color = DarkOnSurfaceVariant,
                    fontSize = 16.sp,
                )
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))

            // Status text
            Text(
                text =
                    if (state.isLevel) {
                        stringResource(R.string.level_perfect)
                    } else {
                        stringResource(R.string.level_adjust)
                    },
                color = if (state.isLevel) AccentLevel else Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Bubble level visualization
            Canvas(modifier = Modifier.size(300.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val radius = min(canvasWidth, canvasHeight) / 2

                // Outer circle
                drawCircle(
                    color = if (state.isLevel) AccentLevel else Color.Gray,
                    radius = radius,
                    style = Stroke(width = 4.dp.toPx()),
                )

                // Center crosshair
                val crosshairSize = 20.dp.toPx()
                drawLine(
                    color = Color.Gray,
                    start = Offset(canvasWidth / 2 - crosshairSize, canvasHeight / 2),
                    end = Offset(canvasWidth / 2 + crosshairSize, canvasHeight / 2),
                    strokeWidth = 2.dp.toPx(),
                )
                drawLine(
                    color = Color.Gray,
                    start = Offset(canvasWidth / 2, canvasHeight / 2 - crosshairSize),
                    end = Offset(canvasWidth / 2, canvasHeight / 2 + crosshairSize),
                    strokeWidth = 2.dp.toPx(),
                )

                // Bubble (affected by tilt)
                val maxOffset = radius * 0.7f
                val bubbleX = canvasWidth / 2 + (state.roll * maxOffset / 10f)
                val bubbleY = canvasHeight / 2 + (state.pitch * maxOffset / 10f)
                val bubbleRadius = 40.dp.toPx()

                drawCircle(
                    color = if (state.isLevel) AccentLevel else Color.White,
                    radius = bubbleRadius,
                    center = Offset(bubbleX, bubbleY),
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
