package com.duoc.juntazaror.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.duoc.juntazaror.data.local.entity.Movimiento
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.model.TipoMovimiento
import com.duoc.juntazaror.model.UsuarioSesion
import com.duoc.juntazaror.viewmodel.MovimientosViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MovimientosScreen(
    usuario: UsuarioSesion,
    onRegistrar: () -> Unit,
    viewModel: MovimientosViewModel = viewModel()
) {
    if (usuario.rol != Rol.TESORERIA) return

    val movimientos by viewModel.movimientos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Movimientos", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = onRegistrar, modifier = Modifier.fillMaxWidth()) {
            Text("Registrar movimiento")
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (movimientos.isEmpty()) {
            Text("Aún no hay movimientos registrados")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(movimientos, key = { it.id }) { movimiento ->
                    MovimientoCard(movimiento)
                }
            }
        }
    }
}

@Composable
private fun MovimientoCard(movimiento: Movimiento) {
    val esIngreso = movimiento.tipo == TipoMovimiento.INGRESO
    val colorMonto = if (esIngreso) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
    val signo = if (esIngreso) "+" else "-"

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            movimiento.comprobanteUri?.let { ruta ->
                AsyncImage(
                    model = Uri.parse(ruta),
                    contentDescription = "Comprobante",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(movimiento.concepto, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${formatearFecha(movimiento.fecha)} · ${movimiento.tipo.etiqueta}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "$signo${formatearMonto(movimiento.monto)}",
                color = colorMonto,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

fun formatearMonto(monto: Int): String =
    "$" + NumberFormat.getIntegerInstance(Locale("es", "CL")).format(monto)