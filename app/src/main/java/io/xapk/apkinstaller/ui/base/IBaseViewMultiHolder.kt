package io.xapk.apkinstaller.ui.base

import android.view.View
import com.chad.library.adapter.base.BaseViewHolder

abstract class IBaseViewMultiHolder<T>(itemView: View) : BaseViewHolder(itemView) {
    open fun updateView(dateItem: T) {
        itemView.tag = dateItem
    }

    protected fun getItemTagDate(): Any? = itemView.tag
}
