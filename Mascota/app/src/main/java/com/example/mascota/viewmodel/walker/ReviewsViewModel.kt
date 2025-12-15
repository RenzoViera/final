package com.example.mascota.viewmodel.walker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.ReviewDto
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewsUiState(
    val isLoading: Boolean = false,
    val items: List<ReviewDto> = emptyList(),
    val error: String? = null
)

class ReviewsViewModel(
    private val repo: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val list = repo.getReviews()
                _uiState.value = ReviewsUiState(
                    isLoading = false,
                    items = list,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = ReviewsUiState(
                    isLoading = false,
                    items = emptyList(),
                    error = e.message ?: "Error cargando reviews"
                )
            }
        }
    }
}
