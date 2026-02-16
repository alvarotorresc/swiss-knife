package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alvarotc.swissknife.R
import com.alvarotc.swissknife.ui.theme.AccentTipCalc
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkOutline
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.TipCalculatorViewModel
import java.util.Locale

@Composable
fun TipCalculatorScreen(viewModel: TipCalculatorViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = DarkOutline,
            focusedBorderColor = AccentTipCalc,
            unfocusedLabelColor = DarkOnSurfaceVariant,
            focusedLabelColor = AccentTipCalc,
            cursorColor = AccentTipCalc,
            unfocusedTextColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedContainerColor = DarkSurfaceVariant,
            focusedContainerColor = DarkSurfaceVariant,
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
    ) {
        // Bill amount input
        OutlinedTextField(
            value = state.billAmountText,
            onValueChange = { viewModel.setBillAmount(it) },
            label = { Text(stringResource(R.string.bill_amount)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
            prefix = { Text("$") },
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tip percentage slider
        Text(
            text = stringResource(R.string.tip_percent, state.tipPercent.toInt()),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Slider(
            value = state.tipPercent,
            onValueChange = { viewModel.setTipPercent(it) },
            valueRange = 0f..30f,
            steps = 29,
            colors =
                SliderDefaults.colors(
                    thumbColor = AccentTipCalc,
                    activeTrackColor = AccentTipCalc,
                    inactiveTrackColor = DarkSurfaceVariant,
                ),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Number of people
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.split_between),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.decrementPeople() }) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = stringResource(R.string.decrement),
                        tint = AccentTipCalc,
                    )
                }
                Text(
                    text = stringResource(R.string.people_count, state.numPeople),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                IconButton(onClick = { viewModel.incrementPeople() }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.increment),
                        tint = AccentTipCalc,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Results
        Surface(
            color = DarkSurfaceVariant,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                ResultRow(stringResource(R.string.tip_amount), viewModel.getTipAmount())
                Spacer(modifier = Modifier.height(8.dp))
                ResultRow(stringResource(R.string.total_amount), viewModel.getTotalAmount())
                if (state.numPeople > 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                    ResultRow(
                        stringResource(R.string.per_person),
                        viewModel.getAmountPerPerson(),
                        highlight = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    amount: Double,
    highlight: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = if (highlight) Color.White else DarkOnSurfaceVariant,
            fontSize = if (highlight) 18.sp else 16.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
        )
        Text(
            text = String.format(Locale.US, "$%.2f", amount),
            color = if (highlight) AccentTipCalc else Color.White,
            fontSize = if (highlight) 24.sp else 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
