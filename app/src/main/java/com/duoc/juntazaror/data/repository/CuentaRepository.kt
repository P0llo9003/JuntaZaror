package com.duoc.juntazaror.data.repository

import com.duoc.juntazaror.data.local.dao.CuentaDao
import com.duoc.juntazaror.data.local.entity.Cuenta
import com.duoc.juntazaror.model.Rol
import kotlinx.coroutines.flow.Flow

class CuentaRepository(private val dao: CuentaDao) {
    val cuentas: Flow<List<Cuenta>> = dao.observeAll()

    suspend fun buscarPorEmail(email: String): Cuenta? =
        dao.buscarPorEmail(email.trim().lowercase())

    suspend fun save(name: String, email: String, password: String, rol: Rol) {
        dao.insert(
            Cuenta(
                name = name.trim(),
                email = email.trim().lowercase(),
                password = password,
                rol = rol
            )
        )
    }

    suspend fun delete(cuenta: Cuenta) {
        dao.delete(cuenta)
    }
}