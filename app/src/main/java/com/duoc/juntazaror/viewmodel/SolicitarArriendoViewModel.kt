package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.repository.ReservaRemoteRepository
import com.duoc.juntazaror.model.BloqueHorario
import com.duoc.juntazaror.remote.ApiException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val repository = ReservaRemoteRepository()

    private val _form = MutableStateFlow(SolicitudForm())
    val form: StateFlow<SolicitudForm> = _form.asStateFlow()

    private val _errores = MutableStateFlow(SolicitudErrores())
    val errores: StateFlow<SolicitudErrores> = _errores.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _guardada = MutableStateFlow(false)
    val guardada: StateFlow<Boolean> = _guardada.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val ocupados: StateFlow<Set<BloqueHorario>> = _form
        .map { it.fecha }
        .distinctUntilChanged()
        .flatMapLatest { fecha ->
            if (fecha.isBlank()) flowOf(emptyList()) else repository.bloquesOcupados(fecha)
        }
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun limpiarBloque() {
        _form.value = _form.value.copy(bloque = null)
    }

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
        if (_guardando.value) return
        if (!validarSolicitud()) return

        val datos = _form.value
        val bloque = datos.bloque ?: return

        _guardando.value = true
        viewModelScope.launch {
            try {
                // chequeo previo de disponibilidad (el backend igual responde 409 si el bloque se ocupó).
                val libre = repository.estaDisponible(datos.fecha, bloque).getOrElse { e ->
                    _errores.value = _errores.value.copy(general = e.message)
                    return@launch
                }
                if (!libre) {
                    _errores.value = _errores.value.copy(general = MENSAJE_OCUPADO)
                    return@launch
                }

                repository.solicitar(
                    cuentaId = cuentaId.toLong(),
                    fecha = datos.fecha,
                    bloque = bloque,
                    motivo = datos.motivo,
                    cantidadPersonas = datos.personas.toInt()
                )
                    .onSuccess {
                        _form.value = SolicitudForm()
                        _guardada.value = true
                    }
                    .onFailure { e ->
                        val mensaje = if ((e as? ApiException)?.code == 409) MENSAJE_OCUPADO else e.message
                        _errores.value = _errores.value.copy(general = mensaje ?: "Hubo un error inesperado :(")
                    }
            } finally {
                _guardando.value = false
            }
        }
    }

    fun confirmacionVista() {
        _guardada.value = false
    }

    companion object {
        const val MAX_PERSONAS = 100
        private const val MENSAJE_OCUPADO = "Esa fecha y horario ya están reservados!! Prueba con otro"
    }
}
