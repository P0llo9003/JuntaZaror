package com.duoc.juntazaror.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.juntazaror.model.BloqueHorario
import com.duoc.juntazaror.model.UsuarioSesion
import com.duoc.juntazaror.viewmodel.SolicitarArriendoViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitarArriendoScreen(
    usuario: UsuarioSesion,
    onVolver: () -> Unit,
    viewModel: SolicitarArriendoViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val guardada by viewModel.guardada.collectAsState()
    var mostrarCalendario by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(guardada) {
        if (guardada) {
            Toast.makeText(context, "Solicitud enviada", Toast.LENGTH_SHORT).show()
            viewModel.confirmacionVista()
            onVolver()
        }
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Solicitar arriendo",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = form.fecha,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha") },
            placeholder = { Text("Selecciona una fecha") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { mostrarCalendario = true }) {
                    Icon(Icons.Filled.DateRange, contentDescription = "Elegir fecha")
                }
            },
            isError = errores.fecha != null,
            supportingText = { errores.fecha?.let { MensajeError(it) } }
        )


        Text("Horario", style = MaterialTheme.typography.titleMedium)
        BloqueHorario.entries.forEach { bloque ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = form.bloque == bloque,
                        onClick = { viewModel.actualizarBloque(bloque) },
                        role = Role.RadioButton
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = form.bloque == bloque, onClick = null)
                Text(text = bloque.etiqueta, modifier = Modifier.padding(start = 8.dp))
            }
        }
        errores.bloque?.let { MensajeError(it) }

        OutlinedTextField(
            value = form.motivo,
            onValueChange = { viewModel.actualizarMotivo(it) },
            label = { Text("Motivo del arriendo") },
            modifier = Modifier.fillMaxWidth(),
            isError = errores.motivo != null,
            supportingText = { errores.motivo?.let { MensajeError(it) } }
        )

        OutlinedTextField(
            value = form.personas,
            onValueChange = { viewModel.actualizarPersonas(it) },
            label = { Text("Cantidad de personas") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = errores.personas != null,
            supportingText = { errores.personas?.let { MensajeError(it) } }
        )

        errores.general?.let { MensajeError(it) }

        Button(
            onClick = { viewModel.guardar(usuario.id) },
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (guardando) "Enviando..." else "Enviar solicitud")
        }
    }

    if (mostrarCalendario) {
        val hoyUtc = remember {
            LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        val pickerState = rememberDatePickerState(
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis >= hoyUtc
            }
        )
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val fecha = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        viewModel.actualizarFecha(fecha.toString())   // "2026-11-15"
                    }
                    mostrarCalendario = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
fun MensajeError(mensaje: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = mensaje, color = MaterialTheme.colorScheme.error)
    }
}