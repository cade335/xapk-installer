package io.xapk.apkinstaller.utils.bean.xapk

import android.os.Parcelable
import io.xapk.apkinstaller.utils.bean.ApkAssetType
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ApksBean(
        var packageName: String,
        var label: String,
        var iconPath: String,
        var apkAssetType: ApkAssetType?,
        var outputFileDir: String,
        var splitApkPaths: ArrayList<String>?
) : Parcelable {
    constructor() : this(String(), String(), String(), null, String(), null)
}