package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.repository.ReservaRemoteRepository
import com.duoc.juntazaror.remote.dto.ReservaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MisSolicitudesViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: ReservaRemoteRepository = ReservaRemoteRepository()
) : AndroidViewModel(application) {

    private val _solicitudes = MutableStateFlow<List<ReservaDto>>(emptyList())
    val solicitudes: StateFlow<List<ReservaDto>> = _solicitudes.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargar(cuentaId: Int) {
        viewModelScope.launch {
            repository.delUsuario(cuentaId.toLong())
                .onSuccess { _solicitudes.value = it; _error.value = null }
                .onFailure { _error.value = it.message }
        }
    }
}
