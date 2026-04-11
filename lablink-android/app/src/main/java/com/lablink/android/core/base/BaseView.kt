package com.lablink.android.core.base

/**
 * Base View interface for all MVP View contracts.
 * Every feature's View contract should extend this interface.
 */
interface BaseView {
    fun showLoading(show: Boolean)
    fun showError(message: String)
    fun showNetworkError()
    fun handleUnauthorized()
}
