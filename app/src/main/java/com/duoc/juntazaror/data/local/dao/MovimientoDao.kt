package com.duoc.juntazaror.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duoc.juntazaror.data.local.entity.Movimiento
import com.duoc.juntazaror.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(movimiento: Movimiento)

    @Query("SELECT * FROM movimientos ORDER BY fecha DESC, id DESC")
    fun observarTodos(): Flow<List<Movimiento>>

    @Query("SELECT COALESCE(SUM(monto), 0) FROM movimientos WHERE tipo = :tipo")
    fun observarTotal(tipo: TipoMovimiento): Flow<Int>
}