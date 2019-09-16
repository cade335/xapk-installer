package io.xapk.apkinstaller.utils.bean

import io.xapk.apkinstaller.utils.bean.apk.ApkInfo
import io.xapk.apkinstaller.utils.bean.xapk.XApkInfo

data class ApkAssetBean(var apkInfo: ApkInfo?,
                        var xApkInfo: XApkInfo?,
                        var apksInfo: ApksInfo?,
                        var sortPosition: Long,
                        var apkAssetType: ApkAssetType) {
    constructor() : this(null, null, null, 0L, ApkAssetType.Apk)
}