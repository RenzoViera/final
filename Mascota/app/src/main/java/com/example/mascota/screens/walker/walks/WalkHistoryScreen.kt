package com.example.mascota.screens.walker.walks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import com.example.mascota.data.model.WalkDto
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.launch

private data class HistoryUiState(
    val isLoading: Boolean = false,
    val items: List<WalkDto> = emptyList(),
    val error: String? = null
)

@Composable
fun WalkHistoryScreen(nav: NavHostController, container: AppContainer) {

    val scope = rememberCoroutineScope()
    var ui by remember { mutableStateOf(HistoryUiState(isLoading = true)) }

    fun isFinished(status: String): Boolean {
        val s = status.lowercase().trim()
        return s == "finalized" || s == "finished" || s == "completed" || s == "ended" || s == "done"
    }

    fun load(repo: Repository) {
        scope.launch {
            ui = ui.copy(isLoading = true, error = null)
            try {
                // Usamos tu endpoint existente GET /walks y filtramos finalizados
                val all = repo.getWalks()
                val finished = all.filter { isFinished(it.status) }
                ui = HistoryUiState(isLoading = false, items = finished)
            } catch (e: Exception) {
                ui = HistoryUiState(isLoading = false, error = e.message ?: "Error cargando historial")
            }
        }
    }

    LaunchedEffect(Unit) { load(container.repo) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Historial (Finalizados)", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        ui.error?.let { msg ->
            Text(msg, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { ui = ui.copy(error = null) }) { Text("Ocultar") }
            Spacer(Modifier.height(8.dp))
        }

        if (ui.isLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        if (!ui.isLoading && ui.items.isEmpty()) {
            Text("No tienes paseos finalizados aún.")
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ui.items) { walk ->
                val pet = walk.pet
                val petName = pet?.name ?: "Mascota"
                val petPhoto = pet?.photoUrl ?: ""
                val dateTime = walk.scheduledAt
                val duration = walk.durationMinutes

                Card(
                    onClick = { nav.navigate("${Routes.WALKER_WALK_DETAIL}/${walk.id}") }
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Paseo #${walk.id}", style = MaterialTheme.typography.titleMedium)
                        Text("🐾 $petName")
                        if (petPhoto.isNotBlank() && petPhoto.lowercase() != "null") {
                            Text("Foto: $petPhoto", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("📅 $dateTime")
                        Text("⏱ $duration min")
                        Text("Estado: Finalizado")
                        // ⭐ Puntuación recibida (opcional):
                        // Aquí la mostraremos cuando tengamos claro dónde viene (en /walks/{id} o /reviews)
                    }
                }
            }
        }
    }
}
