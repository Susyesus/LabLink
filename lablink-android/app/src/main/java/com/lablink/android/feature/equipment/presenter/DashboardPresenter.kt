package com.lablink.android.feature.equipment.presenter

import android.content.Context
import com.lablink.android.core.base.BasePresenter
import com.lablink.android.core.local.SessionManager
import com.lablink.android.core.model.ApiResponse
import com.lablink.android.core.util.NetworkUtils
import com.lablink.android.core.util.parseErrorMessage
import com.lablink.android.feature.equipment.contract.DashboardContract
import com.lablink.android.feature.equipment.data.EquipmentRepository
import com.lablink.android.feature.equipment.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Presenter for the Dashboard screen.
 * Manages equipment loading, category filtering, search, and equipment detail display.
 */
class DashboardPresenter(
    private val repository: EquipmentRepository,
    private val sessionManager: SessionManager,
    private val context: Context
) : BasePresenter<DashboardContract.View>(), DashboardContract.Presenter {

    private var selectedCategoryId: String? = null
    private var searchQuery: String? = null

    override fun loadData() {
        loadCategories()
        loadEquipment()
    }

    private fun loadCategories() {
        repository.getCategories(object : Callback<ApiResponse<CategoriesWrapper>> {
            override fun onResponse(
                call: Call<ApiResponse<CategoriesWrapper>>,
                response: Response<ApiResponse<CategoriesWrapper>>
            ) {
                if (!isViewAttached) return
                if (response.isSuccessful && response.body()?.success == true) {
                    val categories = response.body()!!.data?.categories ?: emptyList()
                    view?.showCategories(categories)
                }
            }

            override fun onFailure(call: Call<ApiResponse<CategoriesWrapper>>, t: Throwable) {
                // Categories are non-critical, silently fail
            }
        })
    }

    override fun loadEquipment() {
        view?.showLoading(true)

        repository.getEquipment(
            search = searchQuery,
            categoryId = selectedCategoryId,
            page = 1,
            limit = 50,
            callback = object : Callback<ApiResponse<EquipmentPageResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<EquipmentPageResponse>>,
                    response: Response<ApiResponse<EquipmentPageResponse>>
                ) {
                    if (!isViewAttached) return
                    view?.showLoading(false)

                    if (response.isSuccessful && response.body()?.success == true) {
                        val items = response.body()!!.data?.equipment ?: emptyList()
                        view?.showEquipment(items)
                        view?.showEmptyState(items.isEmpty())
                    } else if (response.code() == 401) {
                        view?.handleUnauthorized()
                    } else {
                        view?.showError(parseErrorMessage(response))
                    }
                }

                override fun onFailure(call: Call<ApiResponse<EquipmentPageResponse>>, t: Throwable) {
                    if (!isViewAttached) return
                    view?.showLoading(false)

                    if (!NetworkUtils.isConnected(context)) {
                        view?.showNetworkError()
                    } else {
                        view?.showError("A network error occurred. Please try again.")
                    }
                }
            }
        )
    }

    override fun onCategorySelected(categoryId: String?) {
        selectedCategoryId = categoryId
        loadEquipment()
    }

    override fun onSearchQueryChanged(query: String?) {
        searchQuery = query
        loadEquipment()
    }

    override fun onEquipmentClicked(equipment: EquipmentDto) {
        view?.showEquipmentDetail(equipment)
    }

    override fun getUserName(): String {
        return sessionManager.getUserName() ?: "Student"
    }
}
