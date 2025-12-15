package com.example.mascota.data.model

import com.google.gson.annotations.SerializedName

data class ReviewDto(
    val id: Long,
    @SerializedName("walk_id")
    val walkId: Long,
    @SerializedName("walker_id")
    val walkerId: Long,
    val rating: Int,
    val comment: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    // En tu ejemplo viene "walk": { ... } (sin mascota normalmente)
    val walk: WalkDto?
)
