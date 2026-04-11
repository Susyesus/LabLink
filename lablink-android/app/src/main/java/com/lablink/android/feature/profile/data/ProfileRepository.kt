package com.lablink.android.feature.profile.data

import com.lablink.android.core.local.SessionManager
import com.lablink.android.core.model.ApiResponse
import com.lablink.android.core.network.ApiService
import com.lablink.android.feature.profile.model.ChangePasswordRequest
import com.lablink.android.feature.profile.model.UpdateProfileRequest
import com.lablink.android.feature.profile.model.UserProfile
import retrofit2.Callback

/**
 * Repository that encapsulates all profile-related data operations.
 * Provides a clean API for Presenters to interact with user profile backend endpoints.
 */
class ProfileRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {

    fun getProfile(callback: Callback<ApiResponse<UserProfile>>) {
        apiService.getProfile().enqueue(callback)
    }

    fun updateProfile(request: UpdateProfileRequest, callback: Callback<ApiResponse<UserProfile>>) {
        apiService.updateProfile(request).enqueue(callback)
    }

    fun changePassword(request: ChangePasswordRequest, callback: Callback<ApiResponse<Void>>) {
        apiService.changePassword(request).enqueue(callback)
    }

    fun logout(callback: Callback<ApiResponse<Void>>) {
        apiService.logout().enqueue(callback)
    }

    fun clearSession() {
        sessionManager.clearSession()
    }

    fun updateUserName(name: String) {
        sessionManager.updateUserName(name)
    }

    fun getUserName(): String? = sessionManager.getUserName()

    fun getUserIdNumber(): String? = sessionManager.getUserIdNumber()

    fun getAuthToken(): String? = sessionManager.getToken()

    fun getProfilePhotoUrl(): String = "http://10.0.2.2:8080/api/v1/users/me/photo"
}
