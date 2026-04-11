package com.lablink.android.core.local

import android.content.Context
import android.content.SharedPreferences
import com.lablink.android.feature.auth.model.AuthData

/**
 * Manages user session data in SharedPreferences.
 * Stores JWT token, refresh token, and basic user info.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "lablink_session"
        private const val KEY_TOKEN = "token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_ID_NUMBER = "user_id_number"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    /**
     * Save the entire auth response after login or register.
     */
    fun saveSession(authData: AuthData) {
        prefs.edit().apply {
            putString(KEY_TOKEN, authData.token)
            putString(KEY_REFRESH_TOKEN, authData.refreshToken)
            putString(KEY_USER_ID, authData.user.id)
            putString(KEY_USER_EMAIL, authData.user.email)
            putString(KEY_USER_NAME, authData.user.name)
            putString(KEY_USER_ID_NUMBER, authData.user.idNumber)
            putString(KEY_USER_ROLE, authData.user.role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun getUserIdNumber(): String? = prefs.getString(KEY_USER_ID_NUMBER, null)

    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    /**
     * Update cached user name after profile edit.
     */
    fun updateUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    /**
     * Clear all session data on logout.
     */
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
