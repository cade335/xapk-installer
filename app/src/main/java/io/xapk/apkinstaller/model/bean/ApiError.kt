package io.xapk.apkinstaller.model.bean

import android.content.Context
import io.xapk.apkinstaller.R

object ApiError {
    const val UNKNOWN = 1004

    fun handleThrowable(mContext: Context, e: Throwable): ApiException {
        return ApiException(e, UNKNOWN, mContext.getString(R.string.error_unknown))
    }
}
