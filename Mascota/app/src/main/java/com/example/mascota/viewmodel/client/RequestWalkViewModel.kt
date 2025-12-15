package com.example.mascota.viewmodel.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.PetDto
import com.example.mascota.data.model.WalkCreateRequest
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RequestWalkUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val pets: List<PetDto> = emptyList()
)

class RequestWalkViewModel(
    private val repo: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RequestWalkUiState())
    val uiState: StateFlow<RequestWalkUiState> = _uiState.asStateFlow()

    fun loadPets() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(loading = true, error = null)
                val pets = repo.getPets()
                _uiState.value = _uiState.value.copy(loading = false, pets = pets)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message ?: "Error cargando mascotas")
            }
        }
    }

    fun submit(req: WalkCreateRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null, success = false)
            try {
                val resp = repo.createWalkRaw(req)
                if (resp.isSuccessful) {
                    _uiState.value = _uiState.value.copy(loading = false, success = true)
                } else {
                    val body = resp.errorBody()?.string()
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        error = "Error ${resp.code()}: ${body ?: "No body"}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(loading = false, error = e.message ?: "Error creando paseo")
            }
        }
    }
}
