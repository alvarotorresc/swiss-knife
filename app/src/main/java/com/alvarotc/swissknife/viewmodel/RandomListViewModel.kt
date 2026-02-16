package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class RandomListError {
    data object ItemAlreadyAdded : RandomListError()

    data object NeedMoreItems : RandomListError()
}

data class RandomListUiState(
    val itemInput: String = "",
    val items: List<String> = emptyList(),
    val result: String? = null,
    val error: RandomListError? = null,
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
        _uiState.update { it.copy(result = picked, error = null) }
    }

    fun reset() {
        _uiState.value = RandomListUiState()
    }
}
