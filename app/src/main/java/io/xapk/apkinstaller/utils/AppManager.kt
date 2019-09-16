package io.xapk.apkinstaller.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.text.TextUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.model.event.AppInstalledStatusEvent
import io.xapk.apkinstaller.model.event.EventManager
import io.xapk.apkinstaller.receiver.AppInstallBroadcastReceiver
import io.xapk.apkinstaller.utils.rx.RxObservableTransformer

class AppManager private constructor(val mContext: Context) :
    AppInstallBroadcastReceiver.AppStatusChangeLister {
    private val logTag: String by lazy { javaClass.simpleName }
    private val appInstallBroadcastReceiver by lazy { AppInstallBroadcastReceiver() }
    private val installedPackageNames = arrayListOf<String>()
    private val installedAppList = arrayListOf<AppInfo>()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var appManager: AppManager? = null

        fun getInstance(mContext: Context): AppManager {
            if (appManager == null) {
                synchronized(AppManager::class.java) {
                    if (appManager == null) {
                        appManager = AppManager(mContext)
                    }
                }
            }
            return appManager!!
        }

        fun initialize(application: Application) {
            getInstance(application)
        }
    }

    init {
        appInstallBroadcastReceiver.register(mContext)
        appInstallBroadcastReceiver.setAppStatusChangeLister(this)
        //updateInstalledAppInfoList()
    }

    @Suppress("ProtectedInFinal", "Unused")
    protected fun finalize() {
        appInstallBroadcastReceiver.unregister(mContext)
    }

    override fun onInstall(packName: String) {
        if (!installedPackageNames.contains(packName)) {
            installedPackageNames.add(packName)
        }
        existsApp(packName) ?: apply {
            AppUtils.getSingleInstalledAppInfo(mContext, packName)?.let { it1 ->
                installedAppList.add(it1)
                EventManager.post(
                    AppInstalledStatusEvent(AppInstalledStatusEvent.Status.INSTALL, it1)
                )
            }
        }
    }

    override fun onRemove(packName: String) {
        if (installedPackageNames.contains(packName)) {
            installedPackageNames.remove(packName)
        }
        existsApp(packName)?.let {
            installedAppList.remove(it)
            EventManager.post(AppInstalledStatusEvent(AppInstalledStatusEvent.Status.REMOVE, it))
        }
    }

    fun existsInstalledApp(packName: String) = installedPackageNames.contains(packName)

    private fun existsApp(packName: String): AppInfo? {
        var appInfo: AppInfo? = null
        installedAppList.forEach {
            if (it.packageName == packName) {
                appInfo = it
            }
        }
        return appInfo
    }

    fun getInstalledAppInfoList(): Observable<List<AppInfo>> {
        return Observable.create(ObservableOnSubscribe<List<AppInfo>> { emitter ->
            AppUtils.getInstalledAppsInfo(mContext)
                .apply {
                    updateInstalledAppInfoList(this)
                    if (!emitter.isDisposed) {
                        emitter.onNext(this)
                        emitter.onComplete()
                    }
                }
        })
            .compose(RxObservableTransformer.io_main())
            .compose(RxObservableTransformer.addDisposable(mContext))
            .compose(RxObservableTransformer.errorResult(mContext))
    }

    private fun updateInstalledAppInfoList(t: List<AppInfo>) {
        installedAppList.clear()
        installedPackageNames.clear()
        AppLogger.d(logTag, TextUtils.join("\n", t))
        installedAppList.addAll(t)
        installedAppList.forEach {
            installedPackageNames.add(it.packageName)
        }
    }
}