package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.repository.CuentaRemoteRepository
import com.duoc.juntazaror.model.RegistroModel
import com.duoc.juntazaror.remote.ApiException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegistroErrores(
    val name: String? = null,
    val email: String? = null,
    val passwordHush: String? = null,
    val acceptTerms: String? = null
)

// El repositorio se recibe por constructor (con valor por defecto) para poder usar un mock en las pruebas.
// @JvmOverloads genera también el constructor (Application) que necesita viewModel().
class RegistroViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: CuentaRemoteRepository = CuentaRemoteRepository()
) : AndroidViewModel(application) {

    private val _registro = MutableStateFlow(RegistroModel())
    val registro: StateFlow<RegistroModel> = _registro.asStateFlow()

    private val _errores = MutableStateFlow(RegistroErrores())
    val errores: StateFlow<RegistroErrores> = _errores.asStateFlow()

    private val _registrado = MutableStateFlow(false)
    val registrado: StateFlow<Boolean> = _registrado.asStateFlow()

    fun actualizarNombre(valor: String) {
        _registro.value = _registro.value.copy(name = valor)
    }

    fun actualizarCorreo(valor: String) {
        _registro.value = _registro.value.copy(email = valor.trim())
    }

    fun actualizarClaveHush(valor: String) {
        _registro.value = _registro.value.copy(passwordHush = valor)
    }

    fun actualizarAceptaTerminos(valor: Boolean) {
        _registro.value = _registro.value.copy(acceptTerms = valor)
    }

    fun validarRegistro(): Boolean {
        val datos = _registro.value
        var nameError: String? = null
        var emailError: String? = null
        var passwordHushError: String? = null
        var termsError: String? = null

        if (datos.name.isBlank()) {
            nameError = "El nombre es obligatorio"
        } else if (datos.name.length < 3) {
            nameError = "El nombre debe tener al menos 3 caracteres"
        }

        if (datos.email.isBlank()) {
            emailError = "El correo es obligatorio"
        } else if (!EMAIL_REGEX.matches(datos.email)) {
            emailError = "Ingresa un correo valido"
        }

        if (datos.passwordHush.isBlank()) {
            passwordHushError = "Debe ingresar una contraseña"
        } else if (datos.passwordHush.length < 4) {
            passwordHushError = "La contraseña debe tener al menos 4 caracteres"
        } else if (!datos.passwordHush.any { !it.isLetterOrDigit() }) {
            passwordHushError = "La contraseña debe tener al menos un caracter especial"
        }

        if (!datos.acceptTerms) {
            termsError = "Debe aceptar los terminos y condiciones"
        }

        val nuevosErrores = RegistroErrores(
            name = nameError,
            email = emailError,
            passwordHush = passwordHushError,
            acceptTerms = termsError
        )
        _errores.value = nuevosErrores
        return nuevosErrores == RegistroErrores()
    }

    fun guardar() {
        if (!validarRegistro()) return

        val datos = _registro.value
        viewModelScope.launch {//del repositorio remoto:
            repository.registrar(datos.name, datos.email, datos.passwordHush, datos.phone)
                .onSuccess {
                    _registro.value = RegistroModel()
                    _registrado.value = true
                }
                .onFailure { e ->
                    val mensaje = if ((e as? ApiException)?.code == 409) {
                        "Este correo ya está registrado"
                    } else {
                        e.message
                    }
                    _errores.value = _errores.value.copy(email = mensaje)
                }
        }
    }

    fun confirmacionVista() {
        _registrado.value = false
    }

    companion object {
        // Reemplaza a android.util.Patterns.EMAIL_ADDRESS, que no funciona en pruebas unitarias (JVM).
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}
