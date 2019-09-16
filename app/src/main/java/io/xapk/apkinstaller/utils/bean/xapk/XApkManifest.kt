package io.xapk.apkinstaller.utils.bean.xapk

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.xapk.apkinstaller.utils.LocaleUtils

data class XApkManifest(@Expose
                        @SerializedName("xapk_version")
                        var xApkVersion: Int,
                        @Expose
                        @SerializedName("package_name")
                        var packageName: String,
                        @Expose
                        @SerializedName("name")
                        var label: String,
                        @Expose
                        @SerializedName("locales_name")
                        var localesLabel: Map<String, String>? = null,
                        @Expose
                        @SerializedName("version_code")
                        var versionCode: String,
                        @Expose
                        @SerializedName("version_name")
                        var versionName: String,
                        @Expose
                        @SerializedName("min_sdk_version")
                        var minSdkVersion: String,
                        @Expose
                        @SerializedName("target_sdk_version")
                        var targetSdkVersion: String,
                        @Expose
                        @SerializedName("permissions")
                        var permissions: List<String>? = null,
                        @Expose
                        @SerializedName("total_size")
                        var totalSize: Long,
                        @Expose
                        @SerializedName("expansions")
                        var expansionList: List<XApkExpansion>? = null,
                        @Expose
                        @SerializedName("split_apks")
                        var XSplitApks: List<XSplitApks>? = null) {
    constructor() : this(0
            , String(), String()
            , null, String()
            , String(), String()
            , String(), null
            , 0L, null,null)

    fun getLocalLabel(): String {
        val localeTag = LocaleUtils.appLocalTag
        var label1 = this.label
        localesLabel?.let {
            if (it.containsKey(localeTag)) {
                it[localeTag]?.let { it2 ->
                    label1 = it2
                }
            }
        }
        return label1
    }

    fun useSplitApks() = !this.XSplitApks.isNullOrEmpty()

    fun useObbs() = !this.expansionList.isNullOrEmpty()
}