package com.lablink.android.data.model

import com.google.gson.annotations.SerializedName

// ─── User Profile ──────────────────────────────────────────────

data class UserProfile(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("idNumber") val idNumber: String?,
    @SerializedName("role") val role: String,
    @SerializedName("hasPhoto") val hasPhoto: Boolean,
    @SerializedName("createdAt") val createdAt: String?
)

// ─── Update Profile Request ────────────────────────────────────

data class UpdateProfileRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("idNumber") val idNumber: String?
)

// ─── Change Password Request ───────────────────────────────────

data class ChangePasswordRequest(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)
