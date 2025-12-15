package com.example.mascota

import android.content.Context
import com.example.mascota.data.api.ApiClient
import com.example.mascota.data.repo.RepositoryImpl
import com.example.mascota.data.storage.SessionStore
import com.example.mascota.viewmodel.client.PetsViewModel
import com.example.mascota.viewmodel.common.AuthViewModel
import com.example.mascota.viewmodel.common.RoleViewModel
import com.example.mascota.viewmodel.common.SplashViewModel

class AppContainer(context: Context) {

    val sessionStore = SessionStore(context)
    val api = ApiClient.create(sessionStore)
    val repo = RepositoryImpl(api)

    val petsViewModel: PetsViewModel by lazy { PetsViewModel(repo) }

    // ✅ ahora son PROPIEDADES
    val roleViewModel: RoleViewModel by lazy { RoleViewModel(sessionStore) }
    val authViewModel: AuthViewModel by lazy { AuthViewModel(api, sessionStore) }
    val splashViewModel: SplashViewModel by lazy { SplashViewModel(sessionStore) }
}
