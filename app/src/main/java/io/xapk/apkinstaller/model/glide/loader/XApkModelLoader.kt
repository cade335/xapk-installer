package io.xapk.apkinstaller.model.glide.loader

import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import io.xapk.apkinstaller.utils.bean.xapk.XApkIconUrl
import io.xapk.apkinstaller.utils.asset.XApkInstallUtils
import io.xapk.apkinstaller.utils.io.FsUtils
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.InputStream

class XApkModelLoader : ModelLoader<XApkIconUrl, InputStream> {
    class Factory : ModelLoaderFactory<XApkIconUrl, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory) = XApkModelLoader()
        override fun teardown() = Unit
    }

    override fun buildLoadData(xApkIconUrl: XApkIconUrl, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(xApkIconUrl), object : DataFetcher<InputStream> {
            override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
                var zipFile: ZipFile? = null
                try {
                    zipFile = XApkInstallUtils.parseXApkZipFile(xApkIconUrl.filePath)!!
                    callback.onDataReady(XApkInstallUtils.getXApkIcon(zipFile))
                } catch (e: Exception) {
                    e.printStackTrace()
                    callback.onLoadFailed(e)
                } finally {
                    try {
                        zipFile?.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun cleanup() = Unit
            override fun cancel() = Unit
            override fun getDataClass() = InputStream::class.java
            override fun getDataSource() = DataSource.LOCAL
        })
    }

    override fun handles(xApkIconUrl: XApkIconUrl) = FsUtils.exists(xApkIconUrl.filePath)
}
