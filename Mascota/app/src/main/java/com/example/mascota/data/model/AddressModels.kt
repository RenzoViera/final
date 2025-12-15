package com.example.mascota.data.model

import com.google.gson.annotations.SerializedName

data class AddressDto(
    val id: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    val label: String,
    val address: String,
    val lat: String,
    val lng: String,
    val owner: OwnerDto?
)

data class AddressCreateRequest(
    val label: String,
    val latitude: String,
    val longitude: String,
    val address: String,
    @SerializedName("is_available")
    val isAvailable: Boolean = true
)
