package com.example.mascota.data.model

import com.google.gson.annotations.SerializedName

data class PetDto(
    val id: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    val name: String,
    val species: String,
    val notes: String?,
    val photoUrl: String?
)

data class PetCreateRequest(
    val name: String,
    @SerializedName("type")
    val type: String,
    val notes: String? = null,
    @SerializedName("photo_url")   // ✅ CAMBIO CLAVE
    val photoUrl: String? = null
)

data class PetUpdateRequest(
    val name: String,
    @SerializedName("type")
    val type: String,
    val notes: String? = null,
    @SerializedName("photo_url")   // ✅ CAMBIO CLAVE
    val photoUrl: String? = null
)
