package io.xapk.apkinstaller.ui.fragment.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.model.bean.AppedIconUrl
import io.xapk.apkinstaller.model.glide.ImageLoader
import io.xapk.apkinstaller.ui.base.IBaseViewMultiHolder
import io.xapk.apkinstaller.ui.widget.button.StateButton
import io.xapk.apkinstaller.ui.widget.roundview.RoundTextView
import io.xapk.apkinstaller.utils.ClipboardUtil
import io.xapk.apkinstaller.utils.IntentUtils
import io.xapk.apkinstaller.utils.LaunchUtils
import io.xapk.apkinstaller.utils.StringUtils
import io.xapk.apkinstaller.utils.toast.SimpleToast
import io.xapk.apkinstaller.utils.unit.FormatUtils
import io.xapk.apkinstaller.utils.unit.JodaTimeUtils

class AppInstalledAdapter(val mActivity: Activity, data: List<AppInfo>?) : BaseQuickAdapter<AppInfo
        , AppInstalledAdapter.AppInstalledViewHolder>(R.layout.item_frag_installed_app, data) {
    override fun convert(helper: AppInstalledViewHolder, item: AppInfo) {
        helper.updateView(item)
    }

    inner class AppInstalledViewHolder(view: View) : IBaseViewMultiHolder<AppInfo>(view) {
        private val iconIv: ImageView = view.findViewById(R.id.icon_iv)
        private val nameTv: TextView = view.findViewById(R.id.name_tv)
        private val versionInfoTv: TextView = view.findViewById(R.id.version_info_tv)
        private val sizeTv: TextView = view.findViewById(R.id.size_tv)
        private val unInstalledBt: StateButton = view.findViewById(R.id.un_installed_bt)
        private val optionRl: RelativeLayout = view.findViewById(R.id.option_rl)
        private val xApkRtv: RoundTextView = view.findViewById(R.id.xapk_rtv)
        @SuppressLint("SetTextI18n")
        override fun updateView(dateItem: AppInfo) {
            super.updateView(dateItem)
            ImageLoader.Builder(mContext, AppedIconUrl(dateItem.packageName, dateItem.versionCode))
                .setRequestOptions(ImageLoader.defaultRequestOptions(R.color.placeholder_color))
                .build(iconIv)
            nameTv.apply {
                this.text = dateItem.label
                this.requestLayout()
            }
            sizeTv.text = FormatUtils.formatFileLength(dateItem.appTotalSize)
            versionInfoTv.text = dateItem.versionName
            xApkRtv.apply {
                if (dateItem.isExpandXApk) {
                    this.visibility = View.VISIBLE
                } else {
                    this.visibility = View.GONE
                }
            }
            unInstalledBt.setOnClickListener {
                IntentUtils.unInstalledApp(mActivity, dateItem.packageName)
            }
            optionRl.setOnClickListener {
                showOptionPopupWindow(it, dateItem)
            }
        }
    }

    private fun showOptionPopupWindow(authorView: View, appInfo: AppInfo) {
        PopupMenu(mContext, authorView).apply {
            this.menu.apply {
                mContext.resources.getStringArray(R.array.installed_app_options)
                    .forEachIndexed { index, s ->
                        this.add(0, index, index, s)
                    }
            }
            this.menu.findItem(3)?.isVisible = appInfo.isExpandXApk
            this.menu.findItem(4)?.isVisible = appInfo.isUpdateFile
            this.menu.findItem(5)?.isVisible = appInfo.isExpandApks
            this.setOnMenuItemClickListener {
                when (it.itemId) {
                    0 -> IntentUtils.openApp(mContext, appInfo.packageName)
                    1 -> aboutAppInfo(appInfo)
                    2 -> IntentUtils.openAppSetting(mContext, appInfo.packageName)
                    3 -> LaunchUtils.startXApkOutputZipService(mContext, appInfo)
                    4 -> LaunchUtils.startApkExportService(mContext, appInfo)
                    5 -> LaunchUtils.startApksExportService(mContext, appInfo)
                    else -> {
                    }
                }
                return@setOnMenuItemClickListener true
            }
            this.show()
        }
    }

    private fun aboutAppInfo(appInfo: AppInfo) {
        MaterialDialog(mContext).show {
            val copyInfo = StringUtils.fromHtml(context.getString(R.string.menu_app_info_hit
                , "${appInfo.versionName}(${appInfo.versionCode})"
                , appInfo.packageName
                , appInfo.sourceDir
                , FormatUtils.formatFileLength(appInfo.appTotalSize)
                , JodaTimeUtils.formatDataToShortDateInfo(JodaTimeUtils.longToDate(appInfo.lastUpdateTime))))
            this.title(null, appInfo.label)
            this@show.message(null, copyInfo)
            this.negativeButton(android.R.string.copy) {
                ClipboardUtil.getInstance(mContext).text = copyInfo
                SimpleToast.defaultShow(mContext, R.string.copy_successfully)
            }
            this.positiveButton(android.R.string.ok)
        }
    }
}