package com.lablink.android.feature.auth.model

import com.google.gson.annotations.SerializedName

// ─── Auth Request Models ───────────────────────────────────────

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    @SerializedName("idNumber") val idNumber: String?
)

// ─── Auth Response Models ──────────────────────────────────────

data class AuthData(
    @SerializedName("user") val user: UserDto,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("idNumber") val idNumber: String?,
    @SerializedName("role") val role: String
)
