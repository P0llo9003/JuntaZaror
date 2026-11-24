package com.duoc.juntazaror.data.repository

import com.duoc.juntazaror.remote.externa.FeriadoDto
import com.duoc.juntazaror.remote.externa.FeriadosApi
import com.duoc.juntazaror.remote.externa.FeriadosRetrofit
import com.duoc.juntazaror.remote.safeApiCall

class FeriadoRepository(
    private val api: FeriadosApi = FeriadosRetrofit.api
) {
    private val cache = mutableMapOf<Int, List<FeriadoDto>>()

    suspend fun feriadosDe(anio: Int): Result<List<FeriadoDto>> {
        cache[anio]?.let { return Result.success(it) }
        return safeApiCall { api.feriados(anio, "CL") }
            .map { lista -> lista.filter { it.global != false } }
            .onSuccess { cache[anio] = it }
    }
}