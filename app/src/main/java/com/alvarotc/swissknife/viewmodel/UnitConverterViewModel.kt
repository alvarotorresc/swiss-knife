package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import com.alvarotc.swissknife.model.UnitCategory
import com.alvarotc.swissknife.model.convertUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UnitConverterUiState(
    val category: UnitCategory = UnitCategory.LENGTH,
    val inputValue: String = "",
    val fromUnitIndex: Int = 0,
    val toUnitIndex: Int = 1,
)

class UnitConverterViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UnitConverterUiState())
    val uiState: StateFlow<UnitConverterUiState> = _uiState.asStateFlow()

    fun setCategory(category: UnitCategory) {
        _uiState.update {
            it.copy(
                category = category,
                fromUnitIndex = 0,
                toUnitIndex = 1,
            )
        }
    }

    fun setInputValue(value: String) {
        _uiState.update { it.copy(inputValue = value) }
    }

    fun setFromUnit(index: Int) {
        _uiState.update { it.copy(fromUnitIndex = index) }
    }

    fun setToUnit(index: Int) {
        _uiState.update { it.copy(toUnitIndex = index) }
    }

    fun swapUnits() {
        _uiState.update {
            it.copy(
                fromUnitIndex = it.toUnitIndex,
                toUnitIndex = it.fromUnitIndex,
            )
        }
    }

    fun getConvertedValue(): Double? {
        val value = _uiState.value.inputValue.toDoubleOrNull() ?: return null
        val state = _uiState.value
        val fromUnit = state.category.units[state.fromUnitIndex]
        val toUnit = state.category.units[state.toUnitIndex]
        return convertUnit(value, fromUnit, toUnit, state.category)
    }
}
