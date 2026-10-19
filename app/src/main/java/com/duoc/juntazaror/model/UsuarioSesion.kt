package com.duoc.juntazaror.model

data class UsuarioSesion(
    val id: Int,
    val nombre: String,
    val email: String,
    val rol: Rol
)
