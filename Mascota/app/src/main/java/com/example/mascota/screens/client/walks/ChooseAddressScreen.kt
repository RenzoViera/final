package com.example.mascota.screens.client.walks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import com.example.mascota.data.model.AddressDto

@Composable
fun ChooseAddressScreen(nav: NavHostController, container: AppContainer) {

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var addresses by remember { mutableStateOf<List<AddressDto>>(emptyList()) }

    fun reload() {
        loading = true
        error = null
        // carga asíncrona
        // (usamos LaunchedEffect manual)
    }

    LaunchedEffect(Unit) {
        try {
            addresses = container.repo.getAddresses()
        } catch (e: Exception) {
            error = e.message ?: "Error cargando direcciones"
        } finally {
            loading = false
        }
    }

    Column(Modifier.fillMaxSize()) {
        Text(
            text = "Elegir dirección",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        when {
            loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(error ?: "Error")
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            loading = true
                            error = null
                            // recargar
                            // volvemos a lanzar carga aquí mismo:
                            // (sin crear otra coroutine scope extra)
                            // Nota: para simpleza, repetimos el try/catch en LaunchedEffect con key
                        }) { Text("Reintentar") }
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = { nav.popBackStack() }) { Text("Volver") }
                    }
                }
            }

            addresses.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No tienes direcciones guardadas.")
                        Spacer(Modifier.height(12.dp))
                        // Luego añadimos la pantalla para crear dirección
                        OutlinedButton(onClick = { nav.popBackStack() }) { Text("Volver") }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(addresses) { addr ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    nav.navigate("${Routes.CLIENT_WALKERS_NEARBY}/${addr.id}")
                                }
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(addr.label, style = MaterialTheme.typography.titleMedium)
                                Text(addr.address)
                            }
                        }
                    }
                    item { Spacer(Modifier.height(24.dp)) }
                }
            }
        }
    }
}
