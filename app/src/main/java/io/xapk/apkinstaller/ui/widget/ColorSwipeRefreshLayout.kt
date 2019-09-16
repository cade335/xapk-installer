package io.xapk.apkinstaller.ui.widget

import android.content.Context
import android.util.AttributeSet
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.utils.ColorUtils

open class ColorSwipeRefreshLayout : SwipeRefreshLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onFinishInflate() {
        super.onFinishInflate()
        setColorSchemeColors(ColorUtils.getThemeColor(context, R.attr.colorPrimary))
    }
}
