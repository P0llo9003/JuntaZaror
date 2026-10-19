package com.duoc.juntazaror.notificaciones

import android.app.Application
import com.duoc.juntazaror.data.local.AppDatabase
import com.duoc.juntazaror.data.repository.ReservaRepository
import com.duoc.juntazaror.model.EstadoReserva
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.model.UsuarioSesion
import com.duoc.juntazaror.ui.screens.formatearFecha

class NotificadorReservas(private val app: Application) {

    private val repo = ReservaRepository(AppDatabase.getInstance(app).reservaDao())
    private val store = NotificadasStore(app)

    suspend fun escuchar(usuario: UsuarioSesion) {
        when (usuario.rol) {

            Rol.DIRECTIVA -> repo.conSolicitante.collect { lista ->
                lista.filter { it.reserva.estado == EstadoReserva.PENDIENTE }.forEach { item ->
                    val r = item.reserva
                    notificarUnaVez(
                        clave = "${usuario.id}|${r.id}|${r.estado}",
                        canal = Notificaciones.CANAL_NUEVAS,
                        id = r.id,
                        titulo = "Nueva solicitud de arriendo",
                        texto = "${item.solicitante} solicita el ${formatearFecha(r.fecha)} " +
                                "(${r.bloque.etiqueta}) para ${r.cantidadPersonas} personas. " +
                                "Estado: pendiente de revision."
                    )
                }
            }

            Rol.VECINO -> repo.delUsuario(usuario.id).collect { lista ->
                lista.filter { it.estado != EstadoReserva.PENDIENTE }.forEach { r ->
                    val aprobada = r.estado == EstadoReserva.CONFIRMADA
                    notificarUnaVez(
                        clave = "${usuario.id}|${r.id}|${r.estado}",
                        canal = Notificaciones.CANAL_RESOLUCIONES,
                        id = r.id + 1_000_000,
                        titulo = if (aprobada) "Solicitud de arriendo aprobada" else "Solicitud de arriendo rechazada",
                        texto = "Tu solicitud para el ${formatearFecha(r.fecha)} (${r.bloque.etiqueta}) " +
                                "fue ${if (aprobada) "aprobada" else "rechazada"}. " +
                                "Estado: ${if (aprobada) "Confirmada" else "Rechazada"}."
                    )
                }
            }

            Rol.TESORERIA -> Unit
            //TODO En un futuro, implementar notificaciones para rol tesoreria, vamos que se puedeeeee
        }
    }

    private fun notificarUnaVez(clave: String, canal: String, id: Int, titulo: String, texto: String) {
        if (store.yaNotificada(clave)) return
        if (Notificaciones.mostrar(app, canal, id, titulo, texto)) store.marcar(clave)
    }
}