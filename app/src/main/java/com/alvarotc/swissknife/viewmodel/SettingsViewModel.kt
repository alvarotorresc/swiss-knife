package com.alvarotc.swissknife.viewmodel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.alvarotc.swissknife.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val currentLanguage: String = "en",
)

class SettingsViewModel(
    application: Application,
) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.language.collect { lang ->
                _uiState.value = _uiState.value.copy(currentLanguage = lang)
            }
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repository.setLanguage(lang)
            val localeList = LocaleListCompat.forLanguageTags(lang)
            AppCompatDelegate.setApplicationLocales(localeList)
        }
    }
}
