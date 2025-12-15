package com.example.mascota.viewmodel.walker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.WalkDto
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class PendingWalksUiState(
    val isLoading: Boolean = false,
    val items: List<WalkDto> = emptyList(),
    val error: String? = null
)

class WalkerWalksViewModel(
    private val repo: Repository
) : ViewModel() {

    private val _pending = MutableStateFlow(PendingWalksUiState())
    val pending: StateFlow<PendingWalksUiState> = _pending

    fun loadPending() {
        viewModelScope.launch {
            _pending.value = _pending.value.copy(isLoading = true, error = null)
            try {
                val all = repo.getWalks()

                // ✅ Pendientes: ajusta aquí si tu backend usa otro valor (ej: "requested")
                val pendingList = all.filter { it.status.equals("pending", ignoreCase = true) }

                _pending.value = PendingWalksUiState(
                    isLoading = false,
                    items = pendingList,
                    error = null
                )
            } catch (e: Exception) {
                _pending.value = PendingWalksUiState(
                    isLoading = false,
                    items = emptyList(),
                    error = e.message ?: "Error cargando pendientes"
                )
            }
        }
    }

    fun accept(walkId: Long) {
        viewModelScope.launch {
            try {
                val res = repo.acceptWalkRaw(walkId)
                if (res.isSuccessful) {
                    loadPending()
                } else {
                    _pending.value = _pending.value.copy(
                        error = "No se pudo aceptar (HTTP ${res.code()})"
                    )
                }
            } catch (e: Exception) {
                _pending.value = _pending.value.copy(error = e.message ?: "Error aceptando")
            }
        }
    }

    fun reject(walkId: Long) {
        viewModelScope.launch {
            try {
                val res = repo.rejectWalkRaw(walkId)
                if (res.isSuccessful) {
                    loadPending()
                } else {
                    _pending.value = _pending.value.copy(
                        error = "No se pudo rechazar (HTTP ${res.code()})"
                    )
                }
            } catch (e: Exception) {
                _pending.value = _pending.value.copy(error = e.message ?: "Error rechazando")
            }
        }
    }

    fun clearError() {
        _pending.value = _pending.value.copy(error = null)
    }
}
