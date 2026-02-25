package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.alvarotc.swissknife.ui.theme.AccentCoin
import com.alvarotc.swissknife.ui.theme.AccentCoinContainer
import com.alvarotc.swissknife.viewmodel.CoinFlipViewModel
import com.alvarotc.swissknife.viewmodel.CoinSide

@Composable
fun CoinFlipScreen(viewModel: CoinFlipViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var flipTrigger by remember { mutableIntStateOf(0) }

    val rotation by animateFloatAsState(
        targetValue = flipTrigger * 720f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        finishedListener = { viewModel.onAnimationFinished() },
        label = "coinFlip",
    )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Surface(
            modifier =
                Modifier
                    .size(180.dp)
                    .graphicsLayer {
                        rotationX = rotation
                        cameraDistance = 14f * density
                    },
            shape = CircleShape,
            color =
                when (state.result) {
                    CoinSide.HEADS -> AccentCoin
                    CoinSide.TAILS -> AccentCoinContainer
                    null -> MaterialTheme.colorScheme.surfaceVariant
                },
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .border(
                            width = 3.dp,
                            color =
                                when (state.result) {
                                    CoinSide.HEADS -> AccentCoin.copy(alpha = 0.6f)
                                    CoinSide.TAILS -> AccentCoin.copy(alpha = 0.3f)
                                    null -> MaterialTheme.colorScheme.outline
                                },
                            shape = CircleShape,
                        ),
            ) {
                when (state.result) {
                    CoinSide.HEADS -> {
                        Icon(
                            imageVector = Icons.Outlined.Construction,
                            contentDescription = stringResource(R.string.heads),
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(80.dp),
                        )
                    }
                    CoinSide.TAILS -> {
                        Text(
                            text = "Swiss\nKnife",
                            color = AccentCoin,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                        )
                    }
                    null -> {
                        Text(
                            text = "?",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text =
                when (state.result) {
                    CoinSide.HEADS -> stringResource(R.string.heads)
                    CoinSide.TAILS -> stringResource(R.string.tails)
                    null -> stringResource(R.string.tap_to_flip)
                },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (state.totalFlips > 0) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatItem(stringResource(R.string.flips), state.totalFlips.toString())
                StatItem(stringResource(R.string.heads), state.headsCount.toString())
                StatItem(stringResource(R.string.tails), state.tailsCount.toString())
            }
        }

        Button(
            onClick = {
                flipTrigger++
                viewModel.flip()
            },
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
                text = stringResource(R.string.flip),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }

        if (state.totalFlips > 0) {
            TextButton(onClick = {
                flipTrigger = 0
                viewModel.reset()
            }) {
                Text(
                    text = stringResource(R.string.reset),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
