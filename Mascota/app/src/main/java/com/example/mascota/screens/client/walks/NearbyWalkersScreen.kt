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
import com.example.mascota.data.model.WalkerDto
import com.example.mascota.viewmodel.client.NearbyWalkersViewModel

@Composable
fun NearbyWalkersScreen(
    nav: NavHostController,
    container: AppContainer,
    addressId: Long
) {
    val vm = remember { NearbyWalkersViewModel(container.repo) }
    val state by vm.uiState.collectAsState()

    var errorLocal by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("Paseadores cercanos") }

    LaunchedEffect(addressId) {
        errorLocal = null
        try {
            val addresses = container.repo.getAddresses()
            val addr = addresses.firstOrNull { it.id == addressId }

            if (addr == null) {
                errorLocal = "Dirección no encontrada"
                return@LaunchedEffect
            }

            title = "Paseadores cerca de: ${addr.label}"
            vm.load(addr.lat, addr.lng)
        } catch (e: Exception) {
            errorLocal = e.message ?: "Error cargando dirección"
        }
    }

    Column(Modifier.fillMaxSize()) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        if (errorLocal != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorLocal ?: "Error")
            }
            return
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
                    Button(onClick = { nav.popBackStack() }) { Text("Volver") }
                }
            }
            return
        }

        if (state.walkers.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay paseadores disponibles cerca")
            }
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.walkers) { w: WalkerDto ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // ✅ arrastramos addressId como query
                            nav.navigate("${Routes.CLIENT_WALKER_DETAIL}/${w.id}?addressId=$addressId")
                        }
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(w.name, style = MaterialTheme.typography.titleMedium)
                        Text("Calificación: ${w.extras.ratingSummary}")
                        Text("Costo/hr: ${w.extras.priceHour}")
                    }
                }
            }
        }
    }
}
