package com.duoc.juntazaror.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.duoc.juntazaror.data.local.entity.Cuenta
import kotlinx.coroutines.flow.Flow

@Dao
interface CuentaDao {

    @Query("SELECT * FROM cuentas ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Cuenta>>

    @Query("SELECT * FROM cuentas WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Cuenta?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(cuenta: Cuenta)

    @Delete
    suspend fun delete(cuenta: Cuenta)

}