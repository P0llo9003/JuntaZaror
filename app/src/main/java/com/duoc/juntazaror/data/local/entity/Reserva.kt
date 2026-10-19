package com.duoc.juntazaror.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.duoc.juntazaror.model.BloqueHorario
import com.duoc.juntazaror.model.EstadoReserva


@Entity(
    tableName = "reservas",
    foreignKeys = [
        ForeignKey( entity = Cuenta::class, parentColumns = ["id"], childColumns = ["cuentaId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("cuentaId"), Index(value = ["fecha", "bloque"])]
)


data class Reserva(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cuentaId: Int,
    val fecha: String, // "YYYY-MM-DD"
    val bloque: BloqueHorario,
    val motivo: String,
    val cantidadPersonas: Int,
    val estado: EstadoReserva = EstadoReserva.PENDIENTE,
    val creadaEn: Long = System.currentTimeMillis()
)