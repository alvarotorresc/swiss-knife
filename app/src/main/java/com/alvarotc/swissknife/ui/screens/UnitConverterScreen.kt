package com.alvarotc.swissknife.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.alvarotc.swissknife.model.UnitCategory
import com.alvarotc.swissknife.ui.theme.AccentUnitConv
import com.alvarotc.swissknife.ui.theme.DarkOnSurfaceVariant
import com.alvarotc.swissknife.ui.theme.DarkOutline
import com.alvarotc.swissknife.ui.theme.DarkSurfaceVariant
import com.alvarotc.swissknife.viewmodel.UnitConverterViewModel
import java.util.Locale

@Composable
fun UnitConverterScreen(viewModel: UnitConverterViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val textFieldColors =
        OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = DarkOutline,
            focusedBorderColor = AccentUnitConv,
            unfocusedLabelColor = DarkOnSurfaceVariant,
            focusedLabelColor = AccentUnitConv,
            cursorColor = AccentUnitConv,
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
        // Category chips
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            UnitCategory.entries.forEach { category ->
                FilterChip(
                    selected = state.category == category,
                    onClick = { viewModel.setCategory(category) },
                    label = {
                        Text(
                            stringResource(
                                when (category) {
                                    UnitCategory.LENGTH -> R.string.category_length
                                    UnitCategory.WEIGHT -> R.string.category_weight
                                    UnitCategory.TEMPERATURE -> R.string.category_temperature
                                    UnitCategory.VOLUME -> R.string.category_volume
                                },
                            ),
                        )
                    },
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentUnitConv,
                            selectedLabelColor = Color.White,
                            containerColor = DarkSurfaceVariant,
                            labelColor = DarkOnSurfaceVariant,
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // From unit
        UnitDropdown(
            label = stringResource(R.string.from),
            selectedIndex = state.fromUnitIndex,
            units = state.category.units,
            onSelect = { viewModel.setFromUnit(it) },
            textFieldColors = textFieldColors,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Swap button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(onClick = { viewModel.swapUnits() }) {
                Icon(
                    imageVector = Icons.Filled.SwapVert,
                    contentDescription = stringResource(R.string.swap),
                    tint = AccentUnitConv,
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // To unit
        UnitDropdown(
            label = stringResource(R.string.to),
            selectedIndex = state.toUnitIndex,
            units = state.category.units,
            onSelect = { viewModel.setToUnit(it) },
            textFieldColors = textFieldColors,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input
        OutlinedTextField(
            value = state.inputValue,
            onValueChange = { viewModel.setInputValue(it) },
            label = { Text(stringResource(R.string.value)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = textFieldColors,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Result
        val result = viewModel.getConvertedValue()
        Surface(
            color = DarkSurfaceVariant,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.result),
                    color = DarkOnSurfaceVariant,
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text =
                        if (result != null) {
                            String.format(Locale.US, "%.4f", result)
                        } else {
                            "—"
                        },
                    color = AccentUnitConv,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun UnitDropdown(
    label: String,
    selectedIndex: Int,
    units: List<com.alvarotc.swissknife.model.Unit>,
    onSelect: (Int) -> kotlin.Unit,
    textFieldColors: androidx.compose.material3.TextFieldColors,
) {
    Column {
        Text(
            text = label,
            color = DarkOnSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            units.forEachIndexed { index, unit ->
                FilterChip(
                    selected = selectedIndex == index,
                    onClick = { onSelect(index) },
                    label = { Text(unit.symbol) },
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentUnitConv,
                            selectedLabelColor = Color.White,
                            containerColor = DarkSurfaceVariant,
                            labelColor = DarkOnSurfaceVariant,
                        ),
                )
            }
        }
    }
}
