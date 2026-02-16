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
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentCoin
import com.alvarotc.swissknife.ui.theme.AccentCoinContainer
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkOutline
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
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

        // Coin
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
                    null -> DarkSurfaceVariant
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
                                    null -> DarkOutline
                                },
                            shape = CircleShape,
                        ),
            ) {
                when (state.result) {
                    CoinSide.HEADS -> {
                        Icon(
                            imageVector = Icons.Filled.Construction,
                            contentDescription = stringResource(R.string.heads),
                            tint = Color.Black,
                            modifier = Modifier.size(80.dp),
                        )
                    }
                    CoinSide.TAILS -> {
                        Text(
                            text = "Swiss\nKnife",
                            color = AccentCoin,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 28.sp,
                        )
                    }
                    null -> {
                        Text(
                            text = "?",
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkOnSurfaceVariant,
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
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Stats
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

        // Buttons
        Button(
            onClick = {
                flipTrigger++
                viewModel.flip()
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentCoin),
        ) {
            Text(
                text = stringResource(R.string.flip),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
            )
        }

        if (state.totalFlips > 0) {
            TextButton(onClick = {
                flipTrigger = 0
                viewModel.reset()
            }) {
                Text(stringResource(R.string.reset), color = DarkOnSurfaceVariant)
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
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = DarkOnSurfaceVariant,
        )
    }
}
