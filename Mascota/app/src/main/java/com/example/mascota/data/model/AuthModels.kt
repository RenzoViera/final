package com.example.mascota.data.model

import com.google.gson.annotations.SerializedName

enum class UserRole { CLIENT, WALKER }

data class LoginRequest(
    val email: String,
    val password: String
)

data class ClientRegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val photoUrl: String? = null
)

data class WalkerRegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val photoUrl: String? = null,
    @SerializedName("price_hour")
    val priceHour: String
)

data class ClientLoginResponse(
    @SerializedName("access_token")
    val accessToken: String
)

data class WalkerLoginResponse(
    @SerializedName("token")
    val token: String? = null,

    @SerializedName("access_token")
    val accessToken: String? = null
) {
    fun resolvedToken(): String = token ?: accessToken ?: ""
}



data class ApiError(
    val message: String? = null
)
