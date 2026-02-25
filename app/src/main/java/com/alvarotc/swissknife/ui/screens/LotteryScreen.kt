package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentLottery
import com.alvarotc.swissknife.ui.theme.AccentLotteryContainer
import com.alvarotc.swissknife.viewmodel.LotteryError
import com.alvarotc.swissknife.viewmodel.LotteryViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val DRUM_SIZE_DP = 180
private const val DRUM_CYCLING_COUNT = 4
private const val DRUM_CYCLE_DELAY_MS = 50L
private const val DRUM_NUMBER_FONT_SIZE = 22
private const val DRUM_ROTATION_DEGREES = 5f

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LotteryScreen(viewModel: LotteryViewModel = viewModel()) {
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
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            OutlinedTextField(
                value = state.maxText,
                onValueChange = { viewModel.setMaxNumber(it) },
                label = { Text(stringResource(R.string.range_max)) },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                singleLine = true,
                modifier = Modifier.width(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                enabled = !state.isDrawing,
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = state.countText,
                onValueChange = { viewModel.setCount(it) },
                label = { Text(stringResource(R.string.numbers_to_draw)) },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                singleLine = true,
                modifier = Modifier.width(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                enabled = !state.isDrawing,
            )
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text =
                    when (state.error) {
                        LotteryError.CountExceedsRange ->
                            stringResource(R.string.error_count_exceeds_range)
                        LotteryError.InvalidNumbers ->
                            stringResource(R.string.error_valid_numbers)
                        null -> ""
                    },
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (state.results.isEmpty() && !state.isDrawing) {
            Text(
                text = stringResource(R.string.draw_numbers),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else if (state.results.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                state.results.forEachIndexed { index, number ->
                    if (index < state.revealedCount) {
                        LotteryBall(
                            number = number,
                            revealIndex = index,
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state.isDrawing,
            enter =
                fadeIn(animationSpec = tween(durationMillis = 300)) +
                    scaleIn(initialScale = 0.8f, animationSpec = tween(durationMillis = 300)),
            exit =
                fadeOut(animationSpec = tween(durationMillis = 400)) +
                    scaleOut(targetScale = 0.8f, animationSpec = tween(durationMillis = 400)),
        ) {
            LotteryDrum(maxNumber = state.maxNumber)
        }

        Spacer(modifier = Modifier.weight(1f))

        if (state.results.isNotEmpty() && !state.isDrawing) {
            Button(
                onClick = { viewModel.draw() },
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
                    text = stringResource(R.string.new_draw),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { viewModel.reset() }) {
                Text(
                    text = stringResource(R.string.reset),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Button(
                onClick = { viewModel.draw() },
                enabled = !state.isDrawing,
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
                    text = stringResource(R.string.draw_numbers),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Composable
private fun LotteryDrum(maxNumber: Int) {
    var cyclingNumbers by remember { mutableStateOf(generateCyclingNumbers(maxNumber)) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(DRUM_CYCLE_DELAY_MS)
            cyclingNumbers = generateCyclingNumbers(maxNumber)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "drumRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = -DRUM_ROTATION_DEGREES,
        targetValue = DRUM_ROTATION_DEGREES,
        animationSpec =
            infiniteRepeatable(
                animation = tween(durationMillis = 200, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "drumRotationAngle",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .padding(vertical = 24.dp)
                .size(DRUM_SIZE_DP.dp)
                .graphicsLayer { rotationZ = rotation }
                .clip(CircleShape)
                .background(color = AccentLotteryContainer),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            cyclingNumbers.forEach { number ->
                Text(
                    text = number.toString(),
                    fontSize = DRUM_NUMBER_FONT_SIZE.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentLottery,
                )
            }
        }
    }
}

private fun generateCyclingNumbers(maxNumber: Int): List<Int> {
    val bound = maxOf(maxNumber, 1)
    return List(DRUM_CYCLING_COUNT) { Random.nextInt(1, bound + 1) }
}

@Composable
private fun LotteryBall(
    number: Int,
    revealIndex: Int,
) {
    val scale = remember(revealIndex) { Animatable(0f) }

    LaunchedEffect(revealIndex) {
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessHigh,
                ),
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            Modifier
                .size(56.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                }
                .background(color = AccentLotteryContainer, shape = CircleShape),
    ) {
        Text(
            text = number.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AccentLottery,
        )
    }
}
