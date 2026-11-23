package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.repository.MovimientoRemoteRepository
import com.duoc.juntazaror.remote.dto.MovimientoDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovimientosViewModel(application: Application) : AndroidViewModel(application) {
//ya no se usa room. pasó a remoto
    private val repository = MovimientoRemoteRepository()// su repository remoto.

    private val _movimientos = MutableStateFlow<List<MovimientoDto>>(emptyList())
    val movimientos: StateFlow<List<MovimientoDto>> = _movimientos.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            repository.listar()
                .onSuccess { _movimientos.value = it; _error.value = null }
                .onFailure { _error.value = it.message }
        }
    }
}
