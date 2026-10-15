package com.duoc.juntazaror.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.juntazaror.data.local.entity.Cuenta
import com.duoc.juntazaror.viewmodel.UsuariosViewModel

@Composable
fun UsuariosScreen(
    viewModel: UsuariosViewModel = viewModel()
) {
    val cuentas by viewModel.cuentas.collectAsState()
    var cuentaAEliminar by remember { mutableStateOf<Cuenta?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        Text(
            text = "Usuarios registrados (${cuentas.size})",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (cuentas.isEmpty()) {
            Text("Aún no hay usuarios registrados")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(cuentas, key = { it.id }) { cuenta ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cuenta.name, style = MaterialTheme.typography.titleMedium)
                                Text(cuenta.email)
                                Text("Rol: ${cuenta.rol}", style = MaterialTheme.typography.bodySmall)
                            }
                            IconButton(onClick = { cuentaAEliminar = cuenta }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Eliminar usuario",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    cuentaAEliminar?.let { cuenta ->
        AlertDialog(
            onDismissRequest = { cuentaAEliminar = null },
            title = { Text("Eliminar usuario") },
            text = { Text("¿Seguro que quieres eliminar a ${cuenta.name}? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminar(cuenta)
                    cuentaAEliminar = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { cuentaAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}