package com.example.mascota.data.model

import com.google.gson.annotations.SerializedName

data class WalkerLocationRequest(
    val latitude: String,
    val longitude: String
)

data class WalkerLocationResponse(
    val id: Long,
    @SerializedName("walker_id") val walkerId: Long,
    @SerializedName("is_available") val isAvailable: Int,
    @SerializedName("current_latitude") val currentLatitude: String?,
    @SerializedName("current_longitude") val currentLongitude: String?
)
