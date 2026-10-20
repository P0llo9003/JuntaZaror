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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import com.duoc.juntazaror.data.local.entity.ReservaConSolicitante
import com.duoc.juntazaror.model.EstadoReserva
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.model.UsuarioSesion
import com.duoc.juntazaror.viewmodel.SolicitudesViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun SolicitudesScreen(
    usuario: UsuarioSesion,
    viewModel: SolicitudesViewModel = viewModel()
) {
    if (usuario.rol != Rol.DIRECTIVA) return

    val solicitudes by viewModel.solicitudes.collectAsState()
    val pendientes = solicitudes.count { it.reserva.estado == EstadoReserva.PENDIENTE }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Solicitudes de arriendo",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(text = "$pendientes pendientes")
        Spacer(modifier = Modifier.height(10.dp))

        if (solicitudes.isEmpty()) {
            Text("Aún no hay solicitudes")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(solicitudes, key = { it.reserva.id }) { item ->
                    SolicitudCard(
                        item = item,
                        onAprobar = { viewModel.aprobar(item.reserva.id, usuario) },
                        onRechazar = { viewModel.rechazar(item.reserva.id, usuario) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SolicitudCard(
    item: ReservaConSolicitante,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit
) {
    val reserva = item.reserva

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.solicitante,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                EstadoEtiqueta(reserva.estado)
            }
            Text("${formatearFecha(reserva.fecha)} · ${reserva.bloque.etiqueta}")
            Text("Motivo: ${reserva.motivo}")
            Text("Personas: ${reserva.cantidadPersonas}")

            if (reserva.estado == EstadoReserva.PENDIENTE) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onAprobar, modifier = Modifier.weight(1f)) {
                        Text("Aprobar")
                    }
                    OutlinedButton(onClick = onRechazar, modifier = Modifier.weight(1f)) {
                        Text("Rechazar")
                    }
                }
            }
        }
    }
}

@Composable
fun EstadoEtiqueta(estado: EstadoReserva) {
    val (texto, color) = when (estado) {
        EstadoReserva.PENDIENTE -> "Pendiente" to MaterialTheme.colorScheme.tertiary
        EstadoReserva.CONFIRMADA -> "Confirmada" to Color(0xFF66BB6A) //TODO Crear una ruta para el color en vez de llamar al codigo de color de una
        EstadoReserva.RECHAZADA -> "Rechazada" to MaterialTheme.colorScheme.error
    }
    Text(text = texto, color = color, style = MaterialTheme.typography.labelLarge)
}

fun formatearFecha(fecha: String): String =
    runCatching {
        LocalDate.parse(fecha).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }.getOrDefault(fecha)