package com.example.mascota.viewmodel.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.UserRole
import com.example.mascota.data.storage.SessionStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(private val store: SessionStore) : ViewModel() {

    fun decide(next: (role: UserRole?, token: String?) -> Unit) {
        viewModelScope.launch {
            val roleStr = store.roleFlow.first()
            val token = store.tokenFlow.first()
            val role = roleStr?.let { UserRole.valueOf(it) }
            next(role, token)
        }
    }
}
