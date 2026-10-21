package com.duoc.juntazaror.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.juntazaror.data.local.entity.Movimiento
import com.duoc.juntazaror.model.TipoMovimiento
import com.duoc.juntazaror.viewmodel.TransparenciaViewModel

private val VerdeIngreso = Color(0xFF2E7D32)

@Composable
fun TranspareciaScreen(viewModel: TransparenciaViewModel = viewModel()) {
    val resumen by viewModel.resumen.collectAsState()
    val movimientos by viewModel.movimientos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Transparencia financiera", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Ingresos y gastos de la Junta de Vecinos",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TarjetaResumen(
                titulo = "Ingresos",
                monto = resumen.ingresos,
                color = VerdeIngreso,
                modifier = Modifier.weight(1f)
            )
            TarjetaResumen(
                titulo = "Gastos",
                monto = resumen.gastos,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        TarjetaResumen(
            titulo = "Saldo",
            monto = resumen.saldo,
            color = if (resumen.saldo >= 0) VerdeIngreso else MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text("Movimientos", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (movimientos.isEmpty()) {
            Text("Aun no hay movimientos registrados")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(movimientos, key = {it.id}) { movimiento ->
                    FilaMovimiento(movimiento)
                }
            }
        }
    }
}

// Transparencia Financiera
@Composable
private fun TarjetaResumen(
    titulo: String,
    monto: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(16.dp)){
            Text(titulo, style = MaterialTheme.typography.labelLarge)
            Text(
                text = formatearMonto(monto),
                style = MaterialTheme.typography.headlineSmall,
                color = color
            )
        }
    }
}

// Movimientos
@Composable
private fun FilaMovimiento(movimiento: Movimiento) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(movimiento.concepto, style = MaterialTheme.typography.headlineMedium)
                Text(
                    "${formatearFecha(movimiento.fecha)} - ${movimiento.tipo.etiqueta}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = (if (esIngreso) "+" else "-") + formatearMonto(movimiento.monto),
                color = if (esIngreso) VerdeIngreso else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}