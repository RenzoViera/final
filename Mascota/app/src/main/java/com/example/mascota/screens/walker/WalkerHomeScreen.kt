package com.example.mascota.screens.walker

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import com.example.mascota.viewmodel.walker.WalkerHomeViewModel
import com.example.mascota.viewmodel.walker.WalkerLocationViewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun WalkerHomeScreen(nav: NavHostController, container: AppContainer) {

    Log.d("WALKER_HOME", "tokenSync len=${container.sessionStore.getTokenSync()?.length}")

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val availabilityVm = remember { WalkerHomeViewModel(container.repo) }
    val locationVm = remember { WalkerLocationViewModel(container.repo) }
    val state by availabilityVm.uiState.collectAsState()

    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }

    var hasLocationPerm by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPerm = granted
    }

    // ✅ Obtener última ubicación conocida (más simple y suficiente para “nearby”)
    suspend fun getLastLatLng(): Pair<Double, Double>? = suspendCoroutine { cont ->
        if (!hasLocationPerm) {
            cont.resume(null)
            return@suspendCoroutine
        }

        try {
            fused.lastLocation
                .addOnSuccessListener { loc ->
                    if (loc != null) cont.resume(loc.latitude to loc.longitude)
                    else cont.resume(null)
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        } catch (_: Exception) {
            cont.resume(null)
        }
    }

    // ✅ Envío automático: solo si está activo
    LaunchedEffect(state.isAvailable, hasLocationPerm) {
        if (state.isAvailable) {
            if (!hasLocationPerm) {
                permLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                locationVm.stopSending()
            } else {
                // envía inmediatamente y luego cada 5 min (lo maneja el VM)
                locationVm.startSending { getLastLatLng() }
            }
        } else {
            locationVm.stopSending()
        }
    }

    // ✅ cortar al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose { locationVm.stopSending() }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Inicio Paseador", style = MaterialTheme.typography.titleLarge)

        Card {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Disponibilidad", style = MaterialTheme.typography.titleMedium)

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(if (state.isAvailable) "Activo" else "Inactivo")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        Switch(
                            checked = state.isAvailable,
                            enabled = !state.isLoading,
                            onCheckedChange = { availabilityVm.setAvailability(it) }
                        )
                    }
                }

                state.error?.let { msg ->
                    Text(msg, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { availabilityVm.clearError() }) { Text("Ocultar") }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.navigate(Routes.WALKER_WALKS) }
        ) { Text("Mis paseos") }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.navigate(Routes.WALKER_REVIEWS) }
        ) { Text("Mis reviews") }

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            onClick = {
                scope.launch {
                    locationVm.stopSending()
                    container.sessionStore.clear()
                    nav.navigate(Routes.ROLE) {
                        popUpTo(Routes.WALKER_HOME) { inclusive = true }
                    }
                }
            }
        ) { Text("Cerrar sesión") }
    }
}
