package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.duoc.juntazaror.data.local.AppDatabase
import com.duoc.juntazaror.data.local.entity.Cuenta
import com.duoc.juntazaror.data.repository.CuentaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UsuariosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CuentaRepository(
        AppDatabase.getInstance(application).cuentaDao()
    )

    val cuentas: StateFlow<List<Cuenta>> = repository.cuentas
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )


    fun eliminar(cuenta: Cuenta) {
        viewModelScope.launch {
            repository.delete(cuenta)
        }
    }
}