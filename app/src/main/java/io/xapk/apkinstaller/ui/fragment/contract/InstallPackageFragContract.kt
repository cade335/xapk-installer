package io.xapk.apkinstaller.ui.fragment.contract

import android.content.Context
import io.xapk.apkinstaller.model.bean.ApiException
import io.xapk.apkinstaller.ui.base.BasePresenter
import io.xapk.apkinstaller.ui.base.BaseStepView
import io.xapk.apkinstaller.utils.bean.ApkAssetBean

interface InstallPackageFragContract {
    interface StepView : BaseStepView {
        fun loadDataOnSubscribe()

        fun loadDataOnSuccess(apkAssetBeanList: List<ApkAssetBean>)

        fun loadDataOnError(apiException: ApiException)
    }

    interface Presenter : BasePresenter<StepView> {
        fun loadLocalInstallPackageApks(mContext: Context)
    }
}