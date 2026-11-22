package com.duoc.juntazaror.model

data class RegistroModel(
    val name: String = "",
    val email: String = "",
    val passwordHush: String = "",
    val phone: String? = null,
    val rol: Rol? = null,
    val acceptTerms: Boolean = false
)
