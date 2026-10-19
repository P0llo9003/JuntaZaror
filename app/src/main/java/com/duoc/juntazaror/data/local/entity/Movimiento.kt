package com.duoc.juntazaror.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.duoc.juntazaror.model.TipoMovimiento

@Entity(tableName = "movimientos")
data class Movimiento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: TipoMovimiento,
    val concepto: String,
    val monto: Int,
    val fecha: String,
    val comprobanteUri: String? = null,
    val creadoEn: Long = System.currentTimeMillis()
)