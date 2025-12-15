package com.example.mascota.screens.walker.walks

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer

@Composable
fun WalkerWalksScreen(nav: NavHostController, container: AppContainer) {
    var tab by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Pendientes") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Aceptados") })
            Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text("Historial") })
        }

        Spacer(Modifier.height(8.dp))

        when (tab) {
            0 -> PendingWalksScreen(nav, container)
            1 -> AcceptedWalksScreen(nav, container)
            2 -> WalkHistoryScreen(nav, container)
        }
    }
}
