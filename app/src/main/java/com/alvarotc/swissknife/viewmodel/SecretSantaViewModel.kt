package com.alvarotc.swissknife.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Assignment(val giver: String, val receiver: String)

sealed class SecretSantaError {
    data object NameAlreadyAdded : SecretSantaError()
    data object NeedMoreParticipants : SecretSantaError()
}

data class SecretSantaUiState(
    val nameInput: String = "",
    val participants: List<String> = emptyList(),
    val assignments: List<Assignment> = emptyList(),
    val revealedCount: Int = 0,
    val error: SecretSantaError? = null,
)

class SecretSantaViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SecretSantaUiState())
    val uiState: StateFlow<SecretSantaUiState> = _uiState.asStateFlow()

    fun setNameInput(name: String) {
        _uiState.update { it.copy(nameInput = name, error = null) }
    }

    fun addParticipant() {
        val name = _uiState.value.nameInput.trim()
        when {
            name.isBlank() -> return
            _uiState.value.participants.any { it.equals(name, ignoreCase = true) } -> {
                _uiState.update { it.copy(error = SecretSantaError.NameAlreadyAdded) }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        participants = it.participants + name,
                        nameInput = "",
                        assignments = emptyList(),
                        revealedCount = 0,
                        error = null,
                    )
                }
            }
        }
    }

    fun removeParticipant(name: String) {
        _uiState.update {
            it.copy(
                participants = it.participants - name,
                assignments = emptyList(),
                revealedCount = 0,
            )
        }
    }

    fun draw() {
        val participants = _uiState.value.participants
        if (participants.size < 3) {
            _uiState.update { it.copy(error = SecretSantaError.NeedMoreParticipants) }
            return
        }

        val shuffled = derangement(participants)
        val assignments =
            participants.zip(shuffled).map { (giver, receiver) ->
                Assignment(giver, receiver)
            }
        _uiState.update { it.copy(assignments = assignments, revealedCount = 0, error = null) }
    }

    fun revealNext() {
        _uiState.update {
            if (it.revealedCount < it.assignments.size) {
                it.copy(revealedCount = it.revealedCount + 1)
            } else {
                it
            }
        }
    }

    fun reset() {
        _uiState.value = SecretSantaUiState()
    }

    fun buildShareText(): String {
        val assignments = _uiState.value.assignments
        return assignments.joinToString("\n") { "${it.giver} → ${it.receiver}" }
    }

    companion object {
        fun derangement(items: List<String>): List<String> {
            require(items.size >= 2) { "Need at least 2 items" }
            var shuffled: List<String>
            do {
                shuffled = items.shuffled()
            } while (shuffled.zip(items).any { (a, b) -> a == b })
            return shuffled
        }
    }
}
