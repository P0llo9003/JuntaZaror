package com.duoc.juntazaror.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.local.AppDatabase
import com.duoc.juntazaror.data.local.entity.Reserva
import com.duoc.juntazaror.data.repository.ReservaRepository
import com.duoc.juntazaror.model.BloqueHorario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SolicitudForm(
    val fecha: String = "",
    val bloque: BloqueHorario? = null,
    val motivo: String = "",
    val personas: String = ""
)

data class SolicitudErrores(
    val fecha: String? = null,
    val bloque: String? = null,
    val motivo: String? = null,
    val personas: String? = null,
    val general: String? = null
)

class SolicitarArriendoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReservaRepository(
        AppDatabase.getInstance(application).reservaDao()
    )

    private val _form = MutableStateFlow(SolicitudForm())
    val form: StateFlow<SolicitudForm> = _form.asStateFlow()

    private val _errores = MutableStateFlow(SolicitudErrores())
    val errores: StateFlow<SolicitudErrores> = _errores.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _guardada = MutableStateFlow(false)
    val guardada: StateFlow<Boolean> = _guardada.asStateFlow()

    fun actualizarFecha(valor: String) {
        _form.value = _form.value.copy(fecha = valor)
    }

    fun actualizarBloque(valor: BloqueHorario) {
        _form.value = _form.value.copy(bloque = valor)
    }

    fun actualizarMotivo(valor: String) {
        _form.value = _form.value.copy(motivo = valor)
    }

    fun actualizarPersonas(valor: String) {
        _form.value = _form.value.copy(personas = valor.filter { it.isDigit() }.take(3))
    }

    fun validarSolicitud(): Boolean {
        val datos = _form.value
        var fechaError: String? = null
        var bloqueError: String? = null
        var motivoError: String? = null
        var personasError: String? = null

        val fecha = runCatching { LocalDate.parse(datos.fecha) }.getOrNull()
        if (datos.fecha.isBlank()) {
            fechaError = "Selecciona una fecha"
        } else if (fecha == null) {
            fechaError = "Fecha inválida"
        } else if (fecha.isBefore(LocalDate.now())) {
            fechaError = "La fecha no puede ser anterior a hoy"
        }

        if (datos.bloque == null) {
            bloqueError = "Selecciona un horario"
        }

        if (datos.motivo.isBlank()) {
            motivoError = "El motivo es obligatorio"
        } else if (datos.motivo.trim().length < 5) {
            motivoError = "El motivo debe tener al menos 5 caracteres"
        }

        val cantidad = datos.personas.toIntOrNull()
        if (datos.personas.isBlank()) {
            personasError = "Indica la cantidad de personas"
        } else if (cantidad == null || cantidad < 1) {
            personasError = "Debe ser al menos 1 persona"
        } else if (cantidad > MAX_PERSONAS) {
            personasError = "El máximo es $MAX_PERSONAS personas"
        }

        val nuevosErrores =
            SolicitudErrores(
                fecha = fechaError,
                bloque = bloqueError,
                motivo = motivoError,
                personas = personasError
            )
        _errores.value = nuevosErrores
        return nuevosErrores == SolicitudErrores()
    }

    fun guardar(cuentaId: Int) {
        if (!validarSolicitud()) return

        val datos = _form.value
        val bloque = datos.bloque ?: return


        viewModelScope.launch {
            try {
                if (!repository.estaDisponible(datos.fecha, bloque)) {
                    _errores.value = _errores.value.copy(
                        general = "Esa fecha y horario ya están reservados. Prueba con otro."
                    )
                    return@launch
                }
                repository.solicitar(
                    Reserva(
                        cuentaId = cuentaId,
                        fecha = datos.fecha,
                        bloque = bloque,
                        motivo = datos.motivo.trim(),
                        cantidadPersonas = datos.personas.toInt()
                    )
                )
                _form.value = SolicitudForm()
                _guardada.value = true
            } catch (e: SQLiteConstraintException) {
                _errores.value = _errores.value.copy(general = "Hubo un error inesperado")
            }
        }
    }

    // La pantalla lo llama después de volver atrás o mostrar la confirmación
    fun confirmacionVista() {
        _guardada.value = false
    }

    companion object {
        const val MAX_PERSONAS = 100
    }
}