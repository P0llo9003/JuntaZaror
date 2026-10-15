package com.duoc.juntazaror.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.duoc.juntazaror.model.Rol

@Entity(tableName = "cuentas", indices = [Index(value = ["email"], unique = true)])
data class Cuenta(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val rol: Rol,
    val phonenumber: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)