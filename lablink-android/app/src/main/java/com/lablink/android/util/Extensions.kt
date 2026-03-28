package com.lablink.android.util

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.lablink.android.data.model.ApiError
import com.lablink.android.data.model.ApiResponse
import retrofit2.Response

// ─── View Extensions ───────────────────────────────────────────

fun View.show() { visibility = View.VISIBLE }
fun View.hide() { visibility = View.GONE }
fun View.invisible() { visibility = View.INVISIBLE }

// ─── Toast / Snackbar ──────────────────────────────────────────

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun View.snackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, message, duration).show()
}

fun View.snackbarError(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG)
        .setBackgroundTint(0xFFD32F2F.toInt())
        .setTextColor(0xFFFFFFFF.toInt())
        .show()
}

fun View.snackbarSuccess(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(0xFF2E7D32.toInt())
        .setTextColor(0xFFFFFFFF.toInt())
        .show()
}

// ─── Error Parsing ─────────────────────────────────────────────

/**
 * Parses the error body from a failed Retrofit response into a human-readable message.
 */
fun <T> parseErrorMessage(response: Response<ApiResponse<T>>): String {
    return try {
        val errorBody = response.errorBody()?.string()
        if (errorBody != null) {
            val errorResponse = Gson().fromJson(errorBody, ApiResponse::class.java)
            errorResponse.error?.message ?: "An error occurred"
        } else {
            getDefaultErrorMessage(response.code())
        }
    } catch (e: Exception) {
        getDefaultErrorMessage(response.code())
    }
}

fun getDefaultErrorMessage(code: Int): String {
    return when (code) {
        400 -> "Invalid request. Please check your input."
        401 -> "Session expired. Please log in again."
        403 -> "You don't have permission to perform this action."
        404 -> "Requested resource not found."
        409 -> "This resource already exists."
        500 -> "Server error. Please try again later."
        else -> "An unexpected error occurred (code: $code)."
    }
}
