package com.duoc.juntazaror.data.repository

import com.duoc.juntazaror.data.local.dao.MovimientoDao
import com.duoc.juntazaror.data.local.entity.Movimiento
import com.duoc.juntazaror.model.TipoMovimiento
import kotlinx.coroutines.flow.Flow

class MovimientoRepository(private val dao: MovimientoDao) {

    val movimientos: Flow<List<Movimiento>> = dao.observarTodos()

    val totalIngresos: Flow<Int> = dao.observarTotal(TipoMovimiento.INGRESO)
    val totalGastos: Flow<Int> = dao.observarTotal(TipoMovimiento.GASTO)

    suspend fun registrar(movimiento: Movimiento) {
        dao.insert(movimiento)
    }
}