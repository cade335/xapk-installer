package io.xapk.apkinstaller.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.app.ShareCompat
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.Permission
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.model.bean.ApiException
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.io.UriUtils
import io.xapk.apkinstaller.utils.rx.RxPermissionsUtils
import io.xapk.apkinstaller.utils.rx.RxSubscriber
import java.io.File

object IntentUtils {

    fun openAppSetting(mContext: Context, appPkg: String = mContext.packageName) {
        try {
            if (TextUtils.isEmpty(appPkg))
                return
            mContext.startActivity(Intent().apply {
                this.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                this.data = Uri.fromParts("package", appPkg, null)
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun installedApk(mContext: Context, filePath: String) {
        if (FsUtils.exists(filePath)) {
            Intent().apply {
                this.action = Intent.ACTION_VIEW
                this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    this.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }
                this.setDataAndType(UriUtils.fromFileProvider(mContext, File(filePath)), "application/vnd.android.package-archive")
                mContext.startActivity(this)
            }
        }
    }

    fun openApp(mContext: Context, packName: String) {
        mContext.packageManager.getLaunchIntentForPackage(packName)?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContext.startActivity(it)
        }
    }

    fun unInstalledApp(mActivity: Activity, packName: String) {
        if (mActivity is FragmentActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                RxPermissionsUtils.requestPermission(mActivity, Manifest.permission.REQUEST_DELETE_PACKAGES)
                        .subscribe(object : RxSubscriber<Permission>() {
                            override fun rxOnNext(t: Permission) {
                                if (t.granted) {
                                    unloadApp(mActivity, packName)
                                }
                            }

                            override fun rxOnError(apiException: ApiException) = Unit
                        })
            } else {
                unloadApp(mActivity, packName)
            }
        }
    }

    private fun unloadApp(mContext: Context, packName: String) {
        Intent(Intent.ACTION_DELETE).apply {
            this.data = Uri.parse("package:$packName")
            mContext.startActivity(this)
        }
    }

    fun shareText(activity: Activity, extraText: String) {
        ShareCompat.IntentBuilder
            .from(activity)
            .setType("text/plain")
            .setText(extraText)
            .setChooserTitle(activity.getString(R.string.share))
            .startChooser()
    }
}
