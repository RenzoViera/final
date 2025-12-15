package com.example.mascota.viewmodel.walker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mascota.data.repo.Repository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WalkerLocationViewModel(
    private val repo: Repository
) : ViewModel() {

    private var job: Job? = null

    fun startSending(getLatLng: suspend () -> Pair<Double, Double>?) {
        if (job != null) return

        job = viewModelScope.launch {
            while (true) {
                try {
                    val latLng = getLatLng()
                    if (latLng != null) {
                        val (lat, lng) = latLng
                        repo.postWalkerLocation(
                            latitude = lat.toString(),
                            longitude = lng.toString()
                        )
                    }
                } catch (_: Exception) {
                    // si falla, no revientes la app; se reintenta en el siguiente ciclo
                }

                // cada 5 minutos
                delay(5 * 60 * 1000L)
            }
        }
    }

    fun stopSending() {
        job?.cancel()
        job = null
    }

    override fun onCleared() {
        stopSending()
        super.onCleared()
    }
}
