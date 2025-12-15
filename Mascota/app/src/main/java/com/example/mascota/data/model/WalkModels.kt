package com.example.mascota.data.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

/* =======================
   OWNER
   ======================= */
data class OwnerDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: String, // "owner"
    val photoUrl: String?
)

/* =======================
   WALKER
   ======================= */
data class WalkWalkerDto(
    val id: Long,
    val name: String,
    val email: String,
    val role: String, // "walker"
    val photoUrl: String?
)

/* =======================
   WALK
   ======================= */
data class WalkDto(
    val id: Long,

    @SerializedName("owner_id")
    val ownerId: Long,

    @SerializedName("walker_id")
    val walkerId: Long?,

    @SerializedName("pet_id")
    val petId: Long,

    @SerializedName("user_address_id")
    val userAddressId: Long?,

    val status: String,

    @SerializedName("scheduled_at")
    val scheduledAt: String,

    @SerializedName("duration_minutes")
    val durationMinutes: Int,

    val notes: String?,

    // ---------- TIEMPOS ----------
    @SerializedName("started_at")
    val startedAt: String? = null,

    @SerializedName("ended_at")
    val endedAt: String? = null,

    // ---------- METADATA ----------
    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    // ---------- RELACIONES ----------
    val pet: PetDto?,
    val owner: OwnerDto?,
    val walker: WalkWalkerDto?,

    // ---------- SOLO PARA DETALLE ----------
    // vienen en GET /walks/{id}
    val address: AddressDto? = null,

    // ⚠️ JsonElement permite: objeto | array | string | null
    val locations: List<JsonElement> = emptyList(),
    val photos: List<JsonElement> = emptyList(),

    // 🔑 ESTE ERA EL PROBLEMA → puede ser JsonNull
    val review: JsonElement? = null
)
