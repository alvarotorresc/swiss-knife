package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.components.DiceFace
import com.alvarotc.swissknife.ui.components.PolyDiceFace
import com.alvarotc.swissknife.ui.theme.AccentDice
import com.alvarotc.swissknife.ui.theme.AccentDiceContainer
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.DiceRollViewModel
import com.alvarotc.swissknife.viewmodel.DiceType

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
        // Dice type selector
        Text(
            text = stringResource(R.string.dice_type),
            color = DarkOnSurfaceVariant,
            fontSize = 14.sp,
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
                            containerColor = DarkSurfaceVariant,
                            labelColor = DarkOnSurfaceVariant,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dice count selector
        Text(
            text = stringResource(R.string.number_of_dice),
            color = DarkOnSurfaceVariant,
            fontSize = 14.sp,
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
                            containerColor = DarkSurfaceVariant,
                            labelColor = DarkOnSurfaceVariant,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Dice display
        if (state.results.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                val diceSize = if (state.diceCount <= 2) 100.dp else 80.dp
                state.results.forEach { value ->
                    if (state.diceType == DiceType.D6) {
                        DiceFace(
                            value = value,
                            modifier = Modifier.size(diceSize),
                        )
                    } else {
                        PolyDiceFace(
                            value = value,
                            diceType = state.diceType,
                            modifier = Modifier.size(diceSize),
                        )
                    }
                }
            }

            if (state.results.size > 1) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.total, state.results.sum()),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        } else {
            Text(
                text = stringResource(R.string.tap_to_roll),
                fontSize = 18.sp,
                color = DarkOnSurfaceVariant,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Roll button
        Button(
            onClick = { viewModel.roll() },
            enabled = !state.isRolling,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentDice),
        ) {
            Text(
                text = if (state.isRolling) stringResource(R.string.rolling) else stringResource(R.string.roll),
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
        }
    }
}
