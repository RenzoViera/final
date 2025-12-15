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
import com.example.mascota.viewmodel.walker.WalkerWalksViewModel

@Composable
fun PendingWalksScreen(nav: NavHostController, container: AppContainer) {

    val vm = remember { WalkerWalksViewModel(container.repo) }
    val ui by vm.pending.collectAsState()

    // confirm dialog state
    var confirmAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    var confirmTitle by remember { mutableStateOf("") }
    var confirmText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { vm.loadPending() }

    // Dialog confirm
    if (confirmAction != null) {
        AlertDialog(
            onDismissRequest = { confirmAction = null },
            confirmButton = {
                Button(onClick = {
                    confirmAction?.invoke()
                    confirmAction = null
                }) { Text("Confirmar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirmAction = null }) { Text("Cancelar") }
            },
            title = { Text(confirmTitle) },
            text = { Text(confirmText) }
        )
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Solicitudes pendientes", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))

        ui.error?.let { msg ->
            Text(msg, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { vm.clearError() }) { Text("Ocultar") }
            Spacer(Modifier.height(8.dp))
        }

        if (ui.isLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        if (ui.items.isEmpty() && !ui.isLoading) {
            Text("No hay solicitudes pendientes.")
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ui.items) { walk ->
                val pet = walk.pet
                val owner = walk.owner

                val petName = pet?.name ?: "Mascota"
                val petSpecies = pet?.species ?: "—"
                val petNotes = pet?.notes ?: ""
                val petPhoto = pet?.photoUrl ?: ""  // tu PetDto lo tiene así
                val ownerName = owner?.name ?: "Dueño"

                Card {
                    Column(
                        Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Paseo #${walk.id}", style = MaterialTheme.typography.titleMedium)

                        Text("👤 Dueño: $ownerName")
                        Text("📅 Fecha/hora: ${walk.scheduledAt}")
                        Text("⏱ Duración: ${walk.durationMinutes} min")

                        if (!walk.notes.isNullOrBlank()) {
                            Text("📝 Notas del dueño: ${walk.notes}")
                        }

                        Divider(Modifier.padding(vertical = 6.dp))

                        Text("🐾 Mascota: $petName")
                        Text("Tipo: $petSpecies")

                        if (petNotes.isNotBlank()) {
                            Text("Notas mascota: $petNotes")
                        }

                        if (petPhoto.isNotBlank() && petPhoto.lowercase() != "null") {
                            // Sin librerías de imagen: mostramos URL
                            Text("Foto: $petPhoto", style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    confirmTitle = "Aceptar paseo"
                                    confirmText = "¿Aceptar el paseo #${walk.id} de $petName?"
                                    confirmAction = { vm.accept(walk.id) }
                                }
                            ) { Text("Aceptar") }

                            OutlinedButton(
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    confirmTitle = "Rechazar paseo"
                                    confirmText = "¿Rechazar el paseo #${walk.id} de $petName?"
                                    confirmAction = { vm.reject(walk.id) }
                                }
                            ) { Text("Rechazar") }
                        }
                    }
                }
            }
        }
    }
}
