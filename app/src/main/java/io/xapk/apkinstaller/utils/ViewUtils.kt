package io.xapk.apkinstaller.utils

import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import androidx.annotation.MainThread
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.utils.asset.ApksUtils
import io.xapk.apkinstaller.utils.asset.XApkInstallUtils
import io.xapk.apkinstaller.utils.bean.ApkAssetBean
import io.xapk.apkinstaller.utils.bean.ApkAssetType
import io.xapk.apkinstaller.utils.bean.ApksInfo
import io.xapk.apkinstaller.utils.bean.xapk.ApksBean
import io.xapk.apkinstaller.utils.bean.xapk.XApkInfo
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.toast.SimpleToast
import io.xapk.apkinstaller.utils.unit.FormatUtils
import java.io.File

object ViewUtils {

    @MainThread
    fun installXApk(mContext: Context, xApkInfo: XApkInfo) {
        XApkInstallUtils.installXApk(File(xApkInfo.path), object : XApkInstallUtils.XApkInstallProgressCallback {
            private var progressDialog: ProgressDialog? = null
            override fun onStart() {
                ProgressDialog(mContext).apply {
                    progressDialog = this
                    this.setTitle(xApkInfo.label)
                    this.setMessage(mContext.getString(R.string.preparing))
                    this.setCanceledOnTouchOutside(false)
                    this.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                    this.isIndeterminate = false
                    this.max = 100
                    this.progress = 0
                    this.show()
                }
            }

            override fun onObbProgress(currentOffset: Long, totalLength: Long, percent: Int) {
                progressDialog?.apply {
                    if (this.isShowing) {
                        this.setMessage(mContext.getString(R.string.installing_obb_file))
                        if (percent > 0) {
                            this.isIndeterminate = false
                            this.progress = percent
                        } else {
                            this.isIndeterminate = true
                        }
                    }
                }
            }

            override fun onApkProgress(currentOffset: Long, totalLength: Long, percent: Int) {
                progressDialog?.apply {
                    if (this.isShowing) {
                        this.setMessage(mContext.getString(R.string.pick_up_apk_file))
                        if (percent > 0) {
                            this.isIndeterminate = false
                            this.progress = FormatUtils.formatPercent(currentOffset, totalLength)
                        } else {
                            this.isIndeterminate = true
                        }
                    }
                }
            }

            override fun onCompedApk(apkFile: File) {
                progressDialog?.apply {
                    if (this.isShowing) {
                        this.dismiss()
                    }
                }
                IntentUtils.installedApk(mContext, apkFile.absolutePath)
            }

            override fun onCompedApks(apksBean: ApksBean) {
                progressDialog?.apply {
                    if (this.isShowing) {
                        this.dismiss()
                    }
                }
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> LaunchUtils.startInstallSplitApksActivity(mContext, apksBean)
                    apksBean.splitApkPaths?.size == 1 -> IntentUtils.installedApk(mContext, apksBean.splitApkPaths!![0])
                    else -> onError(XApkInstallUtils.InstallError.LowerSdkError)
                }
            }

            override fun onError(installError: XApkInstallUtils.InstallError) {
                progressDialog?.apply {
                    if (this.isShowing) {
                        this.dismiss()
                    }
                }
                when (installError) {
                    XApkInstallUtils.InstallError.ObbError -> SimpleToast.defaultShow(mContext, R.string.install_obb_failed)
                    XApkInstallUtils.InstallError.LowerVersionError -> SimpleToast.defaultShow(mContext, R.string.xapk_lower_version_error)
                    XApkInstallUtils.InstallError.LowerSdkError -> SimpleToast.defaultShow(mContext, R.string.part_xapk_sdk_lower_version_error)
                    else -> SimpleToast.defaultShow(mContext, R.string.install_failed)
                }
            }
        })
    }

    @MainThread
    fun installApks(mContext: Context, apksInfo: ApksInfo) {
        ApksUtils.installApks(mContext, apksInfo, object : ApksUtils.ApksInstallProgressCallback {
            private var progressDialog: ProgressDialog? = null
            override fun onStart() {
                ProgressDialog(mContext).apply {
                    progressDialog = this
                    this.setTitle(apksInfo.fileName)
                    this.setMessage(mContext.getString(R.string.preparing))
                    this.setCanceledOnTouchOutside(false)
                    this.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                    this.isIndeterminate = false
                    this.max = 100
                    this.progress = 0
                    this.show()
                }
            }

            override fun onApkProgress(currentOffset: Long, totalLength: Long, percent: Int) {
                progressDialog?.apply {
                    if (this.isShowing) {
                        this.setMessage(mContext.getString(R.string.pick_up_apk_file))
                        if (percent > 0) {
                            this.isIndeterminate = false
                            this.progress = FormatUtils.formatPercent(currentOffset, totalLength)
                        } else {
                            this.isIndeterminate = true
                        }
                    }
                }
            }

            override fun onCompedApk(apksFile: ArrayList<File>, apkAssetBean: ApkAssetBean?, outputDir: File) {
                val apkInfo = apkAssetBean?.apkInfo
                if (apkInfo != null && apksFile.isNotEmpty()) {
                    progressDialog?.apply {
                        if (this.isShowing) {
                            this.dismiss()
                        }
                    }
                    val splitApkPaths = arrayListOf<String>().apply {
                        apksFile.forEach {
                            this.add(it.absolutePath)
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        LaunchUtils.startInstallSplitApksActivity(mContext, ApksBean().apply {
                            this.label = apkInfo.label
                            this.packageName = apkInfo.packageName
                            this.splitApkPaths = splitApkPaths
                            this.outputFileDir = outputDir.absolutePath
                            this.iconPath = apkInfo.path
                            this.apkAssetType = ApkAssetType.Apks
                        })
                    } else {
                        this.onError(outputDir)
                    }
                } else {
                    this.onError(outputDir)
                }
            }

            override fun onError(outputDir: File) {
                FsUtils.deleteFileOrDir(outputDir)
                progressDialog?.apply {
                    if (this.isShowing) {
                        this.dismiss()
                    }
                }
                SimpleToast.defaultShow(mContext, R.string.install_failed)
            }
        })
    }
}