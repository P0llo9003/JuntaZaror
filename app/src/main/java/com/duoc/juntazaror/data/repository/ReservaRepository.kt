package com.duoc.juntazaror.data.repository

import com.duoc.juntazaror.data.local.dao.ReservaDao
import com.duoc.juntazaror.data.local.entity.Reserva
import com.duoc.juntazaror.data.local.entity.ReservaConSolicitante
import com.duoc.juntazaror.model.BloqueHorario
import com.duoc.juntazaror.model.EstadoReserva
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.model.UsuarioSesion
import kotlinx.coroutines.flow.Flow

class ReservaRepository(private val dao: ReservaDao) {

    val todas: Flow<List<Reserva>> = dao.observarTodas()

    val conSolicitante: Flow<List<ReservaConSolicitante>> = dao.observarConSolicitante()

    fun delUsuario(cuentaId: Int): Flow<List<Reserva>> = dao.observarPorCuenta(cuentaId)

    suspend fun estaDisponible(fecha: String, bloque: BloqueHorario): Boolean = dao.contarOcupadas(fecha, bloque.name) == 0

    suspend fun solicitar(reserva: Reserva) {
        dao.insert(reserva)
    }

    suspend fun aprobar(id: Int, resolutor: UsuarioSesion) {
        exigirDirectiva(resolutor)
        dao.cambiarEstado(id, EstadoReserva.CONFIRMADA)
    }

    suspend fun rechazar(id: Int, resolutor: UsuarioSesion) {
        exigirDirectiva(resolutor)
        dao.cambiarEstado(id, EstadoReserva.RECHAZADA)
    }

    private fun exigirDirectiva(usuario: UsuarioSesion) =
        require(usuario.rol == Rol.DIRECTIVA) { "Solo la directiva puede resolver solicitudes" }

    fun bloquesOcupados(fecha: String): Flow<List<BloqueHorario>> = dao.observarBloquesOcupados(fecha)
}