package com.duoc.juntazaror.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.duoc.juntazaror.data.local.dao.CuentaDao
import com.duoc.juntazaror.data.local.dao.ReservaDao
import com.duoc.juntazaror.data.local.entity.Cuenta
import com.duoc.juntazaror.data.local.entity.Reserva
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.util.PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Cuenta::class, Reserva::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuentaDao(): CuentaDao
    abstract fun reservaDao(): ReservaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val EMAIL_DIRECTIVA = "directiva@demo.cl"

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cuentas_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                runCatching {
                                    val dao = INSTANCE?.cuentaDao() ?: return@launch
                                    if (dao.buscarPorEmail(EMAIL_DIRECTIVA) == null) {
                                        dao.insert(
                                            Cuenta(
                                                name = "Directiva Demo",
                                                email = EMAIL_DIRECTIVA,
                                                password = PasswordHasher.hash("Demo1234"),
                                                rol = Rol.DIRECTIVA
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}