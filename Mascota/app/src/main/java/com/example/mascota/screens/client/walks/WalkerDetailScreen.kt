package com.example.mascota.screens.client.walks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import kotlinx.coroutines.launch

@Composable
fun WalkerDetailScreen(
    nav: NavHostController,
    container: AppContainer,
    walkerId: Long,
    addressId: Long,
    walkId: Long // ✅ nuevo: si viene >0, estamos viendo historial
) {
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // datos reales del paseador
    var name by remember { mutableStateOf("Paseador") }
    var email by remember { mutableStateOf<String?>(null) }
    var photoUrl by remember { mutableStateOf<String?>(null) }

    val isHistoryMode = walkId > 0L
    val canRequest = addressId > 0L && !isHistoryMode

    LaunchedEffect(walkerId) {
        loading = true
        error = null
        try {
            val w = container.repo.getWalkerById(walkerId)
            name = w.name
            email = w.email
            photoUrl = w.photoUrl
        } catch (e: Exception) {
            error = e.message ?: "Error cargando paseador"
        } finally {
            loading = false
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Detalle del paseador", style = MaterialTheme.typography.titleLarge)

        if (loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Text("Nombre: $name")
        email?.let { Text("Email: $it") }
        if (!photoUrl.isNullOrBlank() && photoUrl != "null") {
            Text("Foto: $photoUrl", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(12.dp))

        // ✅ BOTÓN PARA HISTORIAL (calificar)
        if (isHistoryMode) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { nav.navigate("${Routes.CLIENT_WALK_REVIEW}/$walkId") }
            ) {
                Text("⭐ Calificar paseo")
            }
        }

        // ✅ BOTÓN NORMAL DEL FLUJO “solicitar paseo”
        if (canRequest) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    nav.navigate("${Routes.CLIENT_WALK_REQUEST}/$walkerId?addressId=$addressId")
                }
            ) {
                Text("Solicitar paseo")
            }
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.popBackStack() }
        ) {
            Text("Volver")
        }
    }
}
