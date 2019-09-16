package io.xapk.apkinstaller.utils.rx

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable

object RxPermissionsUtils {

    private fun getRxPermissions(activity: FragmentActivity): RxPermissions {
        return RxPermissions(activity)
    }

    @CheckResult
    fun checkSelfPermission(mContext: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED
    }

    @CheckResult
    fun requestPermission(activity: FragmentActivity, vararg permissions: String): Observable<Permission> {
        return getRxPermissions(activity).requestEach(*permissions)
    }

    @CheckResult
    fun requestStoragePermission(activity: FragmentActivity): Observable<Permission> {
        return requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    @CheckResult
    fun isOpenStoragePermission(mContext: Context): Boolean {
        return checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}
