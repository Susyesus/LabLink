package com.lablink.android.feature.auth.data

import com.lablink.android.core.local.SessionManager
import com.lablink.android.core.model.ApiResponse
import com.lablink.android.core.network.ApiService
import com.lablink.android.core.network.RetrofitClient
import com.lablink.android.feature.auth.model.AuthData
import com.lablink.android.feature.auth.model.LoginRequest
import com.lablink.android.feature.auth.model.RegisterRequest
import retrofit2.Callback

/**
 * Repository that encapsulates all auth-related data operations.
 * Provides a clean API for Presenters to interact with auth backend endpoints.
 */
class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    fun login(request: LoginRequest, callback: Callback<ApiResponse<AuthData>>) {
        apiService.login(request).enqueue(callback)
    }

    fun register(request: RegisterRequest, callback: Callback<ApiResponse<AuthData>>) {
        apiService.register(request).enqueue(callback)
    }

    fun saveSession(authData: AuthData) {
        sessionManager.saveSession(authData)
        // Re-init retrofit with new token
        RetrofitClient.init(sessionManager)
    }

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
}
