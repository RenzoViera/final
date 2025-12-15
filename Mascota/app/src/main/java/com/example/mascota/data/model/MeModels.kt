package com.example.mascota.data.model

import com.google.gson.annotations.SerializedName

data class MeResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String,          // "owner" | "walker"
    val photoUrl: String?,
    val extras: WalkerExtrasDto? = null
)
