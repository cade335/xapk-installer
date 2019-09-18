package io.xapk.apkinstaller.utils.bean.xapk

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class XSplitApks(
        @Expose
        @SerializedName("file")
        var fileName: String? = null,
        @Expose
        @SerializedName("id")
        var _id: String? = null
)