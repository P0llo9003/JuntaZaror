package com.duoc.juntazaror.data.repository

import com.duoc.juntazaror.model.TipoMovimiento
import com.duoc.juntazaror.remote.RetrofitInstance
import com.duoc.juntazaror.remote.dto.MovimientoDto
import com.duoc.juntazaror.remote.dto.MovimientoRequest
import com.duoc.juntazaror.remote.dto.ResumenFinancieroDto
import com.duoc.juntazaror.remote.safeApiCall

class MovimientoRemoteRepository {
    private val api = RetrofitInstance.api

    suspend fun listar(): Result<List<MovimientoDto>> = safeApiCall { api.movimientos() }

    suspend fun resumen(): Result<ResumenFinancieroDto> = safeApiCall { api.resumen() }

    suspend fun registrar(
        tipo: TipoMovimiento,
        concepto: String,
        monto: Int,
        fecha: String,
        comprobanteUri: String?,
        registradoPorId: Long
    ): Result<MovimientoDto> =
        safeApiCall {
            api.crearMovimiento(
                MovimientoRequest(tipo, concepto.trim(), monto, fecha, comprobanteUri, registradoPorId)
            )
        }
}
