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
import io.xapk.apkinstaller.model.bean.AppedIconUrl
import io.xapk.apkinstaller.utils.AppUtils
import io.xapk.apkinstaller.utils.imager.BitmapUtils
import java.io.InputStream

class AppedIconModelLoader : ModelLoader<AppedIconUrl, InputStream> {
    private val packageManager by lazy { App.mContext.packageManager }

    class Factory : ModelLoaderFactory<AppedIconUrl, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory) = AppedIconModelLoader()

        override fun teardown() = Unit
    }

    override fun buildLoadData(appedIconUrl: AppedIconUrl, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(ObjectKey(appedIconUrl), object : DataFetcher<InputStream> {
            override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
                try {
                    packageManager.getPackageInfo(appedIconUrl.packName, PackageManager.GET_META_DATA)
                            ?.applicationInfo?.apply {
                        AppUtils.getAppIcon(packageManager, this)?.let {
                            callback.onDataReady(BitmapUtils.bitmap2InputStream(it))
                        }
                    }
                } catch (e: Exception) {
                    callback.onLoadFailed(e)
                    e.printStackTrace()
                }
            }

            override fun cleanup() = Unit
            override fun cancel() = Unit
            override fun getDataClass() = InputStream::class.java
            override fun getDataSource() = DataSource.LOCAL
        })
    }

    override fun handles(appedIconUrl: AppedIconUrl) = appedIconUrl.packName.isNotEmpty()
}
