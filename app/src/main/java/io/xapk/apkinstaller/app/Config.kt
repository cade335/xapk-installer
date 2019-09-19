package io.xapk.apkinstaller.app

import io.xapk.apkinstaller.R

/**
 * @author xiongke
 * @date 2019-09-19
 */
object Config {
    const val XAPK_VERSION = 2
}

object Settings {
    val languageValue by lazy { App.mContext.getString(R.string.language_auto_value) }
}