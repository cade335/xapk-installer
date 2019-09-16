package io.xapk.apkinstaller.utils

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import io.xapk.apkinstaller.BuildConfig

object AppLogger {
    private val tag = AppLogger::class.java.simpleName

    fun initDebug() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)         // (Optional) How many method line to show. Default 2
                .methodOffset(5)        // (Optional) Hides internal method calls up to offset. Default 5
                .tag(tag)   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return !BuildConfig.IS_RELEASE
            }
        })
    }

    fun w(message: String, vararg `object`: Any? = emptyArray()) {
        Logger.w(message, *`object`)
    }

    fun w(tag: String, message: String, vararg `object`: Any? = emptyArray()) {
        Logger.t(tag).w(message, *`object`)
    }

    fun d(message: String, vararg `object`: Any? = emptyArray()) {
        Logger.d(message, *`object`)
    }

    fun d(tag: String, message: String, vararg `object`: Any? = emptyArray()) {
        Logger.t(tag).d(message, *`object`)
    }

    fun i(message: String, vararg `object`: Any? = emptyArray()) {
        Logger.i(message, *`object`)
    }

    fun i(tag: String, message: String, vararg `object`: Any? = emptyArray()) {
        Logger.t(tag).i(message, *`object`)
    }

    fun e(message: String, vararg `object`: Any? = emptyArray()) {
        Logger.d(message, *`object`)
    }

    fun e(throwable: Throwable, message: String, vararg `object`: Any? = emptyArray()) {
        Logger.e(throwable, message, *`object`)
    }

    fun e(tag: String, message: String, vararg `object`: Any? = emptyArray()) {
        Logger.t(tag).e(message, *`object`)
    }

    fun json(json: String) {
        Logger.json(json)
    }

    fun xml(xml: String) {
        Logger.xml(xml)
    }

    fun printThendInfo(tag: String) {
        d(tag + " " + Thread.currentThread().id + " " + Thread.currentThread().name)
    }
}
