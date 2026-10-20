package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.local.AppDatabase
import com.duoc.juntazaror.data.local.entity.ReservaConSolicitante
import com.duoc.juntazaror.data.repository.ReservaRepository
import com.duoc.juntazaror.model.UsuarioSesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SolicitudesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReservaRepository(
        AppDatabase.getInstance(application).reservaDao()
    )

    val solicitudes: StateFlow<List<ReservaConSolicitante>> = repository.conSolicitante
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun aprobar(id: Int, resolutor: UsuarioSesion) {
        viewModelScope.launch { repository.aprobar(id, resolutor) }
    }
    fun rechazar(id: Int, resolutor: UsuarioSesion) {
        viewModelScope.launch { repository.rechazar(id, resolutor) }
    }
}