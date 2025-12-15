package com.example.mascota.viewmodel.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.WalkerDto
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WalkerDetailUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val walker: WalkerDto? = null
)

class WalkerDetailViewModel(
    private val repo: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalkerDetailUiState())
    val uiState: StateFlow<WalkerDetailUiState> = _uiState.asStateFlow()

    fun load(id: Long) {
        viewModelScope.launch {
            _uiState.value = WalkerDetailUiState(loading = true)
            try {
                val w = repo.getWalkerById(id)
                _uiState.value = WalkerDetailUiState(walker = w)
            } catch (e: Exception) {
                _uiState.value = WalkerDetailUiState(error = e.message ?: "Error cargando paseador")
            }
        }
    }
}
