package io.xapk.apkinstaller.utils.toast

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import androidx.annotation.StringRes
import es.dmoral.toasty.Toasty

object SimpleToast {

    @SuppressLint("ShowToast")
    fun defaultShow(mContext: Context, @StringRes messageId: Int, duration: Duration = Duration.SHORT) {
        defaultShow(mContext, mContext.getString(messageId), duration)
    }

    @SuppressLint("ShowToast")
    fun defaultShow(mContext: Context, message: String?, duration: Duration = Duration.SHORT) {
        if (!TextUtils.isEmpty(message)) {
            Toasty.normal(mContext, message!!, duration.time).show()
        }
    }

    fun info(mContext: Context, message: String?) {
        if (!TextUtils.isEmpty(message)) {
            Toasty.info(mContext, message!!).show()
        }
    }

    fun success(mContext: Context, message: String?) {
        if (!TextUtils.isEmpty(message)) {
            Toasty.success(mContext, message!!).show()
        }
    }

    fun error(mContext: Context, message: String?) {
        if (!TextUtils.isEmpty(message)) {
            Toasty.error(mContext, message!!).show()
        }
    }

    fun warning(mContext: Context, message: String?) {
        if (!TextUtils.isEmpty(message)) {
            Toasty.warning(mContext, message!!).show()
        }
    }
}
