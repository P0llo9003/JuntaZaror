package com.duoc.juntazaror.data.local.entity

import androidx.room.Embedded

data class ReservaConSolicitante(
    @Embedded val reserva: Reserva,
    val solicitante: String
)
