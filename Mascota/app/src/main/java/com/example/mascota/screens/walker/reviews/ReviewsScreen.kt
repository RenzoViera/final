package com.example.mascota.screens.walker.reviews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import com.example.mascota.viewmodel.walker.ReviewsViewModel

@Composable
fun ReviewsScreen(nav: NavHostController, container: AppContainer) {

    val vm = remember { ReviewsViewModel(container.repo) }
    val state by vm.uiState.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Mis Reviews", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { vm.load() }) { Text("Recargar") }
        }

        Spacer(Modifier.height(8.dp))

        if (state.isLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(12.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.items) { r ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { nav.navigate("${Routes.WALKER_REVIEW_DETAIL}/${r.id}") }
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {

                        // Estrellas + rating
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StarsRow(r.rating)
                            Text("(${r.rating}/5)", style = MaterialTheme.typography.bodyMedium)
                        }

                        // Comentario
                        Text(
                            text = r.comment ?: "Sin comentario",
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Datos extra (del walk embebido si viene)
                        val scheduled = r.walk?.scheduledAt
                        val duration = r.walk?.durationMinutes

                        if (!scheduled.isNullOrBlank() || duration != null) {
                            Text(
                                text = buildString {
                                    if (!scheduled.isNullOrBlank()) append("📅 $scheduled  ")
                                    if (duration != null) append("⏱️ ${duration} min")
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Text(
                            text = "🕒 ${r.createdAt ?: "—"}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = { nav.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

@Composable
private fun StarsRow(rating: Int) {
    val safe = rating.coerceIn(0, 5)
    val stars = "★".repeat(safe) + "☆".repeat(5 - safe)
    Text(stars, style = MaterialTheme.typography.titleMedium)
}
