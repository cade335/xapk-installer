package io.xapk.apkinstaller.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt

object ColorUtils {
    @ColorInt
    fun getThemeColor(mContext: Context, attrRes: Int): Int {
        val typedArray = mContext.theme.obtainStyledAttributes(intArrayOf(attrRes))
        val color = typedArray.getColor(0, Color.TRANSPARENT)
        typedArray.recycle()
        return color
    }
}
