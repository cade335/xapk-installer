package io.xapk.apkinstaller.utils.toast

import android.widget.Toast

enum class Duration(val time: Int) {
    SHORT(Toast.LENGTH_SHORT),
    Long(Toast.LENGTH_LONG)
}