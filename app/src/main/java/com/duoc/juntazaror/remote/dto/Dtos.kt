package com.duoc.juntazaror.remote.dto

import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.model.EstadoReserva
import com.duoc.juntazaror.model.BloqueHorario
import com.duoc.juntazaror.model.TipoMovimiento


data class LoginRequest(val email: String, val password: String)

data class RegistroRequest(
    val name: String,
    val email: String,
    val password: String,
    val phonenumber: String? = null
    //ya no recibe el campo rol.
)

data class CuentaDto(
    val id: Long,
    val name: String,
    val email: String,
    val rol: Rol,
    val phonenumber: String?,
    val createdAt: String?
)

data class ReservaRequest(
    val cuentaId: Long,
    val fecha: String,             // formato "YYYY-MM-DD"
    val bloque: BloqueHorario,
    val motivo: String,
    val cantidadPersonas: Int
)

data class ReservaDto(
    val id: Long,
    val cuentaId: Long,
    val fecha: String,
    val bloque: BloqueHorario,
    val bloqueEtiqueta: String?,
    val motivo: String,
    val cantidadPersonas: Int,
    val estado: EstadoReserva,
    val resueltaPorId: Long?,
    val creadaEn: String?,
    val solicitante: String?
)

data class BloqueDto(val bloque: BloqueHorario, val etiqueta: String, val disponible: Boolean)

data class MovimientoRequest(
    val tipo: TipoMovimiento,
    val concepto: String,
    val monto: Int,
    val fecha: String,             // formato "YYYY-MM-DD"
    val comprobanteUri: String? = null,
    val registradoPorId: Long
)

data class MovimientoDto(
    val id: Long,
    val tipo: TipoMovimiento,
    val concepto: String,
    val monto: Int,
    val fecha: String,
    val comprobanteUri: String?,
    val registradoPorId: Long,
    val creadoEn: String?
)

data class ResumenFinancieroDto(
    val ingresos: Long,
    val gastos: Long,
    val saldo: Long,
    val cantidadMovimientos: Long
)

data class ApiError(
    val timestamp: String?,
    val status: Int,
    val error: String?,
    val message: String?,
    val path: String?
)
