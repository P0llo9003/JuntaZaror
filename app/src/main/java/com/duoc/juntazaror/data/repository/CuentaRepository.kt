package com.duoc.juntazaror.data.repository

import com.duoc.juntazaror.data.local.dao.CuentaDao
import com.duoc.juntazaror.data.local.entity.Cuenta
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.util.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CuentaRepository(private val dao: CuentaDao) {
    val cuentas: Flow<List<Cuenta>> = dao.observeAll()

    suspend fun buscarPorEmail(email: String): Cuenta? =
        dao.buscarPorEmail(email.trim().lowercase())

    suspend fun save(name: String, email: String, password: String, rol: Rol) {
        val claveHash = withContext(Dispatchers.Default) { PasswordHasher.hash(password) }
        dao.insert(
            Cuenta(
                name = name.trim(),
                email = email.trim().lowercase(),
                password = claveHash,
                rol = rol
            )
        )
    }

    suspend fun delete(cuenta: Cuenta) {
        dao.delete(cuenta)
    }
}