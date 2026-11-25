package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.repository.CuentaRemoteRepository
import com.duoc.juntazaror.model.LoginModel
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.model.UsuarioSesion
import com.duoc.juntazaror.notificaciones.NotificadorReservas
import com.duoc.juntazaror.remote.ApiException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginErrores(
    val email: String? = null,
    val passwordHush: String? = null,
    val general: String? = null
)

class LoginViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: CuentaRemoteRepository = CuentaRemoteRepository(),
    private val notificador: NotificadorReservas = NotificadorReservas(application)
) : AndroidViewModel(application) {


    private val _login = MutableStateFlow(LoginModel())
    val login: StateFlow<LoginModel> = _login.asStateFlow()

    private val _errores = MutableStateFlow(LoginErrores())
    val errores: StateFlow<LoginErrores> = _errores.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _usuario = MutableStateFlow<UsuarioSesion?>(null)
    val usuario: StateFlow<UsuarioSesion?> = _usuario.asStateFlow()

    private var jobNotificaciones: Job? = null

    fun actualizarCorreo(valor: String) {
        _login.value = _login.value.copy(email = valor)
    }

    fun actualizarClaveHush(valor: String) {
        _login.value = _login.value.copy(passwordHush = valor)
    }

    private fun iniciarNotificaciones(usuario: UsuarioSesion) {
        jobNotificaciones?.cancel()
        jobNotificaciones = viewModelScope.launch { notificador.escuchar(usuario) }
    }

    fun cerrarSesion() {
        jobNotificaciones?.cancel()
        jobNotificaciones = null
        _usuario.value = null
        _login.value = LoginModel()
        _errores.value = LoginErrores()
    }

    fun iniciarSesion(onExito: (Rol) -> Unit) {
        if (_cargando.value) return          // evita doble toque mientras hay una llamada en curso

        val datos = _login.value
        val emailError = if (datos.email.isBlank()) "El correo es obligatorio" else null
        val claveError = if (datos.passwordHush.isBlank()) "Debe ingresar su contraseña" else null

        _errores.value = LoginErrores(email = emailError, passwordHush = claveError)
        if (emailError != null || claveError != null) return

        viewModelScope.launch {
            _cargando.value = true
            repository.login(datos.email, datos.passwordHush)
                .onSuccess { cuenta ->
                    val sesion = UsuarioSesion(cuenta.id.toInt(), cuenta.name, cuenta.email, cuenta.rol)
                    _usuario.value = sesion
                    iniciarNotificaciones(sesion)
                    onExito(cuenta.rol)
                }
                .onFailure { e ->
                    val mensaje = when ((e as? ApiException)?.code) {
                        401, 404 -> "Correo o contraseña incorrectos"
                        0 -> "No se pudo conectar con el servidor"
                        else -> e.message ?: "Error inesperado"
                    }
                    _errores.value = LoginErrores(general = mensaje)
                }
            _cargando.value = false
        }
    }
}