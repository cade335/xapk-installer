package io.xapk.apkinstaller.utils.bean.apk

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AppInfo(var label: String,
                   var packageName: String,
                   var versionCode: Int,
                   var versionName: String,
                   var minSdkVersion: String,
                   var targetSdkVersion: String,
                   var apkSize: Long,
                   var firstInstallTime: Long,
                   var lastUpdateTime: Long,
                   var isSystemApp: Boolean,
                   var isEnabled: Boolean,
                   var sourceDir: String,
                   var splitSourceDirs: ArrayList<String>,
                   var isUpdateFile: Boolean,
                   var isExpandXApk: Boolean,
                   var isExpandApks: Boolean,
                   var xApkMainObbPath: String,
                   var xApkMainObbAbsolutePath: String,
                   var xApkPatchObbPath: String,
                   var xApkPatchObbAbsolutePath: String,
                   var xApkObbSize: Long,
                   var appTotalSize: Long,
                   var permissionsArrays: ArrayList<String>,
                   var apksFilePath: ArrayList<String>,
                   var obbExists: Boolean) : Parcelable {
    constructor() : this(String(), String()
            , 0, String()
            , String(), String()
            , 0L, 0L
            , 0L, false
            , false, String()
            , arrayListOf(), true
            , false, false, String()
            , String(), String()
            , String(), 0L
            , 0L, arrayListOf()
            , arrayListOf(), false)

    override fun toString(): String {
        return "label=$label packageName=$packageName versionCode=$versionCode " +
                "apkSize=$apkSize xApkObbSize=$xApkObbSize appTotalSize=$appTotalSize isUpdateFile=$isUpdateFile " +
                "isEnabled=$isEnabled isExpandXApk=$isExpandXApk sourceDir=$sourceDir " +
                "xApkMainObbPath=$xApkMainObbPath xApkPatchObbPath=$xApkPatchObbPath "+
                "obbExists=$obbExists "
    }
}
