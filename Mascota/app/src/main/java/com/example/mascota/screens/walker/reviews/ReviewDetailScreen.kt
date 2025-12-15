package com.example.mascota.screens.walker.reviews

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.viewmodel.walker.ReviewDetailViewModel

@Composable
fun ReviewDetailScreen(nav: NavHostController, container: AppContainer, reviewId: Long) {

    val vm = remember { ReviewDetailViewModel(container.repo) }
    val state by vm.uiState.collectAsState()

    LaunchedEffect(reviewId) {
        vm.load(reviewId)
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Detalle Review", style = MaterialTheme.typography.titleLarge)

        if (state.isLoading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        val review = state.review
        val walk = state.walk

        if (review != null) {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("⭐ Puntuación", style = MaterialTheme.typography.titleMedium)
                    Text("★".repeat(review.rating.coerceIn(0, 5)) + "☆".repeat(5 - review.rating.coerceIn(0, 5)))

                    Text("💬 Comentario", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = review.comment ?: "Sin comentario",
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text("🕒 Fecha: ${review.createdAt ?: "—"}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (walk != null) {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("🐾 Paseo", style = MaterialTheme.typography.titleMedium)
                    Text("Estado: ${walk.status}")
                    Text("📅 ${walk.scheduledAt}   ⏱️ ${walk.durationMinutes} min")

                    val petName = walk.pet?.name
                    val petSpecies = walk.pet?.species
                    val petPhoto = walk.pet?.photoUrl

                    if (!petName.isNullOrBlank() || !petSpecies.isNullOrBlank()) {
                        Text("🐶 Mascota: ${petName ?: "—"} (${petSpecies ?: "—"})")
                    }

                    if (!petPhoto.isNullOrBlank() && petPhoto != "null") {
                        Text("📷 Foto mascota: $petPhoto", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(onClick = { nav.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}
