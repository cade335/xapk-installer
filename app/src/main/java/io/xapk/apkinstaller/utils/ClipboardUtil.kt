package io.xapk.apkinstaller.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build

class ClipboardUtil private constructor(private val context: Context) {

    companion object {
        private var mClipboardManager: ClipboardManager? = null
        private var mNewClipboardManager: ClipboardManager? = null
        @SuppressLint("StaticFieldLeak")
        private var clipboardUtil: ClipboardUtil? = null

        fun getInstance(context: Context): ClipboardUtil {
            if (clipboardUtil == null) {
                synchronized(ClipboardUtil::class.java) {
                    if (clipboardUtil == null)
                        clipboardUtil = ClipboardUtil(context.applicationContext)
                }
            }
            return clipboardUtil!!
        }
    }

    private val isNew: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB

    var text: CharSequence
        get() {
            val sb = StringBuilder()
            if (isNew) {
                if (!mNewClipboardManager!!.hasPrimaryClip()) {
                    return sb.toString()
                } else {
                    val clipData = mNewClipboardManager!!.primaryClip!!
                    val count = clipData.itemCount
                    for (i in 0 until count) {
                        val item = clipData.getItemAt(i)
                        val str = item.coerceToText(context)
                        sb.append(str)
                    }
                }
            } else {
                sb.append(mClipboardManager!!.text)
            }
            return sb.toString()
        }
        set(text) {
            return if (isNew) {
                val clip = ClipData.newPlainText("simple text", text)
                mNewClipboardManager!!.primaryClip = clip
            } else {
                mClipboardManager!!.text = text
            }
        }

    init {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE)
        if (isNew) {
            if (clipboardManager is ClipboardManager) {
                mNewClipboardManager = clipboardManager
            }
        } else {
            if (clipboardManager is ClipboardManager) {
                mClipboardManager = clipboardManager
            }
        }
    }
}
