package com.alvarotc.swissknife.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentRandom
import com.alvarotc.swissknife.viewmodel.RandomNumberError
import com.alvarotc.swissknife.viewmodel.RandomNumberViewModel

@Composable
fun RandomNumberScreen(viewModel: RandomNumberViewModel = viewModel()) {
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

    // Bounce scale when generation finishes
    val resultScale = remember { Animatable(1f) }

    LaunchedEffect(state.isGenerating) {
        if (!state.isGenerating && state.result != null) {
            resultScale.snapTo(1.3f)
            resultScale.animateTo(
                targetValue = 1f,
                animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
            )
        }
    }

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
                value = state.minText,
                onValueChange = { viewModel.setMin(it) },
                label = { Text(stringResource(R.string.min)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.width(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                enabled = !state.isGenerating,
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedTextField(
                value = state.maxText,
                onValueChange = { viewModel.setMax(it) },
                label = { Text(stringResource(R.string.max)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.width(140.dp),
                shape = RoundedCornerShape(12.dp),
                colors = textFieldColors,
                enabled = !state.isGenerating,
            )
        }

        if (state.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text =
                    when (state.error) {
                        RandomNumberError.InvalidNumbers -> stringResource(R.string.error_valid_numbers)
                        RandomNumberError.MinNotLessThanMax -> stringResource(R.string.error_min_less_than_max)
                        null -> ""
                    },
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Slot machine display
        val displayText = state.displayText
        val lockedDigits = state.lockedDigits

        if (displayText != null) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier =
                    Modifier.graphicsLayer {
                        if (!state.isGenerating && state.result != null) {
                            scaleX = resultScale.value
                            scaleY = resultScale.value
                        }
                    },
            ) {
                displayText.forEachIndexed { index, char ->
                    val isLocked = index < lockedDigits
                    Text(
                        text = char.toString(),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color =
                            if (isLocked || !state.isGenerating) {
                                AccentRandom
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            },
                        style =
                            MaterialTheme.typography.displayLarge.copy(
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                }
            }
        } else {
            Text(
                text = "\u2014",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { viewModel.generate() },
            enabled = !state.isGenerating,
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
                text = stringResource(R.string.generate),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}
