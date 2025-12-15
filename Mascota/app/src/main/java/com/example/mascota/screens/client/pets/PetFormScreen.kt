package com.example.mascota.screens.client.pets

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.util.Resource
import com.example.mascota.data.model.PetUpdateRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetFormScreen(
    nav: NavHostController,
    container: AppContainer,
    petId: Long? = null   // ✅ CLAVE
) {
    val vm = container.petsViewModel
    val saveState = vm.saveState.collectAsState().value
    val petsState = vm.pets.collectAsState().value

    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }   // API usa "type"
    var notes by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }

    val isEdit = petId != null

    // ✅ Precargar datos si es edición
    LaunchedEffect(petId, petsState) {
        if (petId != null && petsState is Resource.Success) {
            val pet = petsState.data.firstOrNull { it.id == petId }
            if (pet != null) {
                name = pet.name
                type = pet.species          // species → type
                notes = pet.notes ?: ""
                photoUrl = pet.photoUrl
                    ?.takeIf { it.lowercase() != "null" }
                    ?: ""
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEdit) "Editar Mascota" else "Agregar Mascota")
                }
            )
        }
    ) { padding ->

        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                label = { Text("Tipo / Especie") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notas") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = photoUrl,
                onValueChange = { photoUrl = it },
                label = { Text("URL foto (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://...") }
            )

            if (saveState is Resource.Loading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            val err = (saveState as? Resource.Error)?.message
            if (!err.isNullOrBlank()) {
                Text(err, color = MaterialTheme.colorScheme.error)
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && type.isNotBlank() && saveState !is Resource.Loading,
                onClick = {
                    val n = name.trim()
                    val t = type.trim()
                    val no = notes.trim().ifBlank { null }
                    val ph = photoUrl.trim().ifBlank { null }

                    if (isEdit) {
                        vm.updatePet(
                            id = petId!!,
                            body = PetUpdateRequest(
                                name = n,
                                type = t,
                                notes = no,
                                photoUrl = ph
                            )
                        ) { nav.popBackStack() }
                    } else {
                        vm.createPet(
                            name = n,
                            species = t,   // species = type real
                            notes = no,
                            photoUrl = ph
                        ) { nav.popBackStack() }
                    }
                }
            ) {
                Text(if (isEdit) "Guardar cambios" else "Guardar")
            }

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { nav.popBackStack() }
            ) {
                Text("Cancelar")
            }
        }
    }
}
