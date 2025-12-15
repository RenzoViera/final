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
import com.example.mascota.data.model.PetDto
import com.example.mascota.data.model.WalkCreateRequest
import com.example.mascota.viewmodel.client.RequestWalkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestWalkScreen(nav: NavHostController, container: AppContainer, walkerId: Long, addressId: Long)
 {
    val vm = remember { RequestWalkViewModel(container.repo) }
    val state by vm.uiState.collectAsState()

    // form
    var date by remember { mutableStateOf("") }       // YYYY-MM-DD
    var time by remember { mutableStateOf("") }       // HH:mm
    var duration by remember { mutableStateOf("50") } // minutos
    var notes by remember { mutableStateOf("") }
    var userAddressId by remember { mutableStateOf("1") } // por ahora manual/1

    // selección de mascota real
    var selectedPet by remember { mutableStateOf<PetDto?>(null) }
    var petsExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vm.loadPets()
    }

    LaunchedEffect(state.success) {
        if (state.success) {
            // vuelve a paseos y recarga (tu WalksScreen ya recarga al entrar)
            nav.navigate(Routes.CLIENT_WALKS) {
                popUpTo(Routes.CLIENT_WALKS) { inclusive = true }
            }
        }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Solicitar paseo", style = MaterialTheme.typography.titleLarge)
        Text("Paseador ID: $walkerId")

        if (state.error != null) {
            Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
        }

        // Mascotas (dropdown simple)
        ExposedDropdownMenuBox(
            expanded = petsExpanded,
            onExpandedChange = { petsExpanded = !petsExpanded }
        ) {
            OutlinedTextField(
                value = selectedPet?.name ?: "Selecciona una mascota",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                label = { Text("Mascota") }
            )
            ExposedDropdownMenu(
                expanded = petsExpanded,
                onDismissRequest = { petsExpanded = false }
            ) {
                state.pets.forEach { pet ->
                    DropdownMenuItem(
                        text = { Text(pet.name) },
                        onClick = {
                            selectedPet = pet
                            petsExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Fecha (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Hora (HH:mm)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = duration,
            onValueChange = { duration = it.filter(Char::isDigit) },
            label = { Text("Duración (min)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = userAddressId,
            onValueChange = { userAddressId = it.filter(Char::isDigit) },
            label = { Text("ID Dirección (user_address_id)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Nota para el paseador") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.loading && selectedPet != null,
            onClick = {
                val scheduledAt = "${date.trim()} ${time.trim()}" // formato exacto que viste
                val petId = selectedPet!!.id.toString()

                vm.submit(
                    WalkCreateRequest(
                        walkerId = walkerId.toString(),
                        petId = petId,
                        scheduledAt = scheduledAt,
                        durationMinutes = duration,
                        userAddressId = addressId.toString(),
                        notes = notes.ifBlank { null }
                    )
                )
            }
        ) {
            if (state.loading) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Enviando...")
                }
            } else {
                Text("Solicitar paseo")
            }
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.popBackStack() }
        ) { Text("Cancelar") }
    }
}
