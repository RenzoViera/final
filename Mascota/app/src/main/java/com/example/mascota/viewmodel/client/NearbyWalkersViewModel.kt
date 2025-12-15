package com.example.mascota.viewmodel.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.WalkerDto
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NearbyWalkersUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val walkers: List<WalkerDto> = emptyList()
)

class NearbyWalkersViewModel(
    private val repo: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NearbyWalkersUiState())
    val uiState: StateFlow<NearbyWalkersUiState> = _uiState.asStateFlow()

    fun load(latitude: String, longitude: String) {
        viewModelScope.launch {
            _uiState.value = NearbyWalkersUiState(loading = true)
            try {
                val list = repo.getNearbyWalkers(latitude, longitude)
                    .filter { (it.walkerStatus?.isAvailable ?: 1) == 1 } // por si acaso
                _uiState.value = NearbyWalkersUiState(walkers = list)
            } catch (e: Exception) {
                _uiState.value = NearbyWalkersUiState(error = e.message ?: "Error cargando paseadores")
            }
        }
    }
}
