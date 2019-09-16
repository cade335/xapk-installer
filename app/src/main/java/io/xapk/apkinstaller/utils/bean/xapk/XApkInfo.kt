package io.xapk.apkinstaller.utils.bean.xapk

data class XApkInfo(var label: String,
                    var packageName: String,
                    var versionCode: Int,
                    var versionName: String,
                    var appSize: Long,
                    var lastModified: Long,
                    var path: String) {
    constructor() : this(String(), String(), 0, String(), 0, 0, String())
}