package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.repository.MovimientoRemoteRepository
import com.duoc.juntazaror.remote.dto.MovimientoDto
import com.duoc.juntazaror.remote.dto.ResumenFinancieroDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransparenciaViewModel(application: Application) : AndroidViewModel(application) {
// ya no se usa room, paso a remoto.
    private val repository = MovimientoRemoteRepository()

    private val _resumen = MutableStateFlow(ResumenFinancieroDto(0, 0, 0, 0))
    val resumen: StateFlow<ResumenFinancieroDto> = _resumen.asStateFlow()//ahora, el resumen financiero no es una data classs local, sino que se usa un dto (de Dtos.kt)

    private val _movimientos = MutableStateFlow<List<MovimientoDto>>(emptyList())
    val movimientos: StateFlow<List<MovimientoDto>> = _movimientos.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargar() {
        viewModelScope.launch {
            repository.resumen()
                .onSuccess { _resumen.value = it; _error.value = null }
                .onFailure { _error.value = it.message }
        }
        viewModelScope.launch {
            repository.listar()
                .onSuccess { _movimientos.value = it }
                .onFailure { _error.value = it.message }
        }
    }
}
