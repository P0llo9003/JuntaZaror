package com.duoc.juntazaror.remote.externa

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

// API EXTERNA: Nager.Date -> GET https://date.nager.at/api/v3/PublicHolidays/2026/CL
interface FeriadosApi {
    @GET("api/v3/PublicHolidays/{anio}/{pais}")
    suspend fun feriados(
        @Path("anio") anio: Int,
        @Path("pais") pais: String = "CL"
    ): List<FeriadoDto>
}

data class FeriadoDto(
    val date: String,        // "2026-09-18"
    val localName: String,   // "Fiestas Patrias"
    val name: String,
    val global: Boolean?     // true = nacional, false = regional
)

object FeriadosRetrofit {
    val api: FeriadosApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://date.nager.at/")
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeriadosApi::class.java)
    }
}