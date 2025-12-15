package com.example.mascota.data.api

import com.example.mascota.data.storage.SessionStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    fun create(sessionStore: SessionStore): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()

            val builder = original.newBuilder()
                .addHeader("Accept", "application/json")

            // ⚠️ OJO: aquí se lee el token guardado
            val token = sessionStore.getTokenSync()
            android.util.Log.d("AUTH", "tokenSync len=${token?.length} start=${token?.take(12)}")
            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }

            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }

            chain.proceed(builder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            // Para depurar redirects (opcional, recomendado ahora):
            .followRedirects(false)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://apimascotas.jmacboy.com/api/") // ✅ con / al final
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
