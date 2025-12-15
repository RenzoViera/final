package com.example.mascota.data.repo

import com.example.mascota.data.api.ApiService
import com.example.mascota.data.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import com.example.mascota.data.model.AddressDto


class RepositoryImpl(
    private val api: ApiService
) : Repository {

    // PETS
    override suspend fun getAddresses(): List<AddressDto> = api.getAddresses()
    override suspend fun getPets(): List<PetDto> = api.getPets()
    override suspend fun createPetRaw(req: PetCreateRequest): Response<ResponseBody> = api.createPetRaw(req)
    override suspend fun updatePet(id: Long, body: PetUpdateRequest): PetDto = api.updatePet(id, body)
    override suspend fun deletePet(id: Long) { api.deletePet(id) }

    // WALKS
    override suspend fun getWalks(): List<WalkDto> = api.getWalks()
    override suspend fun getWalkById(id: Long): WalkDto = api.getWalkById(id)
    override suspend fun createWalkRaw(req: WalkCreateRequest): Response<ResponseBody> = api.createWalkRaw(req)

    // WALKERS
    override suspend fun getNearbyWalkers(latitude: String, longitude: String): List<WalkerDto> =
        api.getNearbyWalkers(NearbyWalkersRequest(latitude, longitude))

    override suspend fun getWalkerById(id: Long): WalkerDto = api.getWalkerById(id)

    override suspend fun setWalkerAvailability(isAvailable: Boolean): WalkerAvailabilityResponse =
        api.setWalkerAvailability(WalkerAvailabilityRequest(isAvailable))

    override suspend fun postWalkerLocation(latitude: String, longitude: String): WalkerLocationResponse =
        api.postWalkerLocation(WalkerLocationRequest(latitude, longitude))

    override suspend fun acceptWalkRaw(id: Long) = api.acceptWalkRaw(id)
    override suspend fun rejectWalkRaw(id: Long) = api.rejectWalkRaw(id)

    override suspend fun getAcceptedWalks(): List<WalkDto> = api.getAcceptedWalks()
    override suspend fun startWalkRaw(id: Long) = api.startWalkRaw(id)
    override suspend fun endWalkRaw(id: Long) = api.endWalkRaw(id)
    override suspend fun uploadWalkPhotoRaw(id: Long, photo: okhttp3.MultipartBody.Part) = api.uploadWalkPhotoRaw(id, photo)

    // REVIEWS
    override suspend fun getReviews(): List<ReviewDto> = api.getReviews()
    override suspend fun getReviewById(id: Long): ReviewDto = api.getReviewById(id)

}
