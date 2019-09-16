package io.xapk.apkinstaller.utils.io.filefilter

import io.xapk.apkinstaller.utils.bean.ApkAssetType
import io.xapk.apkinstaller.utils.io.FsUtils
import java.io.File
import java.io.FileFilter

class ApkXpkFileFilter : FileFilter {
    override fun accept(pathname: File?): Boolean {
        return pathname != null && FsUtils.exists(pathname)
                && (pathname.name.toLowerCase().endsWith(ApkAssetType.Apk.suffix)
                || pathname.name.toLowerCase().endsWith(ApkAssetType.XAPK.suffix)
                || pathname.name.toLowerCase().endsWith(ApkAssetType.Apks.suffix))
    }
}