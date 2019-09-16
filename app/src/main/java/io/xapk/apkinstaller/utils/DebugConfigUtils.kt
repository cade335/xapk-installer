package io.xapk.apkinstaller.utils

import android.app.Application
import android.os.StrictMode
import androidx.loader.app.LoaderManager
import com.squareup.leakcanary.LeakCanary
import io.xapk.apkinstaller.BuildConfig

object DebugConfigUtils {
    fun init(application: Application) {
        if (!BuildConfig.IS_RELEASE) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    //.detectAll()
                    //.detectDiskReads()
                    //.detectDiskWrites()
                    .detectCustomSlowCalls()
                    //.detectResourceMismatches()
                    .detectNetwork() 
                    //.penaltyDialog()
                    //.penaltyDeath()
                    //.penaltyFlashScreen()
                    //.penaltyDeathOnNetwork()
                    //.penaltyDropBox()
                    .penaltyLog()
                    .permitDiskReads()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    //.detectAll()
                    .detectActivityLeaks()
                    //.detectCleartextNetwork()
                    //.detectFileUriExposure()
                    .detectLeakedClosableObjects()
                    .detectLeakedRegistrationObjects()
                    .detectLeakedSqlLiteObjects()
                    //.setClassInstanceLimit(MyClass.class, 2)
                    .penaltyLog()
                    //.penaltyDeath()
                    .build()
            )
            LoaderManager.enableDebugLogging(true)
        }
        if (!LeakCanary.isInAnalyzerProcess(application)) {
            LeakCanary.install(application)
        }
        AppLogger.initDebug()
    }
}