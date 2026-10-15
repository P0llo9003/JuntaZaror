package com.duoc.juntazaror.model

data class RegistroModel(
    val name: String = "",
    val email: String = "",
    val passwordHush: String = "",
    val acceptTerms: Boolean = false
)
