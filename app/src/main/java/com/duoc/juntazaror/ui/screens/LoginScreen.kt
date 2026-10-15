package com.duoc.juntazaror.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duoc.juntazaror.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)


@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onIrARegistro: () -> Unit,
    onLoginExitoso: () -> Unit
) {
    val login by viewModel.login.collectAsState()
    val errores by viewModel.errores.collectAsState()
    var mostrarClave by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Login Vecinos",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = login.email,
            onValueChange = { viewModel.actualizarCorreo(it) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = errores.email != null,
            supportingText = { errores.email?.let { Text(it) } }
        )

        OutlinedTextField(
            value = login.passwordHush,
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
            supportingText = { errores.passwordHush?.let { Text(it) } }
        )

        errores.general?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { viewModel.iniciarSesion { onLoginExitoso() } },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿No tienes cuenta?")
            TextButton(onClick = onIrARegistro) {
                Text("Crea una")
            }
        }
    }
}