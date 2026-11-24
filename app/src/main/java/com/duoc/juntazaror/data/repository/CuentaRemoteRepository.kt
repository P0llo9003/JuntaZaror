package com.duoc.juntazaror.data.repository

import com.duoc.juntazaror.remote.ApiService
import com.duoc.juntazaror.remote.RetrofitInstance
import com.duoc.juntazaror.remote.dto.CuentaDto
import com.duoc.juntazaror.remote.dto.LoginRequest
import com.duoc.juntazaror.remote.dto.RegistroRequest
import com.duoc.juntazaror.remote.safeApiCall

class CuentaRemoteRepository(
    private val api: ApiService = RetrofitInstance.api
) {

    suspend fun login(email: String, password: String): Result<CuentaDto> =
        safeApiCall { api.login(LoginRequest(email.trim().lowercase(), password)) }


    suspend fun registrar(
        nombre: String,
        email: String,
        clave: String,
        telefono: String? = null
    ): Result<CuentaDto> =
        safeApiCall {
            api.registrar(
                RegistroRequest(
                    name = nombre.trim(),
                    email = email.trim().lowercase(),
                    password = clave,
                    phonenumber = telefono?.takeIf { it.isNotBlank() }
                )
            )
        }
// funcion listar y eliminar por id
    suspend fun listar(): Result<List<CuentaDto>> = safeApiCall { api.listarCuentas() }

    suspend fun eliminar(id: Long): Result<Unit> = safeApiCall { api.eliminarCuenta(id) }
}
