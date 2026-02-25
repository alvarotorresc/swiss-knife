package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentRPS
import com.alvarotc.swissknife.ui.theme.AccentRPSContainer
import com.alvarotc.swissknife.viewmodel.RPSChoice
import com.alvarotc.swissknife.viewmodel.RPSMode
import com.alvarotc.swissknife.viewmodel.RPSResult
import com.alvarotc.swissknife.viewmodel.RockPaperScissorsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun RockPaperScissorsScreen(viewModel: RockPaperScissorsViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Mode selector chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RPSMode.entries.forEach { mode ->
                FilterChip(
                    selected = state.mode == mode,
                    onClick = { viewModel.setMode(mode) },
                    label = {
                        Text(
                            when (mode) {
                                RPSMode.CPU -> stringResource(R.string.vs_cpu)
                                RPSMode.LOCAL -> stringResource(R.string.vs_friend)
                            },
                        )
                    },
                    enabled = state.playerChoice == null && !state.isRevealing,
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentRPSContainer,
                            selectedLabelColor = AccentRPS,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Score bar
        ScoreBar(
            wins = state.score.wins,
            draws = state.score.draws,
            losses = state.score.losses,
            isLocal = state.mode == RPSMode.LOCAL,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (state.isWaitingForP2) {
            // Handoff screen
            HandoffScreen(onReady = { viewModel.confirmHandoff() })
        } else {
            // Center area: player vs opponent
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Player 1 choice — hidden in LOCAL mode until result
                val p1Emoji =
                    if (state.mode == RPSMode.LOCAL && state.result == null) {
                        "?"
                    } else {
                        state.playerChoice?.emoji ?: "?"
                    }
                ChoiceDisplay(
                    emoji = p1Emoji,
                    isShaking = state.mode == RPSMode.LOCAL && state.isRevealing,
                    modifier = Modifier.size(100.dp),
                )

                Text(
                    text = stringResource(R.string.vs),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                // Opponent choice — shakes during reveal
                ChoiceDisplay(
                    emoji =
                        if (state.isRevealing || state.opponentChoice == null) {
                            "?"
                        } else {
                            state.opponentChoice!!.emoji
                        },
                    isShaking = state.isRevealing,
                    modifier = Modifier.size(100.dp),
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Result text with bounce animation
            ResultText(result = state.result, mode = state.mode)

            Spacer(modifier = Modifier.weight(1f))

            // Play Again button after result
            val showPlayAgain = state.result != null
            Button(
                onClick = { viewModel.reset() },
                enabled = showPlayAgain,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .graphicsLayer { alpha = if (showPlayAgain) 1f else 0f },
                shape = RoundedCornerShape(12.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = AccentRPSContainer,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.play_again),
                    style = MaterialTheme.typography.labelLarge,
                    color = AccentRPS,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Prompt text when idle
            val showPrompt = state.playerChoice == null && !state.isRevealing
            Text(
                text =
                    if (state.mode == RPSMode.LOCAL) {
                        stringResource(R.string.player_1_turn)
                    } else {
                        stringResource(R.string.choose_your_move)
                    },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier =
                    Modifier
                        .padding(bottom = 12.dp)
                        .graphicsLayer { alpha = if (showPrompt) 1f else 0f },
            )

            // Choice buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                RPSChoice.entries.forEach { choice ->
                    ChoiceButton(
                        choice = choice,
                        enabled = !state.isRevealing && !state.isWaitingForP2 && state.result == null,
                        isSelected = state.mode == RPSMode.CPU && state.playerChoice == choice && state.result == null,
                        onClick = { viewModel.play(choice) },
                    )
                }
            }

            // Reset score button
            val showReset = state.score.wins + state.score.losses + state.score.draws > 0
            TextButton(
                onClick = { viewModel.reset() },
                enabled = showReset,
                modifier = Modifier.graphicsLayer { alpha = if (showReset) 1f else 0f },
            ) {
                Text(
                    text = stringResource(R.string.reset),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HandoffScreen(onReady: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.pass_phone),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onReady,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = AccentRPS,
                ),
        ) {
            Text(
                text = stringResource(R.string.ready),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.surface,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.player_2_turn),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ScoreBar(
    wins: Int,
    draws: Int,
    losses: Int,
    isLocal: Boolean,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ScoreItem(
                label = if (isLocal) stringResource(R.string.player_1) else "W",
                value = wins,
                color = AccentRPS,
            )
            ScoreItem(label = "D", value = draws, color = MaterialTheme.colorScheme.onSurfaceVariant)
            ScoreItem(
                label = if (isLocal) stringResource(R.string.player_2) else "L",
                value = losses,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ScoreItem(
    label: String,
    value: Int,
    color: androidx.compose.ui.graphics.Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = color,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ChoiceDisplay(
    emoji: String,
    isShaking: Boolean,
    modifier: Modifier = Modifier,
) {
    var shakeX by remember { mutableFloatStateOf(0f) }
    var shakeY by remember { mutableFloatStateOf(0f) }
    var shakeRot by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isShaking) {
        if (isShaking) {
            while (isActive) {
                shakeX = (Math.random().toFloat() - 0.5f) * 24f
                shakeY = (Math.random().toFloat() - 0.5f) * 16f
                shakeRot = (Math.random().toFloat() - 0.5f) * 20f
                delay(30L)
            }
        } else {
            shakeX = 0f
            shakeY = 0f
            shakeRot = 0f
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier.graphicsLayer {
                translationX = if (isShaking) shakeX else 0f
                translationY = if (isShaking) shakeY else 0f
                rotationZ = if (isShaking) shakeRot else 0f
            },
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ResultText(
    result: RPSResult?,
    mode: RPSMode,
) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(result) {
        if (result != null) {
            scale.snapTo(0f)
            scale.animateTo(
                targetValue = 1.15f,
                animationSpec = tween(durationMillis = 120),
            )
            scale.animateTo(
                targetValue = 1f,
                animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
            )
        } else {
            scale.snapTo(0f)
        }
    }

    Box(
        modifier =
            Modifier
                .height(48.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                },
        contentAlignment = Alignment.Center,
    ) {
        if (result != null) {
            val (text, color) =
                when {
                    result == RPSResult.DRAW ->
                        stringResource(R.string.draw_result) to MaterialTheme.colorScheme.onSurfaceVariant
                    mode == RPSMode.LOCAL && result == RPSResult.WIN ->
                        stringResource(R.string.player_1_wins) to AccentRPS
                    mode == RPSMode.LOCAL && result == RPSResult.LOSE ->
                        stringResource(R.string.player_2_wins) to AccentRPS
                    result == RPSResult.WIN ->
                        stringResource(R.string.you_win) to AccentRPS
                    else ->
                        stringResource(R.string.you_lose) to MaterialTheme.colorScheme.onSurfaceVariant
                }
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ChoiceButton(
    choice: RPSChoice,
    enabled: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = if (isSelected) AccentRPSContainer else MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
    ) {
        Text(
            text = choice.emoji,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
    }
}
