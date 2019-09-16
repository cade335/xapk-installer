package io.xapk.apkinstaller.ui.fragment.presenter

import android.content.Context
import io.reactivex.disposables.Disposable
import io.xapk.apkinstaller.model.bean.ApiException
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.ui.base.IBasePresenter
import io.xapk.apkinstaller.ui.fragment.contract.AppInstalledFragContract
import io.xapk.apkinstaller.utils.AppManager
import io.xapk.apkinstaller.utils.rx.RxSubscriber

class AppInstalledFragPresenter : IBasePresenter<AppInstalledFragContract.StepView>(), AppInstalledFragContract.Presenter {
    override fun getLoadInstalledApps(mContext: Context) {
        mRootView?.let { it1 ->
            AppManager.getInstance(mContext)
                    .getInstalledAppInfoList()
                    .subscribe(object : RxSubscriber<List<AppInfo>>() {
                        override fun onSubscribe(d: Disposable) {
                            super.onSubscribe(d)
                            it1.loadDataOnSubscribe()
                        }

                        override fun rxOnNext(t: List<AppInfo>) {
                            t.asSequence()
                                    .filter { !it.isSystemApp }
                                    .sortedByDescending { it.lastUpdateTime }
                                    .toList()
                                    .apply {
                                        it1.loadDataOnSuccess(this)
                                    }
                        }

                        override fun rxOnError(apiException: ApiException) {
                            it1.loadDataOnError(apiException)
                        }
                    })
        }
    }
}