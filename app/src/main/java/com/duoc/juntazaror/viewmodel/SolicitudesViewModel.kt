package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.repository.ReservaRemoteRepository
import com.duoc.juntazaror.model.UsuarioSesion
import com.duoc.juntazaror.remote.dto.ReservaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// El repositorio se recibe por constructor (con valor por defecto) para poder usar un mock en las pruebas.
// @JvmOverloads genera también el constructor (Application) que necesita viewModel().
class SolicitudesViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: ReservaRemoteRepository = ReservaRemoteRepository()
) : AndroidViewModel(application) {


    private val _solicitudes = MutableStateFlow<List<ReservaDto>>(emptyList())
    val solicitudes: StateFlow<List<ReservaDto>> = _solicitudes.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            repository.solicitudes()
                .onSuccess { _solicitudes.value = it; _error.value = null }
                .onFailure { _error.value = it.message }
        }
    }
//llaman al backend y recargan la lista.
    fun aprobar(id: Long, resolutor: UsuarioSesion) {
        viewModelScope.launch {
            repository.aprobar(id, resolutor.id.toLong())
                .onSuccess { cargar() }
                .onFailure { _error.value = it.message }
        }
    }

    fun rechazar(id: Long, resolutor: UsuarioSesion) {
        viewModelScope.launch {
            repository.rechazar(id, resolutor.id.toLong())
                .onSuccess { cargar() }
                .onFailure { _error.value = it.message }
        }
    }
}
