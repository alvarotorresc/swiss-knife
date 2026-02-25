package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class RandomListError {
    data object ItemAlreadyAdded : RandomListError()

    data object NeedMoreItems : RandomListError()
}

data class RandomListUiState(
    val itemInput: String = "",
    val items: List<String> = emptyList(),
    val result: String? = null,
    val error: RandomListError? = null,
    val isPicking: Boolean = false,
    val highlightedIndex: Int = -1,
)

class RandomListViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RandomListUiState())
    val uiState: StateFlow<RandomListUiState> = _uiState.asStateFlow()

    fun setItemInput(item: String) {
        _uiState.update { it.copy(itemInput = item, error = null) }
    }

    fun addItem() {
        val item = _uiState.value.itemInput.trim()
        when {
            item.isBlank() -> return
            _uiState.value.items.any { it.equals(item, ignoreCase = true) } -> {
                _uiState.update { it.copy(error = RandomListError.ItemAlreadyAdded) }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        items = it.items + item,
                        itemInput = "",
                        result = null,
                        error = null,
                    )
                }
            }
        }
    }

    fun removeItem(item: String) {
        _uiState.update {
            it.copy(
                items = it.items - item,
                result = null,
            )
        }
    }

    fun pick() {
        val items = _uiState.value.items
        if (items.size < 2) {
            _uiState.update { it.copy(error = RandomListError.NeedMoreItems) }
            return
        }

        val picked = items.random()
        val pickedIndex = items.indexOf(picked)

        _uiState.update { it.copy(isPicking = true, error = null) }

        viewModelScope.launch {
            // Fast cycling through items
            var currentIndex = 0
            val totalCycles = items.size * 3 + pickedIndex
            var delayMs = 50L

            for (i in 0 until totalCycles) {
                currentIndex = i % items.size
                _uiState.update { it.copy(highlightedIndex = currentIndex) }
                delay(delayMs)
                // Decelerate in the last cycle
                if (i > totalCycles - items.size) {
                    delayMs += 30L
                }
            }

            // Land on the picked item
            _uiState.update {
                it.copy(
                    highlightedIndex = pickedIndex,
                    result = picked,
                    isPicking = false,
                )
            }
        }
    }

    fun reset() {
        _uiState.value = RandomListUiState()
    }
}
