package com.duoc.juntazaror.remote

import com.duoc.juntazaror.remote.dto.ApiError
import com.google.gson.Gson
import retrofit2.HttpException
import java.io.IOException


class ApiException(val code: Int, override val message: String) : Exception(message)

private val gson = Gson()

suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> = try {
    Result.success(call())
} catch (e: HttpException) {
    val mensaje = e.response()?.errorBody()?.string()
        ?.let { json -> runCatching { gson.fromJson(json, ApiError::class.java).message }.getOrNull() }
        ?: "Error ${e.code()}"
    Result.failure(ApiException(e.code(), mensaje))
} catch (e: IOException) {
    Result.failure(ApiException(0, "No se pudo conectar con el servidor"))
}