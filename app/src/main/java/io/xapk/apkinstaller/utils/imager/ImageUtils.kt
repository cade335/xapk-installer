package io.xapk.apkinstaller.utils.imager

import android.content.Context
import androidx.annotation.WorkerThread
import io.xapk.apkinstaller.model.glide.ImageLoader
import io.xapk.apkinstaller.utils.io.FileWriterUtils
import io.xapk.apkinstaller.utils.io.FsUtils
import java.io.File
import java.io.FileInputStream

object ImageUtils {
    @WorkerThread
    fun downloadImage(mContext: Context, imageUrl: Any, imageFile: File) {
        val cacheImageFile = ImageLoader.getCacheFile(mContext, imageUrl)
        if (FsUtils.exists(cacheImageFile)) {
            FileWriterUtils.writeFileFromIS(imageFile, FileInputStream(cacheImageFile))
        }
    }
}
