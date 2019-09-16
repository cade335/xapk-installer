package io.xapk.apkinstaller.ui.fragment.presenter

import android.content.Context
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import io.xapk.apkinstaller.model.bean.ApiException
import io.xapk.apkinstaller.ui.base.IBasePresenter
import io.xapk.apkinstaller.ui.fragment.contract.InstallPackageFragContract
import io.xapk.apkinstaller.utils.asset.AssetUtils
import io.xapk.apkinstaller.utils.bean.ApkAssetBean
import io.xapk.apkinstaller.utils.rx.RxObservableTransformer
import io.xapk.apkinstaller.utils.rx.RxSubscriber

class InstallPackageFragPresenter : IBasePresenter<InstallPackageFragContract.StepView>(),
    InstallPackageFragContract.Presenter {
    override fun loadLocalInstallPackageApks(mContext: Context) {
        mRootView?.let {
            Observable.create(ObservableOnSubscribe<List<ApkAssetBean>> { emitter ->
                AssetUtils.getStorageDirApk(mContext)
                    .sortedByDescending { it2 -> it2.sortPosition }
                    .toList()
                    .apply {
                        if (!emitter.isDisposed) {
                            emitter.onNext(this)
                            emitter.onComplete()
                        }
                    }
            })
                .compose(RxObservableTransformer.io_main())
                .compose(RxObservableTransformer.addDisposable(mContext))
                .compose(RxObservableTransformer.errorResult(mContext))
                .subscribe(object : RxSubscriber<List<ApkAssetBean>>() {
                    override fun onSubscribe(d: Disposable) {
                        super.onSubscribe(d)
                        it.loadDataOnSubscribe()
                    }

                    override fun rxOnNext(t: List<ApkAssetBean>) {
                        it.loadDataOnSuccess(t)
                    }

                    override fun rxOnError(apiException: ApiException) {
                        it.loadDataOnError(apiException)
                    }
                })
        }
    }
}