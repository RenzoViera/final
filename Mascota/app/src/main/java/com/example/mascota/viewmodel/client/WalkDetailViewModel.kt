package com.example.mascota.viewmodel.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.WalkDto
import com.example.mascota.data.model.WalkStatus
import com.example.mascota.data.model.normStatus
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class WalkDetailUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val walk: WalkDto? = null
)

class WalkDetailViewModel(private val repo: Repository) : ViewModel() {

    private val _uiState = MutableStateFlow(WalkDetailUiState())
    val uiState: StateFlow<WalkDetailUiState> = _uiState.asStateFlow()

    private var pollJob: Job? = null

    fun load(walkId: Long) {
        viewModelScope.launch {
            _uiState.value = WalkDetailUiState(loading = true)
            try {
                val w = repo.getWalkById(walkId)
                _uiState.value = WalkDetailUiState(walk = w)
            } catch (e: Exception) {
                _uiState.value = WalkDetailUiState(error = e.message ?: "Error cargando paseo")
            }
        }
    }

    fun startPollingIfInProgress(walkId: Long) {
        pollJob?.cancel()
        pollJob = viewModelScope.launch {
            while (isActive) {
                try {
                    val w = repo.getWalkById(walkId)
                    _uiState.value = _uiState.value.copy(walk = w, error = null, loading = false)

                    if (w.status.normStatus() != WalkStatus.IN_PROGRESS) {
                        // ya no está en curso -> dejamos de hacer polling
                        break
                    }
                } catch (_: Exception) {
                    // no rompas la app si falla un refresh
                }
                delay(5000) // cada 5s
            }
        }
    }

    fun stopPolling() {
        pollJob?.cancel()
        pollJob = null
    }
}
