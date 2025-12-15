package com.example.mascota.viewmodel.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.model.UserRole
import com.example.mascota.data.storage.SessionStore
import kotlinx.coroutines.launch

class RoleViewModel(private val store: SessionStore) : ViewModel() {
    fun selectRole(role: UserRole) {
        viewModelScope.launch {
            store.saveRole(role)
        }
    }
}
