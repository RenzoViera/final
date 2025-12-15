package com.example.mascota.screens.client

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import kotlinx.coroutines.launch

@Composable
fun ClientHomeScreen(nav: NavHostController, container: AppContainer) {

    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Estás en la app de Dueño", style = MaterialTheme.typography.titleLarge)

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.navigate(Routes.CLIENT_PETS) }
        ) {
            Text("Mis Mascotas")
        }

        // ✅ NUEVO BOTÓN
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.navigate(Routes.CLIENT_WALKS) }
        ) {
            Text("Paseos")
        }

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            onClick = {
                scope.launch {
                    container.sessionStore.clear()
                    nav.navigate(Routes.ROLE) {
                        popUpTo(Routes.CLIENT_HOME) { inclusive = true }
                    }
                }
            }
        ) {
            Text("Cerrar sesión")
        }
    }
}
