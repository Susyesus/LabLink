package com.lablink.android.data.api

import com.lablink.android.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Centralized Retrofit client singleton.
 *
 * Uses 10.0.2.2 because the Android emulator maps that to the host machine's
 * localhost where the Spring Boot backend runs on port 8080.
 */
object RetrofitClient {

    // For emulator → host localhost
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private var retrofit: Retrofit? = null
    private var sessionManager: SessionManager? = null

    fun init(sessionManager: SessionManager) {
        this.sessionManager = sessionManager
        retrofit = null // Force rebuild with new session manager
    }

    val instance: ApiService
        get() {
            if (retrofit == null) {
                val logging = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

                val clientBuilder = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(logging)

                // Add auth interceptor if session manager is available
                sessionManager?.let {
                    clientBuilder.addInterceptor(AuthInterceptor(it))
                }

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(clientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!.create(ApiService::class.java)
        }
}
