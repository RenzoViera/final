package com.example.mascota.data.model

import com.google.gson.annotations.SerializedName

data class WalkerDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: String, // "walker"
    val photoUrl: String?,
    val extras: WalkerExtrasDto,
    @SerializedName("walker_status")
    val walkerStatus: WalkerStatusDto? = null
)

data class WalkerExtrasDto(
    @SerializedName("price_hour")
    val priceHour: String,
    @SerializedName("rating_summary")
    val ratingSummary: Double
)

data class WalkerStatusDto(
    val id: Long,
    @SerializedName("walker_id")
    val walkerId: Long,
    @SerializedName("is_available")
    val isAvailable: Int,
    @SerializedName("current_latitude")
    val currentLatitude: String?,
    @SerializedName("current_longitude")
    val currentLongitude: String?
)
