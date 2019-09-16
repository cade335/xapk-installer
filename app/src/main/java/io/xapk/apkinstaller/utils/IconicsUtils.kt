package io.xapk.apkinstaller.utils

import android.app.Application
import android.content.Context
import android.graphics.Color
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import com.mikepenz.iconics.Iconics
import com.mikepenz.iconics.IconicsDrawable
import com.mikepenz.iconics.typeface.IIcon

object IconicsUtils {
    fun init(application: Application) {
        Iconics.init(application)
        Iconics.registerFont(GoogleMaterial())
    }

    fun getIcon(mContext: Context, icon: IIcon): IconicsDrawable {
        return IconicsDrawable(mContext, icon)
    }

    fun getActionBarIcon(mContext: Context, icon: IIcon): IconicsDrawable {
        return IconicsDrawable(mContext, icon).color(Color.WHITE).sizeDp(18)
    }
}