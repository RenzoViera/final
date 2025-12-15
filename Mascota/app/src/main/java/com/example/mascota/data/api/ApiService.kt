package com.example.mascota.data.api

import com.example.mascota.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import com.example.mascota.data.model.AddressDto


interface ApiService {

    // ---------- AUTH ----------
    @POST("auth/clientlogin")
    suspend fun clientLogin(@Body body: LoginRequest): ClientLoginResponse

    @POST("auth/walkerlogin")
    suspend fun walkerLogin(@Body body: LoginRequest): WalkerLoginResponse

    @POST("auth/clientregister")
    suspend fun clientRegister(@Body body: ClientRegisterRequest): OwnerDto

    @POST("auth/walkerregister")
    suspend fun walkerRegister(@Body body: WalkerRegisterRequest): WalkerDto

    @GET("me")
    suspend fun getMe(): MeResponse

    // ---------- PETS ----------
    @GET("pets")
    suspend fun getPets(): List<PetDto>

    @POST("pets")
    suspend fun createPetRaw(@Body body: PetCreateRequest): Response<ResponseBody>

    @PUT("pets/{id}")
    suspend fun updatePet(
        @Path("id") id: Long,
        @Body body: PetUpdateRequest
    ): PetDto

    @DELETE("pets/{id}")
    suspend fun deletePet(@Path("id") id: Long)

    @POST("walks/{id}/review")
    suspend fun createReviewRaw(
        @Path("id") id: Long,
        @Body body: ReviewCreateRequest
    ): retrofit2.Response<okhttp3.ResponseBody>


    // ---------- WALKERS ----------
    @POST("walkers/nearby")
    suspend fun getNearbyWalkers(@Body body: NearbyWalkersRequest): List<WalkerDto>

    @GET("addresses")
    suspend fun getAddresses(): List<AddressDto>



    // Ya lo tienes:
    @GET("walkers/{id}")
    suspend fun getWalkerById(@Path("id") id: Long): WalkerDto

    // ---------- WALKS ----------
    @POST("walks")
    suspend fun createWalkRaw(@Body body: WalkCreateRequest): Response<ResponseBody>


    // ---------- WALKS ----------
    @GET("walks")
    suspend fun getWalks(): List<WalkDto>

    @GET("walks/{id}")
    suspend fun getWalkById(@Path("id") id: Long): WalkDto

    // ---------- WALKERS ----------
    @POST("walks/{id}/accept")
    suspend fun acceptWalkRaw(
        @Path("id") id: Long
    ): retrofit2.Response<okhttp3.ResponseBody>

    @POST("walks/{id}/reject")
    suspend fun rejectWalkRaw(
        @Path("id") id: Long
    ): retrofit2.Response<okhttp3.ResponseBody>

    @POST("walkers/availability")
    suspend fun setWalkerAvailability(
        @Body body: WalkerAvailabilityRequest
    ): WalkerAvailabilityResponse

    @POST("walkers/location")
    suspend fun postWalkerLocation(
        @Body body: WalkerLocationRequest
    ): WalkerLocationResponse

    // ✅ Aceptados
    @GET("walks/accepted")
    suspend fun getAcceptedWalks(): List<WalkDto>

    // ✅ Start / End
    @POST("walks/{id}/start")
    suspend fun startWalkRaw(@Path("id") id: Long): retrofit2.Response<okhttp3.ResponseBody>

    @POST("walks/{id}/end")
    suspend fun endWalkRaw(@Path("id") id: Long): retrofit2.Response<okhttp3.ResponseBody>

    // ✅ Subir foto (multipart)
    @Multipart
    @POST("walks/{id}/photo")
    suspend fun uploadWalkPhotoRaw(
        @Path("id") id: Long,
        @Part photo: okhttp3.MultipartBody.Part
    ): retrofit2.Response<okhttp3.ResponseBody>

    // ---------- REVIEWS (WALKER) ----------
    @GET("reviews")
    suspend fun getReviews(): List<ReviewDto>

    @GET("reviews/{id}")
    suspend fun getReviewById(@Path("id") id: Long): ReviewDto


}
