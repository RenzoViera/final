package com.example.mascota.screens.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.mascota.AppContainer
import com.example.mascota.Routes
import com.example.mascota.data.model.UserRole

@Composable
fun RoleSelectScreen(nav: NavHostController, container: AppContainer) {
    val vm = remember { container.roleViewModel }


    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Elige qué app usar", style = MaterialTheme.typography.titleLarge)

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                vm.selectRole(UserRole.CLIENT)
                nav.navigate(Routes.LOGIN)
            }
        ) { Text("Dueño de mascota") }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                vm.selectRole(UserRole.WALKER)
                nav.navigate(Routes.LOGIN)
            }
        ) { Text("Paseador") }
    }
}
