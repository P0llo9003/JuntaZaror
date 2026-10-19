package com.duoc.juntazaror.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.duoc.juntazaror.data.local.AppDatabase
import com.duoc.juntazaror.data.local.entity.Reserva
import com.duoc.juntazaror.data.repository.ReservaRepository
import kotlinx.coroutines.flow.Flow

class MisSolicitudesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ReservaRepository(
        AppDatabase.getInstance(application).reservaDao()
    )

    fun solicitudesDe(cuentaId: Int): Flow<List<Reserva>> = repository.delUsuario(cuentaId)
}