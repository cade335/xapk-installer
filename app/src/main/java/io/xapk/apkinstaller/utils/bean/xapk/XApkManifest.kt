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
                        var XSplitApks: List<XSplitApks>? = null,
                        @Expose
                        @SerializedName("split_configs")
                        var splitConfigs: Array<String>? = null) {
    constructor() : this(0
            , String(), String()
            , null, String()
            , String(), String()
            , String(), null
            , 0L, null
            , null, null)

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as XApkManifest

        if (xApkVersion != other.xApkVersion) return false
        if (packageName != other.packageName) return false
        if (label != other.label) return false
        if (localesLabel != other.localesLabel) return false
        if (versionCode != other.versionCode) return false
        if (versionName != other.versionName) return false
        if (minSdkVersion != other.minSdkVersion) return false
        if (targetSdkVersion != other.targetSdkVersion) return false
        if (permissions != other.permissions) return false
        if (totalSize != other.totalSize) return false
        if (expansionList != other.expansionList) return false
        if (XSplitApks != other.XSplitApks) return false
        if (splitConfigs != null) {
            if (other.splitConfigs == null) return false
            if (!splitConfigs!!.contentEquals(other.splitConfigs!!)) return false
        } else if (other.splitConfigs != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = xApkVersion
        result = 31 * result + packageName.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + (localesLabel?.hashCode() ?: 0)
        result = 31 * result + versionCode.hashCode()
        result = 31 * result + versionName.hashCode()
        result = 31 * result + minSdkVersion.hashCode()
        result = 31 * result + targetSdkVersion.hashCode()
        result = 31 * result + (permissions?.hashCode() ?: 0)
        result = 31 * result + totalSize.hashCode()
        result = 31 * result + (expansionList?.hashCode() ?: 0)
        result = 31 * result + (XSplitApks?.hashCode() ?: 0)
        result = 31 * result + (splitConfigs?.contentHashCode() ?: 0)
        return result
    }
}