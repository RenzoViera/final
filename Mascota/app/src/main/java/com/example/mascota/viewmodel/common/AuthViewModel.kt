package com.example.mascota.viewmodel.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.api.ApiService
import com.example.mascota.data.model.*
import com.example.mascota.data.storage.SessionStore
import com.example.mascota.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel(
    private val api: ApiService,
    private val store: SessionStore
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val state = _state.asStateFlow()

    fun login(email: String, password: String, role: UserRole, onOk: () -> Unit) {
        viewModelScope.launch {
            _state.value = Resource.Loading
            try {
                val token = when (role) {
                    UserRole.CLIENT -> api.clientLogin(LoginRequest(email, password)).accessToken
                    UserRole.WALKER -> api.walkerLogin(LoginRequest(email, password)).resolvedToken()
                }

                // ✅ Importante: limpia sesión anterior para evitar mezclar tokens/roles
                // (no rompe Dueño, solo evita inconsistencias)
                store.clear()

                // ✅ guarda token + rol
                store.saveToken(token)
                store.saveRole(role)

                _state.value = Resource.Success(Unit)
                onOk()
            } catch (e: HttpException) {
                _state.value = Resource.Error("Error HTTP ${e.code()}", e.code())
            } catch (e: Exception) {
                _state.value = Resource.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun registerClient(
        name: String,
        email: String,
        password: String,
        photoUrl: String?,
        onOk: () -> Unit
    ) {
        viewModelScope.launch {
            _state.value = Resource.Loading
            try {
                api.clientRegister(
                    ClientRegisterRequest(name, email, password, photoUrl)
                )
                _state.value = Resource.Success(Unit)
                onOk()
            } catch (e: Exception) {
                _state.value = Resource.Error(e.message ?: "Error registro")
            }
        }
    }

    fun registerWalker(
        name: String,
        email: String,
        password: String,
        photoUrl: String?,
        priceHour: String,
        onOk: () -> Unit
    ) {
        viewModelScope.launch {
            _state.value = Resource.Loading
            try {
                api.walkerRegister(
                    WalkerRegisterRequest(name, email, password, photoUrl, priceHour)
                )
                _state.value = Resource.Success(Unit)
                onOk()
            } catch (e: Exception) {
                _state.value = Resource.Error(e.message ?: "Error registro")
            }
        }
    }
}
