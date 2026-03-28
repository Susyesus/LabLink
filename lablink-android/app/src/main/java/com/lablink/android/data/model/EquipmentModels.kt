package com.lablink.android.data.model

import com.google.gson.annotations.SerializedName

// ─── Equipment ─────────────────────────────────────────────────

data class EquipmentPageResponse(
    @SerializedName("equipment") val equipment: List<EquipmentDto>,
    @SerializedName("pagination") val pagination: PaginationDto?
)

data class EquipmentDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("serialNumber") val serialNumber: String?,
    @SerializedName("status") val status: String,
    @SerializedName("category") val category: CategoryDto?,
    @SerializedName("imageUrl") val imageUrl: String?
)

data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?
)

data class PaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("limit") val limit: Int,
    @SerializedName("total") val total: Long,
    @SerializedName("pages") val pages: Int
)

data class CategoriesWrapper(
    @SerializedName("categories") val categories: List<CategoryDto>
)

// ─── Equipment Detail (wrapped in "item" key) ──────────────────

data class EquipmentItemWrapper(
    @SerializedName("item") val item: EquipmentDto
)

// ─── Borrow ────────────────────────────────────────────────────

data class BorrowRequest(
    @SerializedName("equipmentId") val equipmentId: String,
    @SerializedName("expectedReturnDate") val expectedReturnDate: String,
    @SerializedName("purpose") val purpose: String?
)

data class MyBorrowsResponse(
    @SerializedName("activeBorrows") val activeBorrows: List<BorrowRecordDto>
)

data class BorrowRecordDto(
    @SerializedName("id") val id: String,
    @SerializedName("itemName") val itemName: String,
    @SerializedName("equipmentId") val equipmentId: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("borrowDate") val borrowDate: String,
    @SerializedName("expectedReturnDate") val expectedReturnDate: String,
    @SerializedName("actualReturnDate") val actualReturnDate: String?,
    @SerializedName("status") val status: String,
    @SerializedName("purpose") val purpose: String?,
    @SerializedName("remarks") val remarks: String?
)

data class BorrowResponse(
    @SerializedName("record") val record: BorrowRecordDto?,
    @SerializedName("message") val message: String?
)
