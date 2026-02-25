package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

sealed class GroupGeneratorError {
    data object NameAlreadyAdded : GroupGeneratorError()

    data object NeedMoreForGroups : GroupGeneratorError()
}

data class GroupGeneratorUiState(
    val nameInput: String = "",
    val participants: List<String> = emptyList(),
    val numGroups: Int = 2,
    val groups: List<List<String>> = emptyList(),
    val error: GroupGeneratorError? = null,
)

class GroupGeneratorViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GroupGeneratorUiState())
    val uiState: StateFlow<GroupGeneratorUiState> = _uiState.asStateFlow()

    fun setNameInput(name: String) {
        _uiState.update { it.copy(nameInput = name, error = null) }
    }

    fun addParticipant() {
        val name = _uiState.value.nameInput.trim()
        when {
            name.isBlank() -> return
            _uiState.value.participants.any { it.equals(name, ignoreCase = true) } -> {
                _uiState.update { it.copy(error = GroupGeneratorError.NameAlreadyAdded) }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        participants = it.participants + name,
                        nameInput = "",
                        error = null,
                        groups = emptyList(),
                    )
                }
            }
        }
    }

    fun removeParticipant(name: String) {
        _uiState.update {
            it.copy(
                participants = it.participants - name,
                groups = emptyList(),
            )
        }
    }

    fun setNumGroups(count: Int) {
        _uiState.update { it.copy(numGroups = count.coerceIn(2, 10), groups = emptyList()) }
    }

    fun generate() {
        val state = _uiState.value
        if (state.participants.size <= state.numGroups) {
            _uiState.update { it.copy(error = GroupGeneratorError.NeedMoreForGroups) }
            return
        }

        val shuffled = state.participants.shuffled()
        val groups = List(state.numGroups) { mutableListOf<String>() }
        shuffled.forEachIndexed { index, name ->
            groups[index % state.numGroups].add(name)
        }

        _uiState.update { it.copy(groups = groups, error = null) }
    }

    fun reset() {
        _uiState.update { it.copy(groups = emptyList()) }
    }
}
