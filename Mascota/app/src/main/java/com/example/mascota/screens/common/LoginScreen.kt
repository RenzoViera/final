package com.example.mascota.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import com.example.mascota.data.model.UserRole
import com.example.mascota.util.Resource
import kotlinx.coroutines.flow.first

@Composable
fun LoginScreen(nav: NavHostController, container: AppContainer) {

    // ✅ ahora es propiedad, NO función
    val vm = remember { container.authViewModel }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf<UserRole?>(null) }

    val state by vm.state.collectAsState()

    // leer role guardado
    LaunchedEffect(Unit) {
        val roleStr = container.sessionStore.roleFlow.first()
        role = roleStr?.let { UserRole.valueOf(it) }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Login", style = MaterialTheme.typography.titleLarge)
        Text("Rol actual: ${role?.name ?: "No seleccionado"}")

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

        if (state is Resource.Loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val r = role ?: return@Button
                vm.login(email, password, r) {
                    val dest = if (r == UserRole.CLIENT) Routes.CLIENT_HOME else Routes.WALKER_HOME
                    nav.navigate(dest) {
                        popUpTo(Routes.ROLE) { inclusive = true }
                    }
                }
            },
            enabled = role != null && state !is Resource.Loading
        ) {
            Text("Ingresar")
        }

        val err = (state as? Resource.Error)?.message
        if (!err.isNullOrBlank()) {
            Text(err, color = MaterialTheme.colorScheme.error)
        }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.navigate(Routes.ROLE) }
        ) { Text("Volver a elegir rol") }

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { nav.navigate(Routes.REGISTER) }
        ) { Text("Registrarse") }
    }
}
