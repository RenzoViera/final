package com.example.mascota.viewmodel.walker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.ReviewDto
import com.example.mascota.data.model.WalkDto
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReviewDetailUiState(
    val isLoading: Boolean = false,
    val review: ReviewDto? = null,
    val walk: WalkDto? = null,
    val error: String? = null
)

class ReviewDetailViewModel(
    private val repo: Repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun load(reviewId: Long) {
        viewModelScope.launch {
            _uiState.value = ReviewDetailUiState(isLoading = true)
            try {
                val review = repo.getReviewById(reviewId)

                // Para mostrar "el perro": pedimos el paseo completo (trae pet)
                val walk = try {
                    repo.getWalkById(review.walkId)
                } catch (_: Exception) {
                    // si falla, al menos muestra el walk que venía en el review
                    review.walk
                }

                _uiState.value = ReviewDetailUiState(
                    isLoading = false,
                    review = review,
                    walk = walk,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = ReviewDetailUiState(
                    isLoading = false,
                    review = null,
                    walk = null,
                    error = e.message ?: "Error cargando detalle"
                )
            }
        }
    }
}
