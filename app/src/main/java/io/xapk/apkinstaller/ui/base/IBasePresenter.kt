package io.xapk.apkinstaller.ui.base

open class IBasePresenter<T : BaseStepView> : BasePresenter<T> {
    var mRootView: T? = null
        private set

    override fun attachView(mRootView: T) {
        this.mRootView = mRootView
    }

    override fun detachView() = Unit
}