package com.lablink.android.core.base

/**
 * Abstract base Presenter with safe View attach/detach lifecycle.
 * Prevents memory leaks by nullifying the View reference on detach.
 *
 * Usage:
 *   - Call [attachView] in Activity.onCreate
 *   - Call [detachView] in Activity.onDestroy
 *   - Always check [isViewAttached] before calling view methods in callbacks
 */
abstract class BasePresenter<V : BaseView> {

    protected var view: V? = null
        private set

    fun attachView(view: V) {
        this.view = view
    }

    fun detachView() {
        view = null
    }

    protected val isViewAttached: Boolean
        get() = view != null
}
