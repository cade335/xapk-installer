package io.xapk.apkinstaller.utils

import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.app.App

object Settings {
    val languageValue by lazy { App.mContext.getString(R.string.language_auto_value) }
}