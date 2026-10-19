package com.duoc.juntazaror.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duoc.juntazaror.data.local.entity.Reserva
import com.duoc.juntazaror.data.local.entity.ReservaConSolicitante
import com.duoc.juntazaror.model.EstadoReserva
import kotlinx.coroutines.flow.Flow


@Dao
interface ReservaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reserva: Reserva)


    @Query(
        "SELECT COUNT(*) FROM reservas " +
                "WHERE fecha = :fecha AND bloque = :bloque AND estado <> 'RECHAZADA'"
    )
    suspend fun contarOcupadas(fecha: String, bloque: String): Int

    @Query("SELECT * FROM reservas WHERE cuentaId = :cuentaId ORDER BY fecha DESC")
    fun observarPorCuenta(cuentaId: Int): Flow<List<Reserva>>

    @Query("SELECT * FROM reservas ORDER BY fecha DESC ")
    fun observarTodas(): Flow<List<Reserva>>

    @Query(
        """
    SELECT r.*, c.name AS solicitante
    FROM reservas r
    INNER JOIN cuentas c ON c.id = r.cuentaId
    ORDER BY CASE r.estado WHEN 'PENDIENTE' THEN 0 ELSE 1 END, r.fecha ASC
    """
    )
    fun observarConSolicitante(): Flow<List<ReservaConSolicitante>>

    @Query("UPDATE reservas SET estado = :estado WHERE id = :id AND estado = 'PENDIENTE'")
    suspend fun cambiarEstado(id: Int, estado: EstadoReserva)
}