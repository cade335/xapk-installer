package io.xapk.apkinstaller.utils.bean.xapk

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ApksBean(
    var packageName: String,
    var label: String,
    var outputFileDir: String,
    var splitApkPaths: ArrayList<String>?
) : Parcelable {
    constructor() : this(String(), String(), String(), null)
}