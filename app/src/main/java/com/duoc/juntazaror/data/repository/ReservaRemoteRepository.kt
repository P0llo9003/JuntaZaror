package com.duoc.juntazaror.data.repository

import com.duoc.juntazaror.model.BloqueHorario
import com.duoc.juntazaror.remote.ApiService
import com.duoc.juntazaror.remote.RetrofitInstance
import com.duoc.juntazaror.remote.dto.ReservaDto
import com.duoc.juntazaror.remote.dto.ReservaRequest
import com.duoc.juntazaror.remote.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReservaRemoteRepository(
    private val api: ApiService = RetrofitInstance.api
) {

    suspend fun delUsuario(cuentaId: Long): Result<List<ReservaDto>> =
        safeApiCall { api.reservasDeCuenta(cuentaId) }

    suspend fun solicitudes(): Result<List<ReservaDto>> = safeApiCall { api.solicitudes() }

    fun bloquesOcupados(fecha: String): Flow<List<BloqueHorario>> = flow {
        val ocupados = safeApiCall { api.disponibilidad(fecha) }
            .getOrDefault(emptyList())
            .filter { !it.disponible }
            .map { it.bloque }
        emit(ocupados)
    }

    suspend fun estaDisponible(fecha: String, bloque: BloqueHorario): Result<Boolean> =
        safeApiCall { api.disponible(fecha, bloque) }

    suspend fun solicitar(
        cuentaId: Long,
        fecha: String,
        bloque: BloqueHorario,
        motivo: String,
        cantidadPersonas: Int
    ): Result<ReservaDto> =
        safeApiCall {
            api.crearReserva(ReservaRequest(cuentaId, fecha, bloque, motivo.trim(), cantidadPersonas))
        }

    suspend fun aprobar(id: Long, resolutorId: Long): Result<ReservaDto> =
        safeApiCall { api.aprobar(id, resolutorId) }

    suspend fun rechazar(id: Long, resolutorId: Long): Result<ReservaDto> =
        safeApiCall { api.rechazar(id, resolutorId) }
}