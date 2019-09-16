package io.xapk.apkinstaller.utils.rx

import android.content.Context
import androidx.fragment.app.Fragment
import com.trello.rxlifecycle3.android.ActivityEvent
import com.trello.rxlifecycle3.android.FragmentEvent
import com.trello.rxlifecycle3.components.preference.RxPreferenceFragmentCompat
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import com.trello.rxlifecycle3.components.support.RxAppCompatDialogFragment
import com.trello.rxlifecycle3.components.support.RxFragment
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import io.xapk.apkinstaller.model.bean.ApiError

object RxObservableTransformer {

    fun <T> io_main(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
        }
    }

    fun <T> errorResult(mContext: Context): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.onErrorResumeNext(Function { Observable.error(ApiError.handleThrowable(mContext, it)) })
        }
    }

    fun <T> createData(t: T): Observable<T> {
        return Observable.create { observableEmitter ->
            if (!observableEmitter.isDisposed) {
                observableEmitter.onNext(t)
                observableEmitter.onComplete()
            }
        }
    }

    fun <T, C : Context> addDisposable(mContext: C): ObservableTransformer<T, T> {
        return addMultiDisposable(mContext)
    }

    private fun <T, C> addMultiDisposable(mContext: C): ObservableTransformer<T, T> {
        return when (mContext) {
            is RxFragment -> {
                mContext.bindToLifecycle<T>()
                mContext.bindUntilEvent<T>(FragmentEvent.DESTROY_VIEW)
            }
            is RxPreferenceFragmentCompat -> {
                mContext.bindToLifecycle<T>()
                mContext.bindUntilEvent<T>(FragmentEvent.DESTROY_VIEW)
            }
            is RxAppCompatActivity -> {
                mContext.bindToLifecycle<T>()
                mContext.bindUntilEvent<T>(ActivityEvent.DESTROY)
            }
            is RxAppCompatDialogFragment -> {
                mContext.bindToLifecycle<T>()
                mContext.bindUntilEvent<T>(FragmentEvent.DESTROY_VIEW)
            }
            else -> {
                ObservableTransformer { upstream -> upstream }
            }
        }
    }
}
