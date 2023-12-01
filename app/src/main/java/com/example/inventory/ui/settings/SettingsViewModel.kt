package com.example.inventory.ui.settings

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.inventory.data.Settings
import com.example.inventory.ui.item.ItemUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel: ViewModel() {
    fun initUiState() {
        _uiState.value = SettingsUiState(
            supName = Settings.defaultSupName,
            email = Settings.defaultEmail,
            phone = Settings.defaultPhone,
            enableDefaultFields = Settings.enableDefaultFields,
            hideSensitiveData = Settings.hideSensitiveData,
            disableSharing = Settings.disableSharing,
        )
    }

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) {
        _uiState.value = _uiState.value.copy(supName = value)
    }

    fun onPhoneChange(value: String) {
        _uiState.value = _uiState.value.copy(phone = value)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onEnableDefaultSettingsChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(enableDefaultFields = value)
    }

    fun onHideSensitiveDataChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(hideSensitiveData = value)
    }

    fun onDisableSharingChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(disableSharing = value)
    }



    fun save() {
        Settings.defaultSupName = uiState.value.supName
        Settings.defaultPhone = uiState.value.phone
        Settings.defaultEmail = uiState.value.email
        Settings.enableDefaultFields = uiState.value.enableDefaultFields
        Settings.hideSensitiveData = uiState.value.hideSensitiveData
        Settings.disableSharing = uiState.value.disableSharing
    }
}

data class SettingsUiState(
    val supName: String = "",
    val email: String = "",
    val phone: String = "",
    val enableDefaultFields: Boolean = false,
    val hideSensitiveData: Boolean = false,
    val disableSharing: Boolean = false,
)