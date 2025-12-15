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
import com.example.mascota.data.model.WalkDto
import com.example.mascota.viewmodel.client.WalksViewModel

private enum class WalkTab(val title: String) {
    CURRENT("Paseos actuales"),
    HISTORY("Historial de paseos")
}

@Composable
fun WalksScreen(nav: NavHostController, container: AppContainer) {

    var selectedTab by remember { mutableStateOf(WalkTab.CURRENT) }

    // ViewModel simple (sin factory). Funciona mientras no recrees container.
    val vm = remember { WalksViewModel(container.repo) }

    val state by vm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        vm.load()
    }

    Column(Modifier.fillMaxSize()) {

        Text(
            text = "Paseos",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        TabRow(selectedTabIndex = selectedTab.ordinal) {
            WalkTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab.ordinal == index,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.title) }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = { nav.navigate(Routes.CLIENT_CHOOSE_ADDRESS) }
            ) {
                Text("Nuevo paseo")
            }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = { vm.load() } // recargar lista
            ) {
                Text("Recargar")
            }
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
                    Button(onClick = { vm.load() }) { Text("Reintentar") }
                }
            }
            return
        }

        val list = if (selectedTab == WalkTab.CURRENT) state.current else state.history

        WalksList(
            items = list,
            onClick = { walk ->
                if (selectedTab == WalkTab.HISTORY) {
                    val wId = walk.walkerId ?: 0L
                    if (wId == 0L) {
                        // si por alguna razón no hay walker asignado, cae al detalle normal
                        nav.navigate("${Routes.CLIENT_WALK_DETAIL}/${walk.id}")
                    } else {
                        // modo historial: addressId=0 y pasamos walkId para que en WalkerDetail salga "Calificar"
                        nav.navigate("${Routes.CLIENT_WALKER_DETAIL}/$wId?addressId=0&walkId=${walk.id}")
                    }
                } else {
                    nav.navigate("${Routes.CLIENT_WALK_DETAIL}/${walk.id}")
                }
            }
        )
    }
}

@Composable
private fun WalksList(
    items: List<WalkDto>,
    onClick: (WalkDto) -> Unit
) {
    if (items.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay paseos para mostrar")
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items) { walk ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick(walk) } // ✅ CORRECTO
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = walk.scheduledAt,
                        style = MaterialTheme.typography.titleMedium
                    )

                    val walkerName = walk.walker?.name ?: "Sin paseador"
                    val petName = walk.pet?.name ?: "Mascota #${walk.petId}"

                    Text("Paseador: $walkerName")
                    Text("Mascota: $petName")
                    Text("Estado: ${walk.status}")
                }
            }
        }
    }
}
