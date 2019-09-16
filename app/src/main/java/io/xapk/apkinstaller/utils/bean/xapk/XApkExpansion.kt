package io.xapk.apkinstaller.utils.bean.xapk

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class XApkExpansion(@Expose
                         @SerializedName("file")
                         var xFile: String,
                         @Expose
                         @SerializedName("install_location")
                         var installLocation: String,
                         @Expose
                         @SerializedName("install_path")
                         var installPath: String) {
    constructor() : this(String(), "", String())
}