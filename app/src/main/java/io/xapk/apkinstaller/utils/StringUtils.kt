package io.xapk.apkinstaller.utils

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.TextUtils
import androidx.annotation.ArrayRes

object StringUtils {

    fun getStringArray(mContext: Context, @ArrayRes stringId: Int): Array<String> {
        return mContext.resources.getStringArray(stringId)
    }

    fun fromHtml(source: String): CharSequence {
        return if (!TextUtils.isEmpty(source)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(source)
            }
        } else {
            source
        }
    }

    fun parseInt(num: String): Int? {
        return try {
            num.toInt()
        } catch (e: Exception) {
            null
        }
    }
}