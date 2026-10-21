package com.duoc.juntazaror.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.duoc.juntazaror.util.CamaraUriFactory

@Composable
fun ComprobanteSelector(
    imagenUri: Uri?,
    onGaleria: (Uri?) -> Unit,
    onCamara: (Uri?, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var uriPendienteCamara by rememberSaveable { mutableStateOf<Uri?>(null) }

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uriSeleccionado ->
        val copia = uriSeleccionado?.let {
            try {
                CamaraUriFactory.copiarDesdeGaleria(context, it)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "No se pudo cargar la imagen: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                null
            }
        }
        onGaleria(copia)
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        onCamara(uriPendienteCamara, exito)

        if (!exito) {
            uriPendienteCamara = null
        }
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            try {
                val uri = CamaraUriFactory.crearUri(context)
                uriPendienteCamara = uri
                camaraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error al abrir la cámara: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                context,
                "Debes conceder el permiso de cámara en Ajustes.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun tomarFotografia() {
        val permisoConcedido = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (permisoConcedido) {
            try {
                val uri = CamaraUriFactory.crearUri(context)
                uriPendienteCamara = uri
                camaraLauncher.launch(uri)
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error al abrir la cámara: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ImagenInteligente(imagenUri = imagenUri)

        Button(
            onClick = { galeriaLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Collections,
                contentDescription = "Galería"
            )
            Text("  Elegir desde galería")
        }

        OutlinedButton(
            onClick = { tomarFotografia() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.CameraAlt,
                contentDescription = "Cámara"
            )
            Text("  Tomar fotografía")
        }
    }
}