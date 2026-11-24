package com.duoc.juntazaror.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.repository.MovimientoRemoteRepository
import com.duoc.juntazaror.model.TipoMovimiento
import com.duoc.juntazaror.util.ComprobanteCodec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class MovimientoForm(
    val esIngreso: Boolean = true,
    val concepto: String = "",
    val monto: String = "",
    val fecha: String = "",
    val adjuntaComprobante: Boolean = false,
    val comprobanteUri: Uri? = null
)

data class MovimientoErrores(
    val concepto: String? = null,
    val monto: String? = null,
    val fecha: String? = null,
    val comprobante: String? = null,
    val general: String? = null //nuevo campo
)

// El repositorio se recibe por constructor (con valor por defecto) para poder usar un mock en las pruebas.
// @JvmOverloads genera también el constructor (Application) que necesita viewModel().
class RegistrarMovimientoViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: MovimientoRemoteRepository = MovimientoRemoteRepository()
) : AndroidViewModel(application) {


    private val _form = MutableStateFlow(MovimientoForm())
    val form: StateFlow<MovimientoForm> = _form.asStateFlow()

    private val _errores = MutableStateFlow(MovimientoErrores())
    val errores: StateFlow<MovimientoErrores> = _errores.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _guardado = MutableStateFlow(false)
    val guardado: StateFlow<Boolean> = _guardado.asStateFlow()

    fun actualizarTipo(esIngreso: Boolean) {
        _form.value = _form.value.copy(esIngreso = esIngreso)
    }

    fun actualizarConcepto(valor: String) {
        _form.value = _form.value.copy(concepto = valor)
    }

    fun actualizarMonto(valor: String) {
        _form.value = _form.value.copy(monto = valor.filter { it.isDigit() }.take(9))
    }

    fun actualizarFecha(valor: String) {
        _form.value = _form.value.copy(fecha = valor)
    }

    fun actualizarAdjunta(valor: Boolean) {
        _form.value = _form.value.copy(
            adjuntaComprobante = valor,
            comprobanteUri = if (valor) _form.value.comprobanteUri else null
        )
    }

    fun actualizarImagenDesdeGaleria(uri: Uri?) {
        if (uri != null) _form.value = _form.value.copy(comprobanteUri = uri)
    }

    fun actualizarImagenDesdeCamara(uri: Uri?, capturaCorrecta: Boolean) {
        if (capturaCorrecta && uri != null) {
            _form.value = _form.value.copy(comprobanteUri = uri)
        }
    }

    fun validarMovimiento(): Boolean {
        val datos = _form.value
        var conceptoError: String? = null
        var montoError: String? = null
        var fechaError: String? = null
        var comprobanteError: String? = null

        if (datos.concepto.isBlank()) {
            conceptoError = "El concepto es obligatorio"
        } else if (datos.concepto.trim().length < 3) {
            conceptoError = "El concepto debe tener al menos 3 caracteres"
        }

        val monto = datos.monto.toIntOrNull()
        if (datos.monto.isBlank()) {
            montoError = "Indica el monto"
        } else if (monto == null || monto < 1) {
            montoError = "El monto debe ser mayor a 0"
        }

        val fecha = runCatching { LocalDate.parse(datos.fecha) }.getOrNull()
        if (datos.fecha.isBlank()) {
            fechaError = "Selecciona una fecha"
        } else if (fecha == null) {
            fechaError = "Fecha inválida"
        }

        if (datos.adjuntaComprobante && datos.comprobanteUri == null) {
            comprobanteError = "Agrega una foto o imagen del comprobante"
        }

        val nuevosErrores =
            MovimientoErrores(
                concepto = conceptoError,
                monto = montoError,
                fecha = fechaError,
                comprobante = comprobanteError
            )
        _errores.value = nuevosErrores
        return nuevosErrores == MovimientoErrores()
    }

    fun guardar(registradoPorId: Int) {
        if (_guardando.value) return
        if (!validarMovimiento()) return

        val datos = _form.value
        _guardando.value = true
        viewModelScope.launch {
            try {
                //se convierte el comprobante a un jpeg base 64
                val comprobante = datos.comprobanteUri?.let { uri ->
                    ComprobanteCodec.uriABase64(getApplication<Application>(), uri) ?: run {
                        _errores.value = _errores.value.copy(
                            comprobante = "No se pudo leer la imagen del comprobante :c "
                        )
                        return@launch
                    }
                }

                repository.registrar(
                    tipo = if (datos.esIngreso) TipoMovimiento.INGRESO else TipoMovimiento.GASTO,
                    concepto = datos.concepto,
                    monto = datos.monto.toInt(),
                    fecha = datos.fecha,
                    comprobanteUri = comprobante,
                    registradoPorId = registradoPorId.toLong()
                )
                    .onSuccess {
                        _form.value = MovimientoForm()
                        _guardado.value = true
                    }
                    .onFailure { e ->
                        _errores.value = _errores.value.copy(general = e.message ?: "Hubo un error inesperado :((")
                    }
            } finally {
                _guardando.value = false
            }
        }
    }

    fun confirmacionVista() {
        _guardado.value = false
    }
}
