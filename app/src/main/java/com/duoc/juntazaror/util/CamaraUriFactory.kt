package com.duoc.juntazaror.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object CamaraUriFactory {

    private fun carpeta(context: Context): File {
        val dir = File(context.filesDir, "comprobantes")
        if (!dir.exists() && !dir.mkdirs()) {
            throw IllegalStateException("No se pudo crear el directorio de comprobantes")
        }
        return dir
    }

    private fun aUri(context: Context, archivo: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)

    fun crearUri(context: Context): Uri {
        val archivo = File.createTempFile("comprobante_", ".jpg", carpeta(context))
        return aUri(context, archivo)
    }

    fun copiarDesdeGaleria(context: Context, origen: Uri): Uri {
        val archivo = File.createTempFile("comprobante_", ".jpg", carpeta(context))
        context.contentResolver.openInputStream(origen).use { entrada ->
            requireNotNull(entrada) { "No se pudo leer la imagen" }
            archivo.outputStream().use { salida -> entrada.copyTo(salida) }
        }
        return aUri(context, archivo)
    }
}