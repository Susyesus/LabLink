package com.lablink.android.data.api

import okhttp3.Interceptor
import okhttp3.Response
import com.lablink.android.data.local.SessionManager

/**
 * OkHttp Interceptor that injects the Bearer token into every request
 * EXCEPT public auth endpoints (login, register).
 */
class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // Skip auth for public endpoints
        if (path.contains("/auth/login") || path.contains("/auth/register")) {
            return chain.proceed(originalRequest)
        }

        val token = sessionManager.getToken()
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
