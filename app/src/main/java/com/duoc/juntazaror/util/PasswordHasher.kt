package com.duoc.juntazaror.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    private const val ALGORITMO = "PBKDF2WithHmacSHA256"
    private const val ITERACIONES = 100_000
    private const val LARGO_SALT = 16
    private const val LARGO_HASH_BITS = 256

    fun hash(clave: String): String{
        val salt = ByteArray(LARGO_SALT).also { SecureRandom().nextBytes(it)}
        val hash = derivar(clave, salt, ITERACIONES)
        return "$ITERACIONES:${codificar(salt)}:${codificar(hash)}"
    }

    fun verificar(clave: String, guardado: String): Boolean {
        val partes = guardado.split(":")
        if (partes.size !=3)return false
        val iteraciones = partes[0].toIntOrNull() ?: return false
        return try{
            val salt = Base64.getDecoder().decode(partes[1])
            val esperado = Base64.getDecoder().decode(partes[2])
            val calculado = derivar(clave, salt, iteraciones)
            MessageDigest.isEqual(esperado, calculado)
        }catch(e: IllegalArgumentException){
            false
        }
    }

    private fun derivar(clave: String, salt: ByteArray, iteraciones: Int): ByteArray {
        val spec = PBEKeySpec(clave.toCharArray(), salt, iteraciones, LARGO_HASH_BITS)
        return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded
    }

    private fun codificar(bytes: ByteArray): String =
        Base64.getEncoder().encodeToString(bytes)

}