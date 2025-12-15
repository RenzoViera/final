package com.example.mascota.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import com.example.mascota.data.model.UserRole
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(nav: NavHostController, container: AppContainer) {

    LaunchedEffect(Unit) {
        val token = container.sessionStore.tokenFlow.first()
        val roleStr = container.sessionStore.roleFlow.first()
        val role = roleStr?.let { UserRole.valueOf(it) }

        val dest = if (!token.isNullOrBlank() && role != null) {
            if (role == UserRole.CLIENT) Routes.CLIENT_HOME else Routes.WALKER_HOME
        } else Routes.ROLE

        nav.navigate(dest) { popUpTo(Routes.SPLASH) { inclusive = true } }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Cargando...", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        CircularProgressIndicator()
    }
}
