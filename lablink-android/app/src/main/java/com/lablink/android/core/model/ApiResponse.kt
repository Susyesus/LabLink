package com.lablink.android.core.model

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper matching the backend's ApiResponse<T>.
 * Every backend response follows this shape.
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("error") val error: ApiError?,
    @SerializedName("timestamp") val timestamp: String?
)

data class ApiError(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("details") val details: Any?
)
