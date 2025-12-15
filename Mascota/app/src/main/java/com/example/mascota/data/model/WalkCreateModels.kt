package com.example.mascota.data.model

import com.google.gson.annotations.SerializedName

data class WalkCreateRequest(
    @SerializedName("walker_id")
    val walkerId: String,
    @SerializedName("pet_id")
    val petId: String,
    @SerializedName("scheduled_at")
    val scheduledAt: String, // "YYYY-MM-DD HH:mm"
    @SerializedName("duration_minutes")
    val durationMinutes: String,
    @SerializedName("user_address_id")
    val userAddressId: String,
    val notes: String?
)
