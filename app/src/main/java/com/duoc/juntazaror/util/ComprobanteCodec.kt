package com.duoc.juntazaror.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.math.max

//hay que guardar el comprobante no como uri, sino como url o jpeg. la uri por si sola no sirve fuera del frontend.
object ComprobanteCodec {

    private const val LADO_MAXIMO_PX = 1024
    private const val CALIDAD_JPEG = 70
    private const val PREFIJO = "data:image/jpeg;base64,"

    suspend fun uriABase64(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val origen = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(origen) { decoder, info, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val mayor = max(info.size.width, info.size.height)
                if (mayor > LADO_MAXIMO_PX) {
                    val escala = LADO_MAXIMO_PX.toFloat() / mayor
                    decoder.setTargetSize(
                        (info.size.width * escala).toInt().coerceAtLeast(1),
                        (info.size.height * escala).toInt().coerceAtLeast(1)
                    )
                }
            }
            val salida = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)
            PREFIJO + Base64.encodeToString(salida.toByteArray(), Base64.NO_WRAP)
        }.getOrNull()
    }

    fun modeloParaImagen(comprobante: String): Any {
        if (comprobante.startsWith("data:")) {
            val base64 = comprobante.substringAfter("base64,", missingDelimiterValue = "")
            val bytes = runCatching { Base64.decode(base64, Base64.DEFAULT) }.getOrNull()
            if (bytes != null) return ByteBuffer.wrap(bytes)
        }
        return if (comprobante.startsWith("http")) comprobante else Uri.parse(comprobante)
    }
}
