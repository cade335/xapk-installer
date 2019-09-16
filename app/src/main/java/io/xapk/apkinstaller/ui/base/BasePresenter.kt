package io.xapk.apkinstaller.ui.base

interface BasePresenter<in V : BaseStepView> {
    fun attachView(mRootView: V)

    fun detachView()
}