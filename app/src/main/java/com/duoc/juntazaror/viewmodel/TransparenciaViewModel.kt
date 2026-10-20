package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.local.AppDatabase
import com.duoc.juntazaror.data.local.entity.Movimiento
import com.duoc.juntazaror.data.repository.MovimientoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ResumenFinanciero(
    val ingresos: Int = 0,
    val gastos: Int = 0
) {
    val saldo: Int get() = ingresos - gastos
}

class TransparenciaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MovimientoRepository(
        AppDatabase.getInstance(application).movimientoDao()
    )

    val resumen: StateFlow<ResumenFinanciero> =
        combine(repository.totalIngresos, repository.totalGastos) { ingresos, gastos ->
            ResumenFinanciero(ingresos, gastos)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumenFinanciero())

    val movimientos: StateFlow<List<Movimiento>> = repository.movimientos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}