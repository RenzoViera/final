package com.example.mascota.screens.client.walks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.data.model.ReviewCreateRequest
import kotlinx.coroutines.launch

@Composable
fun WalkReviewScreen(nav: NavHostController, container: AppContainer, walkId: Long) {
    val scope = rememberCoroutineScope()

    // ⭐ rating 0..5 (0 = no seleccionado)
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Calificar paseador", style = MaterialTheme.typography.titleLarge)
        Text("Paseo #$walkId")

        Text("Tu calificación:", style = MaterialTheme.typography.titleMedium)

        StarsPicker(
            rating = rating,
            onChange = { rating = it }
        )

        Text(
            text = if (rating == 0) "Selecciona de 1 a 5 estrellas" else "Elegiste: $rating/5",
            style = MaterialTheme.typography.bodySmall
        )

        OutlinedTextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Comentario (ej: Llegó a tiempo)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 6
        )

        if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
        if (success) Text("✅ Calificación enviada", color = MaterialTheme.colorScheme.primary)

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading && rating in 1..5,
            onClick = {
                scope.launch {
                    loading = true
                    error = null
                    success = false
                    try {
                        val resp = container.api.createReviewRaw(
                            id = walkId,
                            body = ReviewCreateRequest(
                                rating = rating,
                                comment = comment.trim().ifBlank { null }
                            )
                        )
                        if (resp.isSuccessful) {
                            success = true
                            nav.popBackStack() // vuelve al detalle
                        } else {
                            error = "Error ${resp.code()}: ${resp.errorBody()?.string() ?: "Sin body"}"
                        }
                    } catch (e: Exception) {
                        error = e.message ?: "Error enviando review"
                    } finally {
                        loading = false
                    }
                }
            }
        ) { Text(if (loading) "Enviando..." else "Enviar calificación") }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.popBackStack() }
        ) { Text("Volver") }
    }
}

@Composable
private fun StarsPicker(
    rating: Int,
    onChange: (Int) -> Unit
) {
    // 0..5 (0 = sin estrellas)
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {

        // botón para poner 0 (quitar selección)
        Text(
            text = "0",
            modifier = Modifier
                .clickable { onChange(0) }
                .padding(6.dp),
            style = MaterialTheme.typography.bodyMedium
        )

        for (i in 1..5) {
            val filled = i <= rating
            Text(
                text = if (filled) "★" else "☆",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .clickable { onChange(i) }
                    .padding(2.dp)
            )
        }
    }
}
