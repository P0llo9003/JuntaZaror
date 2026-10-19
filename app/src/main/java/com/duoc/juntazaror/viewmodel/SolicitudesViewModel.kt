package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.local.AppDatabase
import com.duoc.juntazaror.data.local.entity.ReservaConSolicitante
import com.duoc.juntazaror.data.repository.ReservaRepository
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

    fun aprobar(id: Int) {
        viewModelScope.launch { repository.aprobar(id) }
    }

    fun rechazar(id: Int) {
        viewModelScope.launch { repository.rechazar(id) }
    }
}