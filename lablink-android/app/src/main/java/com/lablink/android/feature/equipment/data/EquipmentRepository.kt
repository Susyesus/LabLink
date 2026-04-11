package com.lablink.android.feature.equipment.data

import com.lablink.android.core.model.ApiResponse
import com.lablink.android.core.network.ApiService
import com.lablink.android.feature.equipment.model.*
import retrofit2.Callback

/**
 * Repository that encapsulates all equipment-related data operations.
 * Provides a clean API for Presenters to interact with equipment backend endpoints.
 */
class EquipmentRepository(private val apiService: ApiService) {

    fun getEquipment(
        search: String?,
        categoryId: String?,
        page: Int,
        limit: Int,
        callback: Callback<ApiResponse<EquipmentPageResponse>>
    ) {
        apiService.getEquipment(
            search = search,
            categoryId = categoryId,
            page = page,
            limit = limit
        ).enqueue(callback)
    }

    fun getCategories(callback: Callback<ApiResponse<CategoriesWrapper>>) {
        apiService.getCategories().enqueue(callback)
    }

    fun getEquipmentById(id: String, callback: Callback<ApiResponse<EquipmentItemWrapper>>) {
        apiService.getEquipmentById(id).enqueue(callback)
    }

    fun borrowEquipment(request: BorrowRequest, callback: Callback<ApiResponse<BorrowResponse>>) {
        apiService.borrowEquipment(request).enqueue(callback)
    }

    fun getMyBorrows(callback: Callback<ApiResponse<MyBorrowsResponse>>) {
        apiService.getMyBorrows().enqueue(callback)
    }
}
