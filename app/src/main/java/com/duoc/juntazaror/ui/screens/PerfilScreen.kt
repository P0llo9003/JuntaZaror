package com.duoc.juntazaror.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.model.UsuarioSesion

@Composable
fun PerfilScreen(
    usuario: UsuarioSesion,
    onCerrarSesion: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Mi perfil", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                DatoPerfil("Nombre", usuario.nombre)
                Spacer(modifier = Modifier.height(12.dp))
                DatoPerfil("Correo", usuario.email)
                Spacer(modifier = Modifier.height(12.dp))
                DatoPerfil("Rol", etiquetaRol(usuario.rol))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
private fun DatoPerfil(titulo: String, valor: String) {
    Column {
        Text(titulo, style = MaterialTheme.typography.labelMedium)
        Text(valor, style = MaterialTheme.typography.titleMedium)
    }
}

private fun etiquetaRol(rol: Rol): String = when (rol) {
    Rol.VECINO -> "Vecino/a"
    Rol.DIRECTIVA -> "Directiva"
    Rol.TESORERIA -> "Tesorería"
}