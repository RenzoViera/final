package com.example.mascota.viewmodel.client

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.*
import com.example.mascota.data.repo.Repository
import com.example.mascota.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class PetsViewModel(private val repo: Repository) : ViewModel() {

    private val _pets = MutableStateFlow<Resource<List<PetDto>>>(Resource.Loading)
    val pets = _pets.asStateFlow()

    private val _saveState = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val saveState = _saveState.asStateFlow()

    fun loadPets() {
        viewModelScope.launch {
            _pets.value = Resource.Loading
            try {
                _pets.value = Resource.Success(repo.getPets())
            } catch (e: Exception) {
                _pets.value = Resource.Error(e.message ?: "Error cargando mascotas")
            }
        }
    }

    fun createPet(
        name: String,
        species: String,
        notes: String?,
        photoUrl: String?,
        onOk: () -> Unit
    ) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading
            try {
                val req = PetCreateRequest(
                    name = name,
                    type = species,          // ✅ aquí va "type"
                    notes = notes,
                    photoUrl = photoUrl
                )

                val resp = repo.createPetRaw(req)

                val raw = resp.body()?.string() ?: resp.errorBody()?.string()
                Log.e("PetsViewModel", "POST /pets code=${resp.code()} raw=$raw")

                if (resp.isSuccessful) {
                    loadPets()
                    _saveState.value = Resource.Success(Unit)
                    onOk()
                } else {
                    _saveState.value = Resource.Error("HTTP ${resp.code()} ${raw ?: ""}".trim(), resp.code())
                }
            } catch (e: Exception) {
                Log.e("PetsViewModel", "createPet EXCEPTION", e)
                _saveState.value = Resource.Error(e.message ?: "Error creando mascota")
            }
        }
    }



    fun deletePet(id: Long) {
        viewModelScope.launch {
            try {
                repo.deletePet(id)
                loadPets()
            } catch (e: Exception) {
                Log.e("PetsViewModel", "deletePet error", e)
            }
        }
    }

    fun updatePet(id: Long, body: PetUpdateRequest, onOk: () -> Unit) {
        viewModelScope.launch {
            _saveState.value = Resource.Loading
            try {
                repo.updatePet(id, body) // devuelve PetDto
                loadPets()
                _saveState.value = Resource.Success(Unit)
                onOk()
            } catch (e: Exception) {
                _saveState.value = Resource.Error(e.message ?: "Error editando mascota")
            }
        }
    }

}
