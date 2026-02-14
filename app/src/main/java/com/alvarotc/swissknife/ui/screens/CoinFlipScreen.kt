package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.ui.theme.AccentCoin
import com.alvarotc.swissknife.ui.theme.AccentCoinContainer
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.viewmodel.CoinFlipViewModel
import com.alvarotc.swissknife.viewmodel.CoinSide

@Composable
fun CoinFlipScreen(viewModel: CoinFlipViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var flipTrigger by remember { mutableIntStateOf(0) }

    val rotation by animateFloatAsState(
        targetValue = if (state.isFlipping) flipTrigger * 360f else flipTrigger * 360f,
        animationSpec = tween(durationMillis = 600),
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
                    .size(160.dp)
                    .graphicsLayer { rotationX = rotation },
            shape = CircleShape,
            color = if (state.result == CoinSide.HEADS) AccentCoin else AccentCoinContainer,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text =
                        when (state.result) {
                            CoinSide.HEADS -> "H"
                            CoinSide.TAILS -> "T"
                            null -> "?"
                        },
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (state.result == CoinSide.HEADS) Color.Black else AccentCoin,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text =
                when (state.result) {
                    CoinSide.HEADS -> "Heads"
                    CoinSide.TAILS -> "Tails"
                    null -> "Tap to flip"
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
                StatItem("Flips", state.totalFlips.toString())
                StatItem("Heads", state.headsCount.toString())
                StatItem("Tails", state.tailsCount.toString())
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
                text = "Flip",
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
                Text("Reset", color = DarkOnSurfaceVariant)
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
