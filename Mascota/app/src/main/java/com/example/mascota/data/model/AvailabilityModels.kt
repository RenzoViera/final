package com.example.mascota.data.model

import com.google.gson.annotations.SerializedName

data class WalkerAvailabilityRequest(
    @SerializedName("is_available")
    val isAvailable: Boolean
)

data class WalkerAvailabilityResponse(
    val id: Long,
    @SerializedName("walker_id")
    val walkerId: Long,
    @SerializedName("is_available")
    val isAvailable: Int, // 1 activo, 0 inactivo
    @SerializedName("current_latitude")
    val currentLatitude: String?,
    @SerializedName("current_longitude")
    val currentLongitude: String?
)
