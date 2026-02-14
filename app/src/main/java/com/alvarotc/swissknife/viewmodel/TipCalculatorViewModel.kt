package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TipCalculatorUiState(
    val billAmountText: String = "",
    val tipPercent: Float = 15f,
    val numPeople: Int = 1,
)

class TipCalculatorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TipCalculatorUiState())
    val uiState: StateFlow<TipCalculatorUiState> = _uiState.asStateFlow()

    fun setBillAmount(amount: String) {
        _uiState.update { it.copy(billAmountText = amount) }
    }

    fun setTipPercent(percent: Float) {
        _uiState.update { it.copy(tipPercent = percent) }
    }

    fun incrementPeople() {
        _uiState.update { it.copy(numPeople = (it.numPeople + 1).coerceAtMost(50)) }
    }

    fun decrementPeople() {
        _uiState.update { it.copy(numPeople = (it.numPeople - 1).coerceAtLeast(1)) }
    }

    fun getBillAmount(): Double {
        return _uiState.value.billAmountText.toDoubleOrNull() ?: 0.0
    }

    fun getTipAmount(): Double {
        val bill = getBillAmount()
        return bill * (_uiState.value.tipPercent / 100)
    }

    fun getTotalAmount(): Double {
        return getBillAmount() + getTipAmount()
    }

    fun getAmountPerPerson(): Double {
        return getTotalAmount() / _uiState.value.numPeople
    }
}
