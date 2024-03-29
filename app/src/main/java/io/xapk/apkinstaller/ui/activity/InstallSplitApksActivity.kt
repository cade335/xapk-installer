package io.xapk.apkinstaller.ui.activity

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageInstaller
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.makeramen.roundedimageview.RoundedImageView
import io.xapk.apkinstaller.BuildConfig
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.model.bean.ApkIconUrl
import io.xapk.apkinstaller.model.glide.ImageLoader
import io.xapk.apkinstaller.ui.base.IBaseActivity
import io.xapk.apkinstaller.utils.bean.ApkAssetType
import io.xapk.apkinstaller.utils.bean.xapk.ApksBean
import io.xapk.apkinstaller.utils.bean.xapk.XApkIconUrl
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.toast.SimpleToast
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class InstallSplitApksActivity : IBaseActivity() {
    private  var apksBean: ApksBean?=null

    companion object{
        private const val KEY_PARAM = "params_apks"
        private const val PACKAGE_INSTALLED_ACTION = BuildConfig.APPLICATION_ID + ".SESSION_API_PACKAGE_INSTALLED"

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        fun newInstanceIntent(mContext: Context, apksBean: ApksBean): Intent {
            return Intent(mContext, InstallSplitApksActivity::class.java).apply {
                this.putExtra(KEY_PARAM, apksBean)
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_install_split_apks
    }

    override fun nextStep() {
        super.nextStep()
        apksBean = intent.getParcelableExtra(KEY_PARAM)
        if (apksBean == null
                || apksBean!!.splitApkPaths.isNullOrEmpty()
                || apksBean!!.packageName.isEmpty()) {
            SimpleToast.defaultShow(mContext, R.string.install_failed)
            finish()
            return
        }
        val iconRiv: RoundedImageView = findViewById(R.id.icon_riv)
        val titleTv: TextView = findViewById(R.id.title_tv)
        titleTv.text = apksBean!!.label
        val apkAssetType = apksBean!!.apkAssetType
        val iconUrl = apksBean!!.iconPath
        if (!TextUtils.isEmpty(iconUrl) && apkAssetType != null) {
            iconRiv.visibility = View.VISIBLE
            if (apkAssetType == ApkAssetType.XAPK) {
                ImageLoader.Builder(mContext, XApkIconUrl(iconUrl))
                        .setRequestOptions(ImageLoader.defaultRequestOptions(R.color.placeholder_color))
                        .build(iconRiv)
            } else if (apkAssetType == ApkAssetType.Apks) {
                ImageLoader.Builder(mContext, ApkIconUrl(iconUrl, -1))
                        .setRequestOptions(ImageLoader.defaultRequestOptions(R.color.placeholder_color))
                        .build(iconRiv)
            }
        } else {
            iconRiv.visibility = View.GONE
        }
        Handler(Looper.getMainLooper()).postDelayed({ this.install() }, 500)
    }

    private fun install() {
        var session: PackageInstaller.Session? = null
        try {
            val packageInstaller = packageManager.packageInstaller
            val params = PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL
            )
            params.setInstallLocation(PackageInfo.INSTALL_LOCATION_AUTO)
            val sessionId = packageInstaller.createSession(params)
            session = packageInstaller.openSession(sessionId)
            for (splitApk in apksBean!!.splitApkPaths!!) {
                addApkFileToInstallSession(File(splitApk), session)
            }
            val intent = Intent(mContext, InstallSplitApksActivity::class.java)
            intent.action = PACKAGE_INSTALLED_ACTION
            intent.putExtra("packageName", apksBean!!.packageName)
            val pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0)
            val statusReceiver = pendingIntent.intentSender
            // Commit the session (this will start the installation workflow).
            session!!.commit(statusReceiver)
        } catch (e: IOException) {
            e.printStackTrace()
            finish()
            return
        } catch (e: RuntimeException) {
            session?.abandon()
            e.printStackTrace()
            finish()
            return
        }

    }

    @Throws(IOException::class)
    private fun addApkFileToInstallSession(file: File, session: PackageInstaller.Session) {
        session.openWrite(file.name, 0, -1).use { packageInSession ->
            FileInputStream(file).use { `is` ->
                val buffer = ByteArray(16384)
                var n: Int
                while (`is`.read(buffer).apply { n = this } >= 0) {
                    packageInSession.write(buffer, 0, n)
                }
                session.fsync(packageInSession)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent == null) {
            return
        }
        val extras = intent.extras
        if (PACKAGE_INSTALLED_ACTION == intent.action) {
            val status = extras!!.getInt(PackageInstaller.EXTRA_STATUS)
            val message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE)
            when (status) {
                PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                    // This test app isn't privileged, so the user has to confirm the install.
                    val confirmIntent = extras.get(Intent.EXTRA_INTENT) as Intent
                    startActivity(confirmIntent)
                }
                PackageInstaller.STATUS_SUCCESS -> {
                    SimpleToast.defaultShow(this, R.string.install_success)
                    finish()
                }
                PackageInstaller.STATUS_FAILURE,
                PackageInstaller.STATUS_FAILURE_ABORTED,
                PackageInstaller.STATUS_FAILURE_BLOCKED,
                PackageInstaller.STATUS_FAILURE_CONFLICT,
                PackageInstaller.STATUS_FAILURE_INCOMPATIBLE,
                PackageInstaller.STATUS_FAILURE_INVALID,
                PackageInstaller.STATUS_FAILURE_STORAGE -> {
                    SimpleToast.defaultShow(this, R.string.install_failed)
                    finish()
                }
                else -> {}
            }

            if (status == PackageInstaller.STATUS_SUCCESS ||
                status == PackageInstaller.STATUS_FAILURE ||
                status == PackageInstaller.STATUS_FAILURE_ABORTED ||
                status == PackageInstaller.STATUS_FAILURE_BLOCKED ||
                status == PackageInstaller.STATUS_FAILURE_CONFLICT ||
                status == PackageInstaller.STATUS_FAILURE_INCOMPATIBLE ||
                status == PackageInstaller.STATUS_FAILURE_INVALID ||
                status == PackageInstaller.STATUS_FAILURE_STORAGE) {
                apksBean?.outputFileDir.let {
                  if (FsUtils.exists(it)){
                      FsUtils.deleteFileOrDir(it)
                  }
                }
                findViewById<View>(R.id.installing_status_view).visibility = View.GONE
                findViewById<View>(R.id.installed_status_view).visibility = View.VISIBLE
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.onKeyDown(keyCode, event)
        }
    }
}
