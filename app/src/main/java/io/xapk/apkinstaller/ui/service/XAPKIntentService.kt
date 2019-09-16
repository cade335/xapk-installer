package io.xapk.apkinstaller.ui.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import io.xapk.apkinstaller.app.App.Companion.mContext
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.ui.service.contract.AssistSerContract
import io.xapk.apkinstaller.ui.service.presenter.AssistSerPresenter

class XAPKIntentService : JobIntentService(), AssistSerContract.StepView {
    private val assistSerPresenter by lazy { AssistSerPresenter() }

    object ActionType {
        const val ACTION_XAPK_OUTPUT_ZIP = "xapk_output_zip"
        const val ACTION_APK_EXPORT = "apk_export"
        const val ACTION_APKS_EXPORT = "apks_export"
    }

    companion object {
        private val serviceId by lazy { XAPKIntentService::javaClass.name.hashCode() }
        private const val PARAM_KEY_DATA = "param_key_data"

        fun enqueueWorkService(mContext: Context, intent: Intent) {
            enqueueWork(mContext, XAPKIntentService::class.java, serviceId, intent)
        }

        fun startApkExport(mContext: Context, appInfo: AppInfo): Intent {
            return Intent(mContext, XAPKIntentService::class.java).apply {
                this.action = ActionType.ACTION_APK_EXPORT
                this.putExtra(PARAM_KEY_DATA, appInfo)
            }
        }

        fun startApksExport(mContext: Context, appInfo: AppInfo): Intent {
            return Intent(mContext, XAPKIntentService::class.java).apply {
                this.action = ActionType.ACTION_APKS_EXPORT
                this.putExtra(PARAM_KEY_DATA, appInfo)
            }
        }

        fun startXApkOutPutZip(mContext: Context, appInfo: AppInfo): Intent {
            return Intent(mContext, XAPKIntentService::class.java).apply {
                this.action = ActionType.ACTION_XAPK_OUTPUT_ZIP
                this.putExtra(PARAM_KEY_DATA, appInfo)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        assistSerPresenter.attachView(this)
    }

    override fun onHandleWork(intent: Intent) {
        when (intent.action) {
            ActionType.ACTION_APK_EXPORT -> {
                intent.getParcelableExtra<AppInfo>(PARAM_KEY_DATA)?.let {
                    assistSerPresenter.apkExport(mContext, it)
                }
            }
            ActionType.ACTION_XAPK_OUTPUT_ZIP -> {
                intent.getParcelableExtra<AppInfo>(PARAM_KEY_DATA)?.let {
                    assistSerPresenter.xApkOutputZip(mContext, it)
                }
            }
            ActionType.ACTION_APKS_EXPORT -> {
                intent.getParcelableExtra<AppInfo>(PARAM_KEY_DATA)?.let {
                    assistSerPresenter.apksExport(mContext, it)
                }
            }
        }
    }

    override fun onDestroy() {
        assistSerPresenter.detachView()
        super.onDestroy()
    }
}