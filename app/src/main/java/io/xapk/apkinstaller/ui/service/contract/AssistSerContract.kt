package io.xapk.apkinstaller.ui.service.contract

import android.content.Context
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.ui.base.BasePresenter
import io.xapk.apkinstaller.ui.base.BaseStepView

interface AssistSerContract {
    interface StepView : BaseStepView
    interface Presenter : BasePresenter<StepView> {
        fun apkExport(mContext: Context, appInfo: AppInfo)

        fun xApkOutputZip(mContext: Context, appInfo: AppInfo)

        fun apksExport(mContext: Context, appInfo: AppInfo)
    }
}