package com.example.mascota.screens.client.walks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import com.example.mascota.data.model.WalkStatus
import com.example.mascota.data.model.normStatus
import com.example.mascota.viewmodel.client.WalkDetailViewModel

@Composable
fun WalkDetailScreen(nav: NavHostController, container: AppContainer, walkId: Long)
 {
    val vm = remember { WalkDetailViewModel(container.repo) }
    val state by vm.uiState.collectAsState()

    LaunchedEffect(walkId) {
        vm.load(walkId)
    }

    // polling solo si está EN CURSO
    LaunchedEffect(state.walk?.status) {
        val s = state.walk?.status?.normStatus()
        if (s == WalkStatus.IN_PROGRESS) vm.startPollingIfInProgress(walkId)
        else vm.stopPolling()
    }

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(state.error ?: "Error")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { vm.load(walkId) }) { Text("Reintentar") }
            }
        }
        return
    }

    val w = state.walk ?: run {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No se encontró el paseo") }
        return
    }

    val status = w.status.normStatus()

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Detalle del paseo", style = MaterialTheme.typography.titleLarge)

        // Datos comunes
        Text("Estado: $status")

        Text("Mascota: ${w.pet?.name ?: "Pet #${w.petId}"}")
        Text("Paseador: ${w.walker?.name ?: (w.walkerId?.let { "Walker #$it" } ?: "Sin asignar")}")
        Text("Fecha/Hora: ${w.scheduledAt}")
        Text("Duración: ${w.durationMinutes} min")
        if (!w.notes.isNullOrBlank()) Text("Nota: ${w.notes}")

        Spacer(Modifier.height(8.dp))

        when (status) {

            WalkStatus.PENDING -> {
                Text("Aún esperando respuesta del paseador.")
                // opcional: botón cancelar (NO lo implemento sin endpoint claro)
                // OutlinedButton(...) { Text("Cancelar solicitud") }
            }

            WalkStatus.REJECTED -> {
                Text("Este paseador rechazó el paseo.")
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // ✅ reusar dirección del paseo para buscar paseadores cerca
                        val addressId = w.userAddressId ?: 0L
                        nav.navigate("${Routes.CLIENT_WALKERS_NEARBY}/$addressId?rebookFromWalkId=${w.id}")
                    }
                ) {
                    Text("Solicitar otro paseador")
                }
            }

            WalkStatus.ACCEPTED -> {
                Text("El paseo fue aceptado. Aún no ha iniciado.")
            }

            WalkStatus.IN_PROGRESS -> {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { nav.navigate("${Routes.CLIENT_WALK_MAP}/${w.id}") }
                ) {
                    Text("Ver mapa del paseo")
                }
            }

            WalkStatus.FINISHED -> {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { nav.navigate("${Routes.CLIENT_WALK_REVIEW}/${w.id}") }
                ) {
                    Text("Calificar paseador")
                }
            }
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.popBackStack() }
        ) { Text("Volver") }
    }
}
