package com.example.mascota.data.model

object WalkStatus {
    const val PENDING = "Pendiente"
    const val ACCEPTED = "Aceptado"
    const val REJECTED = "Rechazado"
    const val IN_PROGRESS = "En curso"
    const val FINISHED = "Finalizado"
}

fun String.normStatus(): String = this.trim()
