package com.example.mascota.viewmodel.walker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.WalkDto
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

data class AcceptedUiState(
    val isLoading: Boolean = false,
    val items: List<WalkDto> = emptyList(),
    val error: String? = null
)

data class WalkDetailUiState(
    val isLoading: Boolean = false,
    val walk: WalkDto? = null,
    val error: String? = null
)

class AcceptedWalksViewModel(private val repo: Repository) : ViewModel() {

    private val _accepted = MutableStateFlow(AcceptedUiState())
    val accepted: StateFlow<AcceptedUiState> = _accepted

    private val _detail = MutableStateFlow(WalkDetailUiState())
    val detail: StateFlow<WalkDetailUiState> = _detail

    fun loadAccepted() {
        viewModelScope.launch {
            _accepted.value = _accepted.value.copy(isLoading = true, error = null)
            try {
                val list = repo.getAcceptedWalks()
                _accepted.value = AcceptedUiState(isLoading = false, items = list)
            } catch (e: Exception) {
                _accepted.value = AcceptedUiState(isLoading = false, error = e.message ?: "Error cargando aceptados")
            }
        }
    }

    fun loadDetail(id: Long) {
        viewModelScope.launch {
            _detail.value = _detail.value.copy(isLoading = true, error = null)
            try {
                val w = repo.getWalkById(id)
                _detail.value = WalkDetailUiState(isLoading = false, walk = w)
            } catch (e: Exception) {
                _detail.value = WalkDetailUiState(isLoading = false, error = e.message ?: "Error cargando detalle")
            }
        }
    }

    fun start(id: Long, onOk: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val res = repo.startWalkRaw(id)
                if (res.isSuccessful) {
                    loadDetail(id)
                    onOk()
                } else _detail.value = _detail.value.copy(error = "No se pudo iniciar (HTTP ${res.code()})")
            } catch (e: Exception) {
                _detail.value = _detail.value.copy(error = e.message ?: "Error iniciando")
            }
        }
    }

    fun end(id: Long, onOk: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val res = repo.endWalkRaw(id)
                if (res.isSuccessful) {
                    loadDetail(id)
                    onOk()
                } else _detail.value = _detail.value.copy(error = "No se pudo finalizar (HTTP ${res.code()})")
            } catch (e: Exception) {
                _detail.value = _detail.value.copy(error = e.message ?: "Error finalizando")
            }
        }
    }

    fun uploadPhoto(id: Long, part: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                val res = repo.uploadWalkPhotoRaw(id, part)
                if (res.isSuccessful) {
                    loadDetail(id)
                } else _detail.value = _detail.value.copy(error = "No se pudo subir foto (HTTP ${res.code()})")
            } catch (e: Exception) {
                _detail.value = _detail.value.copy(error = e.message ?: "Error subiendo foto")
            }
        }
    }

    fun clearError() {
        _accepted.value = _accepted.value.copy(error = null)
        _detail.value = _detail.value.copy(error = null)
    }
}
