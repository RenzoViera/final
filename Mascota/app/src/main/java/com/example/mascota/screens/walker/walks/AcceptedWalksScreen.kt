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
import com.example.mascota.viewmodel.walker.AcceptedWalksViewModel

@Composable
fun AcceptedWalksScreen(nav: NavHostController, container: AppContainer) {

    val vm = remember { AcceptedWalksViewModel(container.repo) }
    val ui by vm.accepted.collectAsState()

    LaunchedEffect(Unit) { vm.loadAccepted() }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Aceptados", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        ui.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { vm.clearError() }) { Text("Ocultar") }
        }

        if (ui.isLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }

        if (ui.items.isEmpty() && !ui.isLoading) {
            Text("No tienes paseos aceptados todavía.")
            return
        }

        // subdividir
        val inProgress = ui.items.filter { it.status.equals("in_progress", true) || it.status.equals("ongoing", true) }
        val accepted = ui.items.filter { !inProgress.contains(it) } // lo demás lo tratamos como “por iniciar”

        if (accepted.isNotEmpty()) {
            Text("Por iniciar", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(accepted) { w ->
                    AcceptedCard(w) {
                        nav.navigate("${Routes.WALKER_WALK_DETAIL}/${w.id}")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }

        if (inProgress.isNotEmpty()) {
            Text("En curso", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(inProgress) { w ->
                    AcceptedCard(w) {
                        nav.navigate("${Routes.WALKER_WALK_DETAIL}/${w.id}")
                    }
                }
            }
        }
    }
}

@Composable
private fun AcceptedCard(walk: com.example.mascota.data.model.WalkDto, onClick: () -> Unit) {
    val pet = walk.pet
    Card(onClick = onClick) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Paseo #${walk.id}", style = MaterialTheme.typography.titleMedium)
            Text("🐾 ${pet?.name ?: "Mascota"} (${pet?.species ?: "—"})")
            Text("📅 ${walk.scheduledAt}")
            Text("⏱ ${walk.durationMinutes} min")
            Text("Estado: ${walk.status}")
        }
    }
}
