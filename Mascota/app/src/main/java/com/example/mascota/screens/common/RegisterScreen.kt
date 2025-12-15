package com.example.mascota.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import com.example.mascota.data.model.UserRole
import com.example.mascota.util.Resource

@Composable
fun RegisterScreen(nav: NavHostController, container: AppContainer) {

    // ✅ ahora es propiedad, NO función
    val vm = remember { container.authViewModel }

    // UI state
    var role by remember { mutableStateOf(UserRole.CLIENT) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }

    // solo para walker
    var priceHour by remember { mutableStateOf("") }

    val state by vm.state.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Registro", style = MaterialTheme.typography.titleLarge)

        // Selector de rol (cliente/paseador)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = role == UserRole.CLIENT,
                onClick = { role = UserRole.CLIENT },
                label = { Text("Dueño") }
            )
            FilterChip(
                selected = role == UserRole.WALKER,
                onClick = { role = UserRole.WALKER },
                label = { Text("Paseador") }
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = photoUrl,
            onValueChange = { photoUrl = it },
            label = { Text("Photo URL (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (role == UserRole.WALKER) {
            OutlinedTextField(
                value = priceHour,
                onValueChange = { priceHour = it },
                label = { Text("Precio por hora") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        if (state is Resource.Loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is Resource.Loading,
            onClick = {
                val pUrl = photoUrl.trim().ifBlank { null }

                if (role == UserRole.CLIENT) {
                    vm.registerClient(
                        name = name.trim(),
                        email = email.trim(),
                        password = password,
                        photoUrl = pUrl
                    ) {
                        // al terminar, vuelve a login
                        nav.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                } else {
                    vm.registerWalker(
                        name = name.trim(),
                        email = email.trim(),
                        password = password,
                        photoUrl = pUrl,
                        priceHour = priceHour.trim()
                    ) {
                        nav.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    }
                }
            }
        ) {
            Text("Crear cuenta")
        }

        val err = (state as? Resource.Error)?.message
        if (!err.isNullOrBlank()) {
            Text(err, color = MaterialTheme.colorScheme.error)
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.popBackStack() }
        ) { Text("Volver") }
    }
}
