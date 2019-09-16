package io.xapk.apkinstaller.utils.rx

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.xapk.apkinstaller.model.bean.ApiError
import io.xapk.apkinstaller.model.bean.ApiException

abstract class RxSubscriber<T> : Observer<T> {
    override fun onSubscribe(d: Disposable) {

    }

    override fun onNext(t: T) {
        rxOnNext(t)
    }

    override fun onError(e: Throwable) {
        e.printStackTrace()
        if (e is ApiException) {
            rxOnError(e)
        } else {
            rxOnError(ApiException(e, ApiError.UNKNOWN))
        }
    }

    override fun onComplete() {

    }

    abstract fun rxOnNext(t: T)

    abstract fun rxOnError(apiException: ApiException)
}
