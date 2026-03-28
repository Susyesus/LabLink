package com.lablink.android.data.api

import com.lablink.android.data.model.*
import retrofit2.Call
import retrofit2.http.*

/**
 * Retrofit API interface — maps every backend endpoint.
 * Protected routes automatically get Bearer token via AuthInterceptor.
 */
interface ApiService {

    // ─── Auth (Public) ─────────────────────────────────────────

    @POST("api/v1/auth/register")
    fun register(@Body request: RegisterRequest): Call<ApiResponse<AuthData>>

    @POST("api/v1/auth/login")
    fun login(@Body request: LoginRequest): Call<ApiResponse<AuthData>>

    @POST("api/v1/auth/logout")
    fun logout(): Call<ApiResponse<Void>>

    // ─── User Profile (Protected) ──────────────────────────────

    @GET("api/v1/users/me")
    fun getProfile(): Call<ApiResponse<UserProfile>>

    @PUT("api/v1/users/me")
    fun updateProfile(@Body request: UpdateProfileRequest): Call<ApiResponse<UserProfile>>

    @PUT("api/v1/users/me/password")
    fun changePassword(@Body request: ChangePasswordRequest): Call<ApiResponse<Void>>

    // ─── Equipment (Public GET, Admin POST/PUT/DELETE) ─────────

    @GET("api/v1/equipment")
    fun getEquipment(
        @Query("search") search: String? = null,
        @Query("status") status: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Call<ApiResponse<EquipmentPageResponse>>

    @GET("api/v1/equipment/categories")
    fun getCategories(): Call<ApiResponse<CategoriesWrapper>>

    @GET("api/v1/equipment/{id}")
    fun getEquipmentById(@Path("id") id: String): Call<ApiResponse<EquipmentItemWrapper>>

    // ─── Borrow (Protected) ────────────────────────────────────

    @POST("api/v1/borrow")
    fun borrowEquipment(@Body request: BorrowRequest): Call<ApiResponse<BorrowResponse>>

    @GET("api/v1/borrow/my-items")
    fun getMyBorrows(): Call<ApiResponse<MyBorrowsResponse>>
}
