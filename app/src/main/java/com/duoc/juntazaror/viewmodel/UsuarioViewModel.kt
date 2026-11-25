package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.repository.CuentaRemoteRepository
import com.duoc.juntazaror.remote.dto.CuentaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuariosViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: CuentaRemoteRepository = CuentaRemoteRepository()
) : AndroidViewModel(application) {

    private val _cuentas = MutableStateFlow<List<CuentaDto>>(emptyList())
    val cuentas: StateFlow<List<CuentaDto>> = _cuentas.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            repository.listar()
                .onSuccess { _cuentas.value = it; _error.value = null }
                .onFailure { _error.value = it.message }
        }
    }

    fun eliminar(cuenta: CuentaDto) {
        viewModelScope.launch {
            repository.eliminar(cuenta.id)
                .onSuccess { cargar() }
                .onFailure { _error.value = it.message }
        }
    }
}
