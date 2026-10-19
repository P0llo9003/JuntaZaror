package com.duoc.juntazaror.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.juntazaror.ui.components.ComprobanteSelector
import com.duoc.juntazaror.viewmodel.RegistrarMovimientoViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarMovimientoScreen(
    onVolver: () -> Unit,
    viewModel: RegistrarMovimientoViewModel = viewModel()
) {
    val form by viewModel.form.collectAsState()
    val errores by viewModel.errores.collectAsState()
    val guardando by viewModel.guardando.collectAsState()
    val guardado by viewModel.guardado.collectAsState()
    var mostrarCalendario by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(guardado) {
        if (guardado) {
            Toast.makeText(context, "Movimiento registrado", Toast.LENGTH_SHORT).show()
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
                text = "Registrar movimiento",
                style = MaterialTheme.typography.headlineMedium
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (form.esIngreso) "Tipo: Ingreso" else "Tipo: Gasto",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = form.esIngreso,
                onCheckedChange = { viewModel.actualizarTipo(it) }
            )
        }

        OutlinedTextField(
            value = form.concepto,
            onValueChange = { viewModel.actualizarConcepto(it) },
            label = { Text("Concepto") },
            placeholder = { Text("Ej: Arriendo sede, Cuenta de luz") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = errores.concepto != null,
            supportingText = { errores.concepto?.let { MensajeError(it) } }
        )

        OutlinedTextField(
            value = form.monto,
            onValueChange = { viewModel.actualizarMonto(it) },
            label = { Text("Monto (CLP)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = errores.monto != null,
            supportingText = { errores.monto?.let { MensajeError(it) } }
        )

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

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = form.adjuntaComprobante,
                onCheckedChange = { viewModel.actualizarAdjunta(it) }
            )
            Text("Adjuntar comprobante")
        }

        AnimatedVisibility(visible = form.adjuntaComprobante) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ComprobanteSelector(
                    imagenUri = form.comprobanteUri,
                    onGaleria = { viewModel.actualizarImagenDesdeGaleria(it) },
                    onCamara = { uri, exito -> viewModel.actualizarImagenDesdeCamara(uri, exito) }
                )
                errores.comprobante?.let { MensajeError(it) }
            }
        }

        Button(
            onClick = { viewModel.guardar() },
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (guardando) "Guardando..." else "Guardar movimiento")
        }
    }

    if (mostrarCalendario) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        val fecha = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        viewModel.actualizarFecha(fecha.toString())
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