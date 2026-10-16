package com.duoc.juntazaror.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.duoc.juntazaror.data.local.entity.Cuenta
import com.duoc.juntazaror.data.local.dao.CuentaDao
import com.duoc.juntazaror.model.Rol
import com.duoc.juntazaror.util.PasswordHasher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Cuenta::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cuentaDao(): CuentaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cuentas_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.cuentaDao()?.insert(
                                    Cuenta(
                                        name = "Directiva Demo",
                                        email = "directiva@demo.cl",
                                        password = PasswordHasher.hash("Demo1234"),
                                        rol = Rol.DIRECTIVA
                                    )
                                )
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
    }
}