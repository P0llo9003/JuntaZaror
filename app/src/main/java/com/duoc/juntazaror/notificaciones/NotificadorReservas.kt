package com.duoc.juntazaror.notificaciones

import android.app.Application
import com.duoc.juntazaror.model.EstadoReserva
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.model.UsuarioSesion
import com.duoc.juntazaror.remote.RetrofitInstance
import com.duoc.juntazaror.remote.dto.ReservaDto
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import retrofit2.HttpException
import java.io.IOException

class NotificadorReservas(private val app: Application) {

    private val api = RetrofitInstance.api
    private val store = NotificadasStore(app)

    suspend fun escuchar(usuario: UsuarioSesion) {
        when (usuario.rol) {

            Rol.DIRECTIVA -> sondear {
                api.solicitudes()
                    .filter { it.estado == EstadoReserva.PENDIENTE }
                    .forEach { r ->
                        notificarUnaVez(
                            clave = "${usuario.id}|${r.id}|${r.estado}",
                            canal = Notificaciones.CANAL_NUEVAS,
                            id = r.id.toInt(),
                            titulo = "Nueva solicitud de arriendo",
                            texto = "${r.solicitante} solicita el ${fecha(r)} " +
                                    "(${r.bloqueEtiqueta}) para ${r.cantidadPersonas} personas. " +
                                    "Estado: pendiente de revision."
                        )
                    }
            }

            Rol.VECINO -> sondear {
                api.reservasDeCuenta(usuario.id.toLong())
                    .filter { it.estado != EstadoReserva.PENDIENTE }
                    .forEach { r ->
                        val aprobada = r.estado == EstadoReserva.CONFIRMADA
                        notificarUnaVez(
                            clave = "${usuario.id}|${r.id}|${r.estado}",
                            canal = Notificaciones.CANAL_RESOLUCIONES,
                            id = r.id.toInt() + 1_000_000,
                            titulo = if (aprobada) "Solicitud de arriendo aprobada" else "Solicitud de arriendo rechazada",
                            texto = "Tu solicitud para el ${fecha(r)} (${r.bloqueEtiqueta}) " +
                                    "fue ${if (aprobada) "aprobada" else "rechazada"}. " +
                                    "Estado: ${if (aprobada) "Confirmada" else "Rechazada"}."
                        )
                    }
            }

            Rol.TESORERIA -> Unit
        }
    }

    /** Ejecuta [bloque] cada [intervaloMs] hasta que se cancele la corrutina (cerrarSesion). */
    private suspend fun sondear(intervaloMs: Long = 30_000, bloque: suspend () -> Unit) {
        while (currentCoroutineContext().isActive) {
            try {
                bloque()
            } catch (_: IOException) {
                // sin red: se reintenta en el siguiente ciclo
            } catch (_: HttpException) {
                // error del servidor: idem
            }
            delay(intervaloMs)
        }
    }

    // Ajusta esto a la firma real de formatearFecha(...) en ui.screens.
    // Si recibe LocalDate: formatearFecha(java.time.LocalDate.parse(r.fecha))
    private fun fecha(r: ReservaDto): String = r.fecha

    private fun notificarUnaVez(clave: String, canal: String, id: Int, titulo: String, texto: String) {
        if (store.yaNotificada(clave)) return
        if (Notificaciones.mostrar(app, canal, id, titulo, texto)) store.marcar(clave)
    }
}