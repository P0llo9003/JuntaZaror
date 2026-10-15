package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.duoc.juntazaror.data.local.AppDatabase
import com.duoc.juntazaror.data.repository.CuentaRepository
import com.duoc.juntazaror.model.Rol
import android.util.Patterns
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.model.LoginModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginErrores(
    val email: String? = null,
    val passwordHush: String? = null,
    val general: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CuentaRepository(
        AppDatabase.getInstance(application).cuentaDao()
    )

    private val _login = MutableStateFlow(LoginModel())
    val login: StateFlow<LoginModel> = _login.asStateFlow()

    private val _errores = MutableStateFlow(LoginErrores())
    val errores: StateFlow<LoginErrores> = _errores.asStateFlow()

    fun actualizarCorreo(valor: String) {
        _login.value = _login.value.copy(email = valor)
    }

    fun actualizarClaveHush(valor: String) {
        _login.value = _login.value.copy(passwordHush = valor)
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val datos = _login.value
        val emailError = if (datos.email.isBlank()) "El correo es obligatorio" else null
        val claveError = if (datos.passwordHush.isBlank()) "Debe ingresar su contraseña" else null

        _errores.value = LoginErrores(email = emailError, passwordHush = claveError)
        if (emailError != null || claveError != null) return

        viewModelScope.launch {
            val cuenta = repository.buscarPorEmail(datos.email)

            if (cuenta == null || cuenta.password != datos.passwordHush) {
                _errores.value = LoginErrores(general = "Correo o contraseña incorrectos")
                return@launch
            }


            if (cuenta.rol != Rol.DIRECTIVA) {
                _errores.value = LoginErrores(general = "Por ahora solo la Directiva puede ingresar")
                return@launch
            }

            onExito()
        }
    }
}