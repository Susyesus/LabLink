package com.lablink.android.feature.equipment.contract

import com.lablink.android.core.base.BaseView
import com.lablink.android.feature.equipment.model.CategoryDto
import com.lablink.android.feature.equipment.model.EquipmentDto

/**
 * MVP Contract for the Dashboard screen.
 * Defines the communication interface between DashboardActivity (View) and DashboardPresenter.
 */
interface DashboardContract {

    interface View : BaseView {
        /** Display the list of equipment items */
        fun showEquipment(items: List<EquipmentDto>)

        /** Show or hide the empty state when no equipment matches */
        fun showEmptyState(show: Boolean)

        /** Populate category filter chips */
        fun showCategories(categories: List<CategoryDto>)

        /** Show equipment detail dialog */
        fun showEquipmentDetail(equipment: EquipmentDto)

        /** Set the welcome user name */
        fun setUserName(name: String)
    }

    interface Presenter {
        /** Load both categories and equipment for initial display */
        fun loadData()

        /** Reload equipment based on current filters */
        fun loadEquipment()

        /** Handle category chip selection */
        fun onCategorySelected(categoryId: String?)

        /** Handle search text changes */
        fun onSearchQueryChanged(query: String?)

        /** Handle equipment item click */
        fun onEquipmentClicked(equipment: EquipmentDto)

        /** Get the current user's display name */
        fun getUserName(): String
    }
}
