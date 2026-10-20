package com.duoc.juntazaror.ui.screens

import android.R.attr.enabled
import android.R.attr.type
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.juntazaror.viewmodel.RegistroViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel = viewModel(),
    onVolverALogin: () -> Unit
) {
    val registro by
    viewModel.registro.collectAsState()

    val errores by
    viewModel.errores.collectAsState()

    var mostrarClave by
    rememberSaveable { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Registro Vecinos",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(
            modifier = Modifier.height(10.dp)
        )
        OutlinedTextField(
            value = registro.name,
            onValueChange = { viewModel.actualizarNombre(it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errores.name != null,
            supportingText = { errores.name?.let { MensajeError(it) } }
        )
        OutlinedTextField(
            value = registro.email,
            onValueChange = { viewModel.actualizarCorreo(it) },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth(),
            isError = errores.email != null,
            supportingText = { errores.email?.let { MensajeError(it) } }
        )

        OutlinedTextField(
            value = registro.passwordHush,
            onValueChange = { viewModel.actualizarClaveHush(it) },
            label = { Text("Contraseña") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (mostrarClave) VisualTransformation.None
            else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { mostrarClave = !mostrarClave }) {
                    Icon(
                        imageVector = if (mostrarClave) Icons.Filled.VisibilityOff
                        else Icons.Filled.Visibility,
                        contentDescription = if (mostrarClave) "Ocultar contraseña"
                        else "Mostrar contraseña"
                    )
                }
            },
            isError = errores.passwordHush != null,
            supportingText = { errores.passwordHush?.let { MensajeError(it) } }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = registro.acceptTerms,
                onCheckedChange = { viewModel.actualizarAceptaTerminos(it) })
            Text(
                text = "Acepto los términos y condiciones"
            )
        }
        if (errores.acceptTerms != null) {
            Text(
                text = errores.acceptTerms!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 12.dp)
            )
        }

        Spacer( modifier = Modifier.height(10.dp) )

        Button( onClick = { viewModel.guardar() },
            modifier = Modifier.fillMaxWidth(),
            )
        {
            Text("Guardar" )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿Ya tienes cuenta?")
            TextButton(onClick = onVolverALogin) {
                Text("Inicia sesión")
            }
        }

    }
}
