package io.xapk.apkinstaller.utils.bean.apk

data class ApkInfo(var label: String,
                   var packageName: String,
                   var versionCode: Int,
                   var versionName: String,
                   var appSize: Long,
                   var lastModified: Long,
                   var path: String,
                   var isUpdateApk: Boolean) {
    constructor() : this(String(), String(), 0, String(), 0L, 0L, String(), true)
}
