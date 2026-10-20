package com.duoc.juntazaror.notificaciones

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.duoc.juntazaror.MainActivity
import com.duoc.juntazaror.R

object Notificaciones {
    const val CANAL_NUEVAS = "nuevas_solicitudes"
    const val CANAL_RESOLUCIONES = "resolucion_solicitudes"

    fun crearCanales(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CANAL_NUEVAS, "Nuevas solicitudes", NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "Avisa a la directiva de solicitudes pendientes de revision" }
        )
        nm.createNotificationChannel(
            NotificationChannel(CANAL_RESOLUCIONES, "Resolucion de solicitudes", NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "Se le notifica al usuario de rol vecino la decision sobre su solicitud" }
        )
    }

    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, canal: String, id: Int, titulo: String, texto: String): Boolean {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return false

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendiente = PendingIntent.getActivity(
            context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, canal)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setStyle(NotificationCompat.BigTextStyle().bigText(texto))
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        NotificationManagerCompat.from(context).notify(id, notificacion)
        return true
    }
}