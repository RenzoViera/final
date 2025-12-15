package com.example.mascota.screens.client.pets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import com.example.mascota.data.model.PetDto
import com.example.mascota.util.Resource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPetsScreen(
    nav: NavHostController,
    container: AppContainer
) {

    val vm = container.petsViewModel
    val state = vm.pets.collectAsState().value

    LaunchedEffect(Unit) {
        vm.loadPets()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Mascotas") },
                actions = {
                    IconButton(
                        onClick = { nav.navigate(Routes.CLIENT_PET_FORM) }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                    }
                }
            )
        }
    ) { padding ->

        when (val s = state) {

            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is Resource.Error -> {
                Text(
                    text = s.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                )
            }

            is Resource.Success -> {
                val pets: List<PetDto> = s.data

                if (pets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes mascotas todavía.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(pets) { pet ->
                            PetRow(
                                pet = pet,
                                onEdit = {
                                    nav.navigate(
                                        Routes.CLIENT_PET_FORM + "?id=${pet.id}"
                                    )
                                },
                                onDelete = {
                                    vm.deletePet(pet.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PetRow(
    pet: PetDto,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {

    val photoUrl = pet.photoUrl
        ?.trim()
        ?.takeIf { it.isNotBlank() && it.lowercase() != "null" }

    Card {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (photoUrl != null) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Foto de mascota",
                    modifier = Modifier.size(56.dp)
                )
            } else {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("—")
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Tipo: ${pet.species}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Notas: ${pet.notes ?: "-"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
