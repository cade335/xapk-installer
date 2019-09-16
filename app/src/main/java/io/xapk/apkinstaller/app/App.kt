package io.xapk.apkinstaller.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import io.xapk.apkinstaller.utils.AppManager
import io.xapk.apkinstaller.utils.DebugConfigUtils
import io.xapk.apkinstaller.utils.IconicsUtils
import io.xapk.apkinstaller.utils.LanguageUtils

class App : MultiDexApplication() {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
            private set
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: App
            private set
    }

    override fun attachBaseContext(base: Context) {
        mContext = base
        super.attachBaseContext(LanguageUtils.attachBaseContext(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        LanguageUtils.updateAppLanguage(this)
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        instance = this
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        IconicsUtils.init(this)
        DebugConfigUtils.init(this)
        AppManager.initialize(this)
    }
}