package com.example.mascota.viewmodel.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.WalkDto
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WalksUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val current: List<WalkDto> = emptyList(),
    val history: List<WalkDto> = emptyList()
)

class WalksViewModel(
    private val repo: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WalksUiState())
    val uiState: StateFlow<WalksUiState> = _uiState.asStateFlow()

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)

            try {
                val all = repo.getWalks()

                val (history, current) = all.partition { it.status.isFinalStatus() }

                _uiState.value = WalksUiState(
                    loading = false,
                    error = null,
                    current = current.sortedBy { it.scheduledAt },
                    history = history.sortedByDescending { it.scheduledAt }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "Error cargando paseos"
                )
            }
        }
    }
}

/**
 * Filtro robusto mientras no tengamos el JSON exacto:
 * Considera finalizado si el status contiene estas variantes.
 */
private fun String?.isFinalStatus(): Boolean {
    val s = (this ?: "").trim().lowercase()
    return s == "finalizado" ||
            s == "finished" ||
            s == "completed" ||
            s == "done" ||
            s.contains("final")
}
