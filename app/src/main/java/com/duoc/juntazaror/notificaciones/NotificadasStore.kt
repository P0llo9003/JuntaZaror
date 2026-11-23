package com.duoc.juntazaror.notificaciones

import android.content.Context
import androidx.core.content.edit

class NotificadasStore(context: Context) {
    private val prefs = context.getSharedPreferences("yaNotificadas", Context.MODE_PRIVATE)

    fun yaNotificada(clave: String): Boolean =
        clave in prefs.getStringSet("claves", emptySet()).orEmpty()

    fun marcar(clave: String) {
        val nuevas = prefs.getStringSet("claves", emptySet()).orEmpty().toMutableSet().apply { add(clave) }
        prefs.edit { putStringSet("claves", nuevas) }
    }
}