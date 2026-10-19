package com.duoc.juntazaror.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.juntazaror.data.local.entity.Reserva
import com.duoc.juntazaror.model.EstadoReserva
import com.duoc.juntazaror.model.UsuarioSesion
import com.duoc.juntazaror.viewmodel.MisSolicitudesViewModel

@Composable
fun MisSolicitudesLista(
    usuario: UsuarioSesion,
    modifier: Modifier = Modifier,
    viewModel: MisSolicitudesViewModel = viewModel()
) {
    val flujo = remember(usuario.id) { viewModel.solicitudesDe(usuario.id) }
    val solicitudes by flujo.collectAsState(initial = emptyList())

    Column(modifier = modifier) {
        Text("Mis solicitudes", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (solicitudes.isEmpty()) {
            Text("Aún no has hecho solicitudes")
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(solicitudes, key = { it.id }) { reserva ->
                    MiSolicitudCard(reserva)
                }
            }
        }
    }
}

@Composable
private fun MiSolicitudCard(reserva: Reserva) {
    val mensaje = when (reserva.estado) {
        EstadoReserva.PENDIENTE -> "Esperando respuesta de la Directiva"
        EstadoReserva.CONFIRMADA -> "Tu arriendo fue aprobado"
        EstadoReserva.RECHAZADA -> "Fue rechazada. Puedes pedir otra fecha u horario"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatearFecha(reserva.fecha),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                EstadoEtiqueta(reserva.estado)
            }
            Text(reserva.bloque.etiqueta)
            Text("Motivo: ${reserva.motivo}")
            Text("Personas: ${reserva.cantidadPersonas}")
            Text(mensaje, style = MaterialTheme.typography.bodySmall)
        }
    }
}