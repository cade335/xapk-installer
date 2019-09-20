package io.xapk.apkinstaller.utils

import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.app.App
import io.xapk.apkinstaller.app.Settings
import java.util.*

object LocaleUtils {

    val appLocal: Locale
        get() {
            val localValue = Settings.languageValue
            return if (TextUtils.equals(localValue, App.mContext.getString(R.string.language_auto_value))) {
                systemLocal
            } else {
                forLanguageTag(localValue)
            }
        }

    val systemLocal: Locale
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                LocaleList.getDefault().get(0)
            } else {
                Locale.getDefault()
            }
        }

    val systemLocalTag: String
        get() {
            return toLanguageTag(systemLocal)
        }

    val appLocalTag: String
        get() {
            return toLanguageTag(appLocal)
        }

    /**
     * if (Build.VERSION.SDK_INT >= 21) {
     *   locale.toLanguageTag()
     *  }
     */
    private fun toLanguageTag(locale: Locale): String {
        val language = locale.language
        val country = locale.country
        val variant = locale.variant
        return when {
            TextUtils.isEmpty(country) -> language
            TextUtils.isEmpty(variant) -> "$language-$country"
            else -> "$language-$country-$variant"
        }
    }

    private fun forLanguageTag(languageTag: String): Locale {
        return if (Build.VERSION.SDK_INT >= 21) {
            Locale.forLanguageTag(languageTag)
        } else {
            val parts = languageTag.split("-".toRegex()).toTypedArray()
            if (parts.size == 1)
                Locale(parts[0])
            else if (parts.size == 2 || parts.size == 3 && parts[2].startsWith("#"))
                Locale(parts[0], parts[1])
            else
                Locale(parts[0], parts[1], parts[2])
        }
    }
}
