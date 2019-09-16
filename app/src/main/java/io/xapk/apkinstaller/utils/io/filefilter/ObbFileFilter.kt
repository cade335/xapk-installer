package io.xapk.apkinstaller.utils.io.filefilter

import io.xapk.apkinstaller.utils.io.FsUtils
import java.io.File
import java.io.FileFilter

class ObbFileFilter : FileFilter {
    override fun accept(pathname: File?): Boolean {
        return pathname != null && FsUtils.exists(pathname)
                && pathname.name.toLowerCase().endsWith(".obb")
    }
}