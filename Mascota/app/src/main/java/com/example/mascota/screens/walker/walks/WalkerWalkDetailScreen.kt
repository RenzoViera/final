package com.example.mascota.screens.walker.walks

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.viewmodel.walker.AcceptedWalksViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@Composable
fun WalkerWalkDetailScreen(nav: NavHostController, container: AppContainer, walkId: Long) {

    val vm = remember { AcceptedWalksViewModel(container.repo) }
    val ui by vm.detail.collectAsState()

    LaunchedEffect(walkId) { vm.loadDetail(walkId) }

    val ctx = LocalContext.current

    // Picker de imagen
    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val part = uriToPart(ctx, uri, "photo")
            if (part != null) vm.uploadPhoto(walkId, part)
        }
    }

    val walk = ui.walk
    val pet = walk?.pet
    val address = walk?.address

    fun isInProgress(status: String): Boolean {
        val s = status.lowercase().trim()
        return s == "in_progress" || s == "ongoing" || s == "started"
    }

    fun isAcceptedNotStarted(status: String): Boolean {
        val s = status.lowercase().trim()
        return s == "accepted" || s == "pending_start"
    }

    fun isFinished(status: String): Boolean {
        val s = status.lowercase().trim()
        return s == "finished" || s == "finalized" || s == "completed" || s == "ended" || s == "done"
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Detalle paseo", style = MaterialTheme.typography.titleLarge)

        ui.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            TextButton(onClick = { vm.clearError() }) { Text("Ocultar") }
        }

        if (ui.isLoading || walk == null) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            return
        }

        // ====== Info base ======
        Text("Paseo #${walk.id}", style = MaterialTheme.typography.titleMedium)

        Text("🐾 Mascota: ${pet?.name ?: "Mascota"}")
        Text("Tipo: ${pet?.species ?: "—"}")
        if (!pet?.notes.isNullOrBlank()) Text("Notas mascota: ${pet?.notes}")

        Spacer(Modifier.height(6.dp))

        if (!walk.notes.isNullOrBlank()) Text("📝 Notas del dueño: ${walk.notes}")
        Text("📅 ${walk.scheduledAt}")
        Text("⏱ ${walk.durationMinutes} min")
        Text("Estado: ${walk.status}")

        // Dirección (si existe)
        address?.let {
            Spacer(Modifier.height(6.dp))
            Text("📍 Dirección: ${it.label} - ${it.address}")
        }

        Spacer(Modifier.height(10.dp))

        // ====== Acciones según estado ======
        when {
            // ✅ FINALIZADO → SOLO LECTURA
            isFinished(walk.status) -> {
                Text("Historial (solo lectura)", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(8.dp))
                Text("📷 Fotos del paseo:", style = MaterialTheme.typography.titleSmall)

                if (walk.photos.isEmpty()) {
                    Text("No hay fotos.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 220.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(walk.photos) { p ->
                            val url = extractPhotoUrl(p)
                            Text("• ${url ?: p.toString()}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Text("⭐ Review del cliente:", style = MaterialTheme.typography.titleSmall)

                val reviewEl = walk.review

                when {
                    reviewEl == null || reviewEl.isJsonNull -> {
                        Text("No hay review.")
                    }

                    reviewEl.isJsonObject -> {
                        val obj = reviewEl.asJsonObject

                        val rating = obj.get("rating")?.asString
                            ?: obj.get("score")?.asString

                        val comment = obj.get("comment")?.asString
                            ?: obj.get("notes")?.asString
                            ?: obj.get("message")?.asString

                        Text("Puntuación: ${rating ?: "—"}")

                        if (!comment.isNullOrBlank()) {
                            Text("Comentario: $comment")
                        }
                    }

                    else -> {
                        Text("Review no compatible.")
                    }
                }

            }

            // ✅ ACEPTADO (no iniciado)
            isAcceptedNotStarted(walk.status) -> {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { vm.start(walkId) }
                ) { Text("▶️ Iniciar paseo") }
            }

            // ✅ EN CURSO
            isInProgress(walk.status) -> {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { photoPicker.launch("image/*") }
                ) { Text("📷 Subir foto") }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { vm.end(walkId) }
                ) { Text("🏁 Finalizar paseo") }
            }

            else -> {
                // Otros estados (por si backend devuelve algo nuevo)
                Text("Este paseo no tiene acciones disponibles en este estado.")
            }
        }
    }
}

private fun uriToPart(context: Context, uri: Uri, fieldName: String): MultipartBody.Part? {
    return try {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        val req = bytes.toRequestBody("image/*".toMediaType())
        MultipartBody.Part.createFormData(fieldName, "walk_photo.jpg", req)
    } catch (_: Exception) {
        null
    }
}

private fun extractPhotoUrl(p: com.google.gson.JsonElement): String? {
    return try {
        when {
            p.isJsonPrimitive && p.asJsonPrimitive.isString -> p.asString
            p.isJsonObject -> {
                val o = p.asJsonObject
                when {
                    o.has("url") -> o.get("url").asString
                    o.has("photoUrl") -> o.get("photoUrl").asString
                    o.has("path") -> o.get("path").asString
                    else -> null
                }
            }
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}
