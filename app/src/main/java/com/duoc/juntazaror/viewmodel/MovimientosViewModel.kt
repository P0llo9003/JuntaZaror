package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.local.AppDatabase
import com.duoc.juntazaror.data.local.entity.Movimiento
import com.duoc.juntazaror.data.repository.MovimientoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MovimientosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovimientoRepository(
        AppDatabase.getInstance(application).movimientoDao()
    )

    val movimientos: StateFlow<List<Movimiento>> = repository.movimientos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}