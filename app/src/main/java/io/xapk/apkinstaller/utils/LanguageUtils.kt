package io.xapk.apkinstaller.utils

import android.content.Context
import android.os.Build
import android.os.LocaleList

object LanguageUtils {
    fun updateAppLanguage(mContext: Context) {
        val resources = mContext.resources
        val configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(LocaleUtils.appLocal)
        } else {
            configuration.locale = LocaleUtils.appLocal
        }
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun attachBaseContext(mContext: Context): Context {
        val resources = mContext.resources
        val appLocale = LocaleUtils.appLocal
        val configuration = resources.configuration
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                configuration.setLocale(appLocale)
                configuration.locales = LocaleList(appLocale).apply {
                    LocaleList.setDefault(this)
                }
                mContext.createConfigurationContext(configuration)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> {
                configuration.setLocale(appLocale)
                mContext.createConfigurationContext(configuration)
            }
            else -> {
                configuration.locale = appLocale
                resources.updateConfiguration(configuration, resources.displayMetrics)
                mContext
            }
        }
    }
}