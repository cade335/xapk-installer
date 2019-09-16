package io.xapk.apkinstaller.utils.asset

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.WorkerThread
import io.xapk.apkinstaller.utils.StringUtils
import io.xapk.apkinstaller.utils.bean.ApkAssetBean
import io.xapk.apkinstaller.utils.bean.ApkAssetType
import io.xapk.apkinstaller.utils.bean.apk.ApkInfo
import io.xapk.apkinstaller.utils.bean.xapk.XApkInfo
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.io.filefilter.ApkXpkFileFilter
import java.io.File

object AssetUtils {
    @WorkerThread
    fun getStorageDirApk(mContext: Context): List<ApkAssetBean> {
        val unAppFileInfoList = arrayListOf<ApkAssetBean>()
        FsUtils.getStorageDir()?.apply {
            FsUtils.getDirFilesArray(this, ApkXpkFileFilter()).forEach {
                if (it.name.endsWith(ApkAssetType.Apk.suffix, true)) {
                    getSingleApkAssetInfo(mContext, it)?.apply {
                        unAppFileInfoList.add(this)
                    }
                } else if (it.name.endsWith(ApkAssetType.XAPK.suffix, true)) {
                    getSingleXApkAssetInfo(it)?.apply {
                        unAppFileInfoList.add(this)
                    }
                } else if (it.name.endsWith(ApkAssetType.Apks.suffix, true)) {
                    getSingleApksAssetInfo(it)?.apply {
                        unAppFileInfoList.add(this)
                    }
                }
            }
        }
        return unAppFileInfoList
    }

    @WorkerThread
    fun getSingleApkAssetInfo(mContext: Context, apkFile: File): ApkAssetBean? {
        var apkAssetBean: ApkAssetBean? = null
        mContext.packageManager.getPackageArchiveInfo(apkFile.path, PackageManager.GET_META_DATA)?.apply {
            val packageInfo = this
            this.applicationInfo?.let {
                it.sourceDir = apkFile.absolutePath
                it.publicSourceDir = apkFile.absolutePath
                apkAssetBean = ApkAssetBean().apply {
                    this.apkAssetType = ApkAssetType.Apk
                    this.apkInfo = ApkInfo().apply {
                        this.label = it.loadLabel(mContext.packageManager).toString()
                        this.packageName = it.packageName ?: String()
                        this.appSize = apkFile.length()
                        this.versionCode = packageInfo.versionCode
                        this.versionName = packageInfo.versionName ?: String()
                        this.lastModified = apkFile.lastModified()
                        this.path = apkFile.absolutePath
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            this.isUpdateApk = it.splitSourceDirs == null || it.splitSourceDirs.isEmpty()
                        } else {
                            this.isUpdateApk = true
                        }
                    }
                    this.sortPosition = this.apkInfo?.lastModified ?: 0L
                }
            }
        }
        return apkAssetBean
    }

    @WorkerThread
    fun getSingleXApkAssetInfo(xApkFile: File): ApkAssetBean? {
        var apkAssetBean: ApkAssetBean? = null
        XApkInstallUtils.getXApkManifest(xApkFile)?.apply {
            val xApkManifest = this
            apkAssetBean = ApkAssetBean().apply {
                this.apkAssetType = ApkAssetType.XAPK
                this.xApkInfo = XApkInfo().apply {
                    this.label = xApkManifest.getLocalLabel()
                    this.packageName = xApkManifest.packageName
                    this.appSize = xApkFile.length()
                    this.versionCode = StringUtils.parseInt(xApkManifest.versionCode) ?: 0
                    this.versionName = xApkManifest.versionName
                    this.lastModified = xApkFile.lastModified()
                    this.path = xApkFile.absolutePath
                }
                this.sortPosition = this.xApkInfo?.lastModified ?: 0L
            }
        }
        return apkAssetBean
    }

    @WorkerThread
    fun getSingleApksAssetInfo(apks: File): ApkAssetBean? {
        var apkAssetBean: ApkAssetBean? = null
        ApksUtils.parseApksZipFile(apks)?.apply {
            val apksInfo= this
            apkAssetBean = ApkAssetBean().apply {
                this.apksInfo = apksInfo
                this.sortPosition = apksInfo.lastModified
                this.apkAssetType = ApkAssetType.Apks
            }
        }
        return apkAssetBean
    }
}