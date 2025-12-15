package com.example.mascota.data.repo

import com.example.mascota.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import com.example.mascota.data.model.AddressDto

interface Repository {
    // Pets
    suspend fun getAddresses(): List<AddressDto>
    suspend fun getPets(): List<PetDto>
    suspend fun createPetRaw(req: PetCreateRequest): Response<ResponseBody>
    suspend fun updatePet(id: Long, body: PetUpdateRequest): PetDto
    suspend fun deletePet(id: Long)

    // Walks
    suspend fun getWalks(): List<WalkDto>
    suspend fun getWalkById(id: Long): WalkDto
    suspend fun createWalkRaw(req: WalkCreateRequest): Response<ResponseBody>

    // Walkers
    suspend fun getNearbyWalkers(latitude: String, longitude: String): List<WalkerDto>
    suspend fun getWalkerById(id: Long): WalkerDto

    // Walker availability
    suspend fun setWalkerAvailability(isAvailable: Boolean): WalkerAvailabilityResponse

    suspend fun postWalkerLocation(latitude: String, longitude: String): WalkerLocationResponse

    suspend fun acceptWalkRaw(id: Long): retrofit2.Response<okhttp3.ResponseBody>
    suspend fun rejectWalkRaw(id: Long): retrofit2.Response<okhttp3.ResponseBody>

    suspend fun getAcceptedWalks(): List<WalkDto>
    suspend fun startWalkRaw(id: Long): retrofit2.Response<okhttp3.ResponseBody>
    suspend fun endWalkRaw(id: Long): retrofit2.Response<okhttp3.ResponseBody>
    suspend fun uploadWalkPhotoRaw(id: Long, photo: okhttp3.MultipartBody.Part): retrofit2.Response<okhttp3.ResponseBody>

    // Reviews (Walker)
    suspend fun getReviews(): List<ReviewDto>
    suspend fun getReviewById(id: Long): ReviewDto

}
