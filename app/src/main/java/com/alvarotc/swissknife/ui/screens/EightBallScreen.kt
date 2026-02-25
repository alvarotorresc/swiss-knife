package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentEightBall
import com.alvarotc.swissknife.ui.theme.AccentEightBallContainer
import com.alvarotc.swissknife.viewmodel.EightBallViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

// Indices: 0–8 positive, 9–12 neutral, 13–19 negative
private fun answerColor(
    answer: String,
    answers: List<String>,
): Color {
    val index = answers.indexOf(answer)
    return when {
        index in 0..8 -> Color(0xFF4ADE80) // green — positive
        index in 9..12 -> Color(0xFFFBBF24) // yellow — neutral
        else -> Color(0xFFF87171) // red — negative
    }
}

private val eightBallAnswers =
    listOf(
        "It is certain",
        "Without a doubt",
        "Yes definitely",
        "You may rely on it",
        "As I see it yes",
        "Most likely",
        "Outlook good",
        "Yes",
        "Signs point to yes",
        "Reply hazy try again",
        "Ask again later",
        "Better not tell you now",
        "Cannot predict now",
        "Concentrate and ask again",
        "Don't count on it",
        "My reply is no",
        "My sources say no",
        "Outlook not so good",
        "Very doubtful",
        "No way",
    )

@Composable
fun EightBallScreen(viewModel: EightBallViewModel = viewModel()) {
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

    // Shake offsets for the ball
    var shakeX by remember { mutableFloatStateOf(0f) }
    var shakeY by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(state.isShaking) {
        if (state.isShaking) {
            while (isActive) {
                shakeX = (Math.random().toFloat() - 0.5f) * 24f
                shakeY = (Math.random().toFloat() - 0.5f) * 16f
                delay(30L)
            }
        } else {
            shakeX = 0f
            shakeY = 0f
        }
    }

    // Answer reveal animation
    val answerScale = remember { Animatable(0f) }

    LaunchedEffect(state.answer) {
        if (state.answer != null) {
            answerScale.snapTo(0f)
            answerScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400),
            )
        } else {
            answerScale.snapTo(0f)
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            value = state.question,
            onValueChange = { viewModel.setQuestion(it) },
            label = { Text(stringResource(R.string.ask_a_question)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            enabled = !state.isShaking,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { viewModel.ask() }),
        )

        Spacer(modifier = Modifier.weight(1f))

        // 8-Ball
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .size(250.dp)
                    .graphicsLayer {
                        translationX = shakeX
                        translationY = shakeY
                    },
        ) {
            // Outer black ball
            Surface(
                modifier = Modifier.size(250.dp),
                shape = CircleShape,
                color = Color.Black,
            ) {}

            // Inner window (dark blue circle)
            Surface(
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                color = AccentEightBallContainer,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when {
                        state.isShaking -> {
                            Text(
                                text = "...",
                                style = MaterialTheme.typography.headlineMedium,
                                color = AccentEightBall,
                                textAlign = TextAlign.Center,
                            )
                        }
                        state.answer != null -> {
                            val answer = state.answer!!
                            Text(
                                text = answer,
                                style = MaterialTheme.typography.bodyMedium,
                                color = answerColor(answer, eightBallAnswers),
                                textAlign = TextAlign.Center,
                                modifier =
                                    Modifier
                                        .padding(12.dp)
                                        .graphicsLayer {
                                            scaleX = answerScale.value
                                            scaleY = answerScale.value
                                            alpha = answerScale.value
                                        },
                            )
                        }
                        else -> {
                            Text(
                                text = "8",
                                style = MaterialTheme.typography.displayMedium,
                                color = AccentEightBall,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text =
                if (state.isShaking) {
                    stringResource(R.string.shake_to_answer)
                } else {
                    stringResource(R.string.ask_a_question)
                },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                if (state.answer != null) {
                    viewModel.reset()
                } else {
                    viewModel.ask()
                }
            },
            enabled = !state.isShaking && state.question.isNotBlank(),
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
                    if (state.answer != null) {
                        stringResource(R.string.ask) + " Again"
                    } else {
                        stringResource(R.string.ask)
                    },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
