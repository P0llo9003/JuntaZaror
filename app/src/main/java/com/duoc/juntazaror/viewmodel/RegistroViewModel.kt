package com.duoc.juntazaror.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.local.AppDatabase
import com.duoc.juntazaror.data.repository.CuentaRepository
import com.duoc.juntazaror.model.RegistroModel
import com.duoc.juntazaror.model.Rol
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

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CuentaRepository(
        AppDatabase.getInstance(application).cuentaDao()
    )

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
        } else if (!Patterns.EMAIL_ADDRESS.matcher(datos.email).matches()) {
            emailError = "Ingresa un correo valido"
        }

        if (datos.passwordHush.isBlank()) {
            passwordHushError = "Debe ingresar una contraseña"
        } else if (datos.passwordHush.length < 4) {
            passwordHushError = "La contraseña debe tener al menos 4 caracteres"
        }

        if (!datos.acceptTerms) {
            termsError = "Debe aceptar los terminos y condiciones"
        }

        val nuevosErrores =
            RegistroErrores(
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
        viewModelScope.launch {
            try {
                if (repository.buscarPorEmail(datos.email) != null) {
                    _errores.value = _errores.value.copy(email = "Este correo ya está registrado")
                    return@launch
                }
                repository.save(datos.name, datos.email, datos.passwordHush, Rol.VECINO)
                _registro.value = RegistroModel()
                _registrado.value = true
            } catch (e: SQLiteConstraintException) {
                _errores.value = _errores.value.copy(email = "Este correo ya está registrado")
            }
        }
    }
    fun confirmacionVista() {
        _registrado.value = false
    }

}


