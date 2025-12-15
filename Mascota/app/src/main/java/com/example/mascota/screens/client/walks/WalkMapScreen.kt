package com.example.mascota.screens.client.walks

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer

@Composable
fun WalkMapScreen(
    nav: NavHostController,
    container: AppContainer,
    walkId: Long
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Mapa del paseo", style = MaterialTheme.typography.titleLarge)
        Text("Walk ID: $walkId")

        // TODO: aquí luego metemos el mapa real y el polling de GET /walks/{id}

        Button(onClick = { nav.popBackStack() }) { Text("Volver") }
    }
}
