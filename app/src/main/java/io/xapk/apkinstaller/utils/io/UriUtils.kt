package io.xapk.apkinstaller.utils.io


import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.text.TextUtils
import androidx.core.content.FileProvider
import io.xapk.apkinstaller.BuildConfig
import java.io.File

object UriUtils {
    private val fileProviderPath by lazy { "${BuildConfig.APPLICATION_ID}.fileprovider" }

    fun fromFileProvider(mContext: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(mContext, fileProviderPath, file)
        } else {
            Uri.fromFile(file)
        }
    }

    fun fromPath(context: Context, uri: Uri): String {
        var path: String? = FileUtilsFix.getPath(context, uri)
        if (TextUtils.isEmpty(path)) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    val id = DocumentsContract.getDocumentId(uri)
                    if (!TextUtils.isEmpty(id)) {
                        val builder = Uri.Builder()
                        var isStorage = false
                        for (item in Uri.parse(id).pathSegments) {
                            if (!isStorage) {
                                isStorage = TextUtils.equals(item, "storage")
                            }
                            if (isStorage) {
                                builder.appendPath(item)
                            }
                        }
                        path = builder.build().path
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (path == null) {
            path = ""
        }
        return path
    }
}
