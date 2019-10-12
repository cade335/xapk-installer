package io.xapk.apkinstaller.misc

import android.content.Context
import android.net.Uri
import com.afollestad.materialdialogs.MaterialDialog
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import io.reactivex.Observable
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.model.bean.ApiException
import io.xapk.apkinstaller.utils.ColorUtils
import io.xapk.apkinstaller.utils.IconicsUtils
import io.xapk.apkinstaller.utils.StringUtils
import io.xapk.apkinstaller.utils.ViewUtils
import io.xapk.apkinstaller.utils.asset.AssetUtils
import io.xapk.apkinstaller.utils.bean.ApkAssetType
import io.xapk.apkinstaller.utils.bean.ApksInfo
import io.xapk.apkinstaller.utils.bean.xapk.XApkInfo
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.io.UriUtils
import io.xapk.apkinstaller.utils.rx.RxObservableTransformer
import io.xapk.apkinstaller.utils.rx.RxSubscriber
import io.xapk.apkinstaller.utils.toast.SimpleToast
import io.xapk.apkinstaller.utils.unit.FormatUtils
import io.xapk.apkinstaller.utils.unit.JodaTimeUtils
import java.io.File

object LinkUrlManager {
    fun jumpToPage(mContext: Context, linkUri: Uri) {
        val scheme = linkUri.scheme ?: return
        if (scheme == "file" || scheme == "content") {
            val filePath = UriUtils.fromPath(mContext, linkUri)
            if (filePath.endsWith(ApkAssetType.XAPK.suffix, true)) {
                installXApkDialog(mContext, filePath)
            }else if (filePath.endsWith(ApkAssetType.Apks.suffix, true)) {
                installApksDialog(mContext, filePath)
            }
        }
    }

    private fun installXApkDialog(mContext: Context, filePath: String) {
        if (!FsUtils.exists(filePath)) {
            return
        }
        Observable.create<XApkInfo> {
            val apkAssetBean = AssetUtils.getSingleXApkAssetInfo(File((filePath)))
            if (apkAssetBean?.xApkInfo != null) {
                it.onNext(apkAssetBean.xApkInfo!!)
                it.onComplete()
            } else {
                it.onError(Throwable())
            }
        }
            .compose(RxObservableTransformer.addDisposable(mContext))
            .compose(RxObservableTransformer.errorResult(mContext))
            .subscribe(object : RxSubscriber<XApkInfo>() {
                override fun rxOnNext(t: XApkInfo) {
                    MaterialDialog(mContext).show {
                        val copyInfo = StringUtils.fromHtml(context.getString(R.string.menu_app_info_hit
                            , "${t.versionName}(${t.versionCode})"
                            , t.packageName
                            , t.path
                            , FormatUtils.formatFileLength(t.appSize)
                            , JodaTimeUtils.formatDataToShortDateInfo(JodaTimeUtils.longToDate(t.lastModified))))
                        this.title(null, t.label)
                        this@show.message(null, copyInfo)
                        this.negativeButton(android.R.string.cancel)
                        this.positiveButton(R.string.install) {
                            ViewUtils.installXApk(mContext, t)
                        }
                    }
                }

                override fun rxOnError(apiException: ApiException) {
                    SimpleToast.defaultShow(mContext, R.string.xapk_file_error)
                }
            })
    }

    private fun installApksDialog(mContext: Context, filePath: String) {
        if (!FsUtils.exists(filePath)) {
            return
        }
        Observable.create<ApksInfo> {
            val apkAssetBean = AssetUtils.getSingleApksAssetInfo(File((filePath)))
            if (apkAssetBean?.apksInfo != null) {
                it.onNext(apkAssetBean.apksInfo!!)
                it.onComplete()
            } else {
                it.onError(Throwable())
            }
        }
            .compose(RxObservableTransformer.addDisposable(mContext))
            .compose(RxObservableTransformer.errorResult(mContext))
            .subscribe(object : RxSubscriber<ApksInfo>() {
                override fun rxOnNext(t: ApksInfo) {
                    MaterialDialog(mContext).show {
                        this.icon(null, IconicsUtils.getIcon(mContext, GoogleMaterial.Icon.gmd_android)
                            .color(ColorUtils.getThemeColor(mContext, R.attr.colorPrimary)))
                        val copyInfo = StringUtils.fromHtml(context.getString(R.string.menu_apks_info_hit
                            , t.filePath
                            , FormatUtils.formatFileLength(t.fileSize)
                            , JodaTimeUtils.formatDataToShortDateInfo(JodaTimeUtils.longToDate(t.lastModified))))
                        this.title(null, t.fileName)
                        this@show.message(null, copyInfo)
                        this.negativeButton(android.R.string.cancel)
                        this.positiveButton(R.string.install) {
                            ViewUtils.installApks(mContext, t)
                        }
                    }
                }

                override fun rxOnError(apiException: ApiException) {
                    SimpleToast.defaultShow(mContext, R.string.apks_file_error)
                }
            })
    }
}