package com.example.mascota.viewmodel.walker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WalkerHomeUiState(
    val isAvailable: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

class WalkerHomeViewModel(
    private val repo: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalkerHomeUiState())
    val uiState: StateFlow<WalkerHomeUiState> = _uiState.asStateFlow()

    fun setAvailability(newValue: Boolean) {
        // Optimista: cambia el switch y si falla, revierte
        val prev = _uiState.value.isAvailable
        _uiState.value = _uiState.value.copy(isAvailable = newValue, isLoading = true, error = null)

        viewModelScope.launch {
            try {
                val res = repo.setWalkerAvailability(newValue)
                val serverValue = (res.isAvailable == 1)
                _uiState.value = _uiState.value.copy(
                    isAvailable = serverValue,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAvailable = prev,
                    isLoading = false,
                    error = e.message ?: "Error al cambiar disponibilidad"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
