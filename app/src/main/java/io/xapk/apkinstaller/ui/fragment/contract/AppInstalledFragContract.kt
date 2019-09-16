package io.xapk.apkinstaller.ui.fragment.contract

import android.content.Context
import io.xapk.apkinstaller.model.bean.ApiException
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.ui.base.BasePresenter
import io.xapk.apkinstaller.ui.base.BaseStepView

interface AppInstalledFragContract {
    interface StepView : BaseStepView {
        fun loadDataOnSubscribe()

        fun loadDataOnSuccess(appInfoList: List<AppInfo>)

        fun loadDataOnError(apiException: ApiException)
    }

    interface Presenter : BasePresenter<StepView> {
        fun getLoadInstalledApps(mContext: Context)
    }
}