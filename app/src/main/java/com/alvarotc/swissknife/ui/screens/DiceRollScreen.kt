package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.components.DiceFace
import com.alvarotc.swissknife.ui.components.PolyDiceFace
import com.alvarotc.swissknife.ui.theme.AccentDice
import com.alvarotc.swissknife.ui.theme.AccentDiceContainer
import com.alvarotc.swissknife.viewmodel.DiceAnimPhase
import com.alvarotc.swissknife.viewmodel.DiceRollViewModel
import com.alvarotc.swissknife.viewmodel.DiceType
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiceRollScreen(viewModel: DiceRollViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.dice_type),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxWidth(),
        ) {
            DiceType.entries.forEach { type ->
                FilterChip(
                    selected = state.diceType == type,
                    onClick = { viewModel.setDiceType(type) },
                    label = { Text(type.label) },
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentDiceContainer,
                            selectedLabelColor = AccentDice,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.number_of_dice),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (1..4).forEach { count ->
                FilterChip(
                    selected = state.diceCount == count,
                    onClick = { viewModel.setDiceCount(count) },
                    label = { Text("$count") },
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentDiceContainer,
                            selectedLabelColor = AccentDice,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (state.results.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                val diceSize = if (state.diceCount <= 2) 100.dp else 80.dp
                state.results.forEachIndexed { index, value ->
                    AnimatedDie(
                        value = value,
                        diceType = state.diceType,
                        dieIndex = index,
                        animPhase = state.animPhase,
                        modifier = Modifier.size(diceSize),
                    )
                }
            }

            if (state.results.size > 1 && state.animPhase == DiceAnimPhase.IDLE) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.total, state.results.sum()),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        } else {
            Text(
                text = stringResource(R.string.tap_to_roll),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.roll() },
            enabled = !state.isRolling,
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
                text = if (state.isRolling) stringResource(R.string.rolling) else stringResource(R.string.roll),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
private fun AnimatedDie(
    value: Int,
    diceType: DiceType,
    dieIndex: Int,
    animPhase: DiceAnimPhase,
    modifier: Modifier = Modifier,
) {
    // Per-die independent shake offsets
    var shakeX by remember { mutableFloatStateOf(0f) }
    var shakeY by remember { mutableFloatStateOf(0f) }
    var shakeRot by remember { mutableFloatStateOf(0f) }

    @Suppress("UNUSED_VARIABLE")
    var shakeTick by remember { mutableIntStateOf(0) }

    // Per-die landing animation with stagger
    val landScale = remember { Animatable(1f) }
    val landOffsetY = remember { Animatable(0f) }

    // Shake phase — each die has its own random jitter
    LaunchedEffect(animPhase) {
        if (animPhase == DiceAnimPhase.SHAKING) {
            while (isActive) {
                shakeX = (Math.random().toFloat() - 0.5f) * 30f
                shakeY = (Math.random().toFloat() - 0.5f) * 20f
                shakeRot = (Math.random().toFloat() - 0.5f) * 30f
                shakeTick++
                delay(25L + (dieIndex * 5L)) // Slightly offset timing per die
            }
        } else {
            shakeX = 0f
            shakeY = 0f
            shakeRot = 0f
        }
    }

    // Throw phase — each die rises independently
    LaunchedEffect(animPhase) {
        when (animPhase) {
            DiceAnimPhase.THROWING -> {
                landScale.snapTo(0.7f)
                landOffsetY.animateTo(
                    targetValue = -50f - (dieIndex * 15f),
                    animationSpec = tween(durationMillis = 250),
                )
            }
            DiceAnimPhase.LANDING -> {
                // Staggered landing — each die lands with different delay
                delay(dieIndex * 120L)
                launch {
                    landScale.animateTo(
                        targetValue = 1.12f,
                        animationSpec = tween(80),
                    )
                    landScale.animateTo(
                        targetValue = 1f,
                        animationSpec =
                            spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium,
                            ),
                    )
                }
                landOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec =
                        spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow,
                        ),
                )
            }
            DiceAnimPhase.IDLE -> {
                landScale.snapTo(1f)
                landOffsetY.snapTo(0f)
            }
            else -> {}
        }
    }

    Box(
        modifier =
            modifier.graphicsLayer {
                translationX = if (animPhase == DiceAnimPhase.SHAKING) shakeX else 0f
                translationY =
                    if (animPhase == DiceAnimPhase.SHAKING) {
                        shakeY
                    } else {
                        landOffsetY.value
                    }
                rotationZ = if (animPhase == DiceAnimPhase.SHAKING) shakeRot else 0f
                scaleX = if (animPhase == DiceAnimPhase.SHAKING) 1f else landScale.value
                scaleY = if (animPhase == DiceAnimPhase.SHAKING) 1f else landScale.value
            },
    ) {
        if (diceType == DiceType.D6) {
            DiceFace(
                value = value,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            PolyDiceFace(
                value = value,
                diceType = diceType,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
