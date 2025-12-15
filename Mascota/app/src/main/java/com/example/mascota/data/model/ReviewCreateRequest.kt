package com.example.mascota.data.model

data class ReviewCreateRequest(
    val rating: Int,
    val comment: String?
)
