package io.xapk.apkinstaller.model.glide.loader

import android.content.pm.PackageManager
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import io.xapk.apkinstaller.app.App
import io.xapk.apkinstaller.model.bean.ApkIconUrl
import io.xapk.apkinstaller.utils.AppUtils
import io.xapk.apkinstaller.utils.imager.BitmapUtils
import io.xapk.apkinstaller.utils.io.FsUtils
import java.io.InputStream

class ApkIconModelLoader : ModelLoader<ApkIconUrl, InputStream> {
    private val packageManager by lazy { App.mContext.packageManager }

    class Factory : ModelLoaderFactory<ApkIconUrl, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory) = ApkIconModelLoader()
        override fun teardown() = Unit
    }

    override fun buildLoadData(apkIconUrl: ApkIconUrl, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(apkIconUrl), object : DataFetcher<InputStream> {
            override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
                try {
                    packageManager.getPackageArchiveInfo(apkIconUrl.filePath, PackageManager.GET_META_DATA)?.applicationInfo?.apply {
                        this.sourceDir = apkIconUrl.filePath
                        this.publicSourceDir = apkIconUrl.filePath
                    }?.apply {
                        AppUtils.getAppIcon(packageManager, this)?.let {
                            callback.onDataReady(BitmapUtils.bitmap2InputStream(it))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.onLoadFailed(e)
                }
            }

            override fun cleanup() = Unit
            override fun cancel() = Unit
            override fun getDataClass() = InputStream::class.java
            override fun getDataSource() = DataSource.LOCAL
        })
    }

    override fun handles(apkIconUrl: ApkIconUrl) = FsUtils.exists(apkIconUrl.filePath)
}
