package com.duoc.juntazaror.remote

import com.duoc.juntazaror.model.BloqueHorario
import com.duoc.juntazaror.model.EstadoReserva
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.model.TipoMovimiento
import com.duoc.juntazaror.remote.dto.*
import retrofit2.http.*

interface ApiService {

    @POST("cuentas/login")
    suspend fun login(@Body body: LoginRequest): CuentaDto

    @POST("cuentas/registro")
    suspend fun registrar(@Body body: RegistroRequest): CuentaDto

    @GET("cuentas")
    suspend fun listarCuentas(@Query("rol") rol: Rol? = null): List<CuentaDto>

    @DELETE("cuentas/{id}")
    suspend fun eliminarCuenta(@Path("id") id: Long)


    @GET("reservas/cuenta/{cuentaId}")
    suspend fun reservasDeCuenta(@Path("cuentaId") cuentaId: Long): List<ReservaDto>

    @GET("reservas/solicitudes")
    suspend fun solicitudes(): List<ReservaDto>


    @GET("reservas/disponibilidad")
    suspend fun disponibilidad(@Query("fecha") fecha: String): List<BloqueDto>

    @GET("reservas/disponible")
    suspend fun disponible(@Query("fecha") fecha: String, @Query("bloque") bloque: BloqueHorario): Boolean

    @POST("reservas")
    suspend fun crearReserva(@Body body: ReservaRequest): ReservaDto

    @PUT("reservas/{id}/aprobar")
    suspend fun aprobar(@Path("id") id: Long, @Query("resolutorId") resolutorId: Long): ReservaDto

    @PUT("reservas/{id}/rechazar")
    suspend fun rechazar(@Path("id") id: Long, @Query("resolutorId") resolutorId: Long): ReservaDto


    @GET("movimientos")
    suspend fun movimientos(
        @Query("tipo") tipo: TipoMovimiento? = null,
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null
    ): List<MovimientoDto>

    @GET("movimientos/resumen")
    suspend fun resumen(
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null
    ): ResumenFinancieroDto

    @POST("movimientos")
    suspend fun crearMovimiento(@Body body: MovimientoRequest): MovimientoDto

}
