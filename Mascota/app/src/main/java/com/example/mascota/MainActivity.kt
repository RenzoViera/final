package com.example.mascota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.mascota.ui.theme.MascotaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = AppContainer(applicationContext)

        setContent {
            MascotaTheme {
                val nav = rememberNavController()
                AppNavHost(nav = nav, container = container)
            }
        }
    }
}
