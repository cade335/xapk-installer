package io.xapk.apkinstaller.ui.fragment.adapter

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import com.afollestad.materialdialogs.MaterialDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.model.bean.ApkIconUrl
import io.xapk.apkinstaller.model.glide.ImageLoader
import io.xapk.apkinstaller.ui.base.IBaseViewMultiHolder
import io.xapk.apkinstaller.ui.widget.button.StateButton
import io.xapk.apkinstaller.ui.widget.roundview.RoundTextView
import io.xapk.apkinstaller.utils.*
import io.xapk.apkinstaller.utils.bean.ApkAssetBean
import io.xapk.apkinstaller.utils.bean.ApkAssetType
import io.xapk.apkinstaller.utils.bean.ApksInfo
import io.xapk.apkinstaller.utils.bean.apk.ApkInfo
import io.xapk.apkinstaller.utils.bean.xapk.XApkIconUrl
import io.xapk.apkinstaller.utils.bean.xapk.XApkInfo
import io.xapk.apkinstaller.utils.firebase.FirebaseUtils
import io.xapk.apkinstaller.utils.io.FsUtils
import io.xapk.apkinstaller.utils.toast.SimpleToast
import io.xapk.apkinstaller.utils.unit.FormatUtils
import io.xapk.apkinstaller.utils.unit.JodaTimeUtils

class InstallPackageAdapter (data: List<ApkAssetBean>?): BaseQuickAdapter<ApkAssetBean, InstallPackageAdapter.InstallPackageViewHolder>(
    R.layout.item_frag_install_package, data) {

    private var onClickViewHolderCallback: OnClickViewHolderCallback? = null

    interface OnClickViewHolderCallback {
        fun delete(apkAssetBean: ApkAssetBean)
    }

    fun setOnClickViewHolderCallback(onClickViewHolderCallback: OnClickViewHolderCallback) {
        this.onClickViewHolderCallback = onClickViewHolderCallback
    }

    override fun convert(helper: InstallPackageViewHolder, item: ApkAssetBean) {
        helper.updateView(item)
    }

    inner class InstallPackageViewHolder(view: View) : IBaseViewMultiHolder<ApkAssetBean>(view) {
        private val iconIv: ImageView = view.findViewById(R.id.icon_iv)
        private val nameTv: TextView = view.findViewById(R.id.name_tv)
        private val versionInfoTv: TextView = view.findViewById(R.id.version_info_tv)
        private val sizeTv: TextView = view.findViewById(R.id.size_tv)
        private val installedBt: StateButton = view.findViewById(R.id.install_bt)
        private val optionRl: RelativeLayout = view.findViewById(R.id.option_rl)
        private val apkFlagsRtv: RoundTextView = view.findViewById(R.id.apk_flags_rtv)
        override fun updateView(dateItem: ApkAssetBean) {
            super.updateView(dateItem)
            when (dateItem.apkAssetType) {
                ApkAssetType.Apk -> {
                    dateItem.apkInfo?.let {
                        updateApkInfoView(it, dateItem)
                    }
                }
                ApkAssetType.XAPK -> {
                    dateItem.xApkInfo?.let {
                        updateXApkInfoView(it, dateItem)
                    }
                }
                ApkAssetType.Apks->{
                    dateItem.apksInfo?.let {
                        updateApksInfoView(it, dateItem)
                    }
                }
            }
        }

        private fun updateApkInfoView(apkInfo: ApkInfo, apkAssetBean: ApkAssetBean) {
            ImageLoader.Builder(mContext, ApkIconUrl(apkInfo.path, apkAssetBean.apkInfo?.versionCode
                ?: 0)
            )
                .setRequestOptions(ImageLoader.defaultRequestOptions(R.color.placeholder_color))
                .build(iconIv)
            apkFlagsRtv.visibility = View.GONE
            nameTv.apply {
                this.text = apkInfo.label
                this.requestLayout()
            }
            sizeTv.text = FormatUtils.formatFileLength(apkInfo.appSize)
            versionInfoTv.text = apkInfo.versionName
            installedBt.setOnClickListener {
                IntentUtils.installedApk(mContext, apkInfo.path)
                FirebaseUtils.clickInstallXApkOrApk(apkAssetBean)
            }
            optionRl.setOnClickListener {
                showOptionPopupWindow(it, apkInfo, apkAssetBean)
            }
        }

        private fun updateXApkInfoView(xapkInfo: XApkInfo, apkAssetBean: ApkAssetBean) {
            ImageLoader.Builder(mContext,
                XApkIconUrl(xapkInfo.path)
            )
                .setRequestOptions(ImageLoader.defaultRequestOptions(R.color.placeholder_color))
                .build(iconIv)
            apkFlagsRtv.apply {
                this.visibility = View.VISIBLE
                this.setText(R.string.xapk_tag)
            }
            nameTv.apply {
                this.text = xapkInfo.label
                this.requestLayout()
            }
            sizeTv.text = FormatUtils.formatFileLength(xapkInfo.appSize)
            versionInfoTv.text = xapkInfo.versionName
            installedBt.setOnClickListener {
                ViewUtils.installXApk(mContext, xapkInfo)
                FirebaseUtils.clickInstallXApkOrApk(apkAssetBean)
            }
            optionRl.setOnClickListener {
                showOptionPopupWindow(it, xapkInfo, apkAssetBean)
            }
        }

        private fun updateApksInfoView(apksInfo: ApksInfo, apkAssetBean: ApkAssetBean) {
            iconIv.setImageDrawable(
                IconicsUtils.getIcon(mContext, GoogleMaterial.Icon.gmd_android)
                    .sizeDp(48)
                    .color(ColorUtils.getThemeColor(mContext, R.attr.colorPrimary))
            )
            apkFlagsRtv.apply {
                this.visibility = View.VISIBLE
                this.setText(R.string.apks_tag)
            }
            nameTv.apply {
                this.text = apksInfo.fileName
                this.requestLayout()
            }
            sizeTv.text = FormatUtils.formatFileLength(apksInfo.fileSize)
            versionInfoTv.visibility = View.GONE
            installedBt.setOnClickListener {
                ViewUtils.installApks(mContext, apksInfo)
                FirebaseUtils.clickInstallXApkOrApk(apkAssetBean)
            }
            optionRl.setOnClickListener {
                showOptionPopupWindow(it, apksInfo, apkAssetBean)
            }
        }
    }

    private fun showOptionPopupWindow(authorView: View, apkInfo: ApkInfo, apkAssetBean: ApkAssetBean) {
        PopupMenu(mContext, authorView).apply {
            this.menu.apply {
                StringUtils.getStringArray(mContext, R.array.install_package_options)
                    .forEachIndexed { index, s ->
                        this.add(0, index, index, s)
                    }
            }
            this.setOnMenuItemClickListener {
                when (it.itemId) {
                    0 -> {
                        FsUtils.deleteFileOrDir(apkInfo.path)
                        onClickViewHolderCallback?.delete(apkAssetBean)
                    }
                    1 -> aboutAppInfo(apkInfo)
                    else -> {
                    }
                }
                return@setOnMenuItemClickListener true
            }
            this.show()
        }
    }

    private fun showOptionPopupWindow(authorView: View, xapkInfo: XApkInfo, apkAssetBean: ApkAssetBean) {
        PopupMenu(mContext, authorView).apply {
            this.menu.apply {
                StringUtils.getStringArray(mContext, R.array.install_package_options)
                    .forEachIndexed { index, s ->
                        this.add(0, index, index, s)
                    }
            }
            this.setOnMenuItemClickListener {
                when (it.itemId) {
                    0 -> {
                        FsUtils.deleteFileOrDir(xapkInfo.path)
                        onClickViewHolderCallback?.delete(apkAssetBean)
                    }
                    1 -> aboutAppInfo(xapkInfo)
                    else -> {
                    }
                }
                return@setOnMenuItemClickListener true
            }
            this.show()
        }
    }

    private fun showOptionPopupWindow(authorView: View, apksInfo: ApksInfo, apkAssetBean: ApkAssetBean) {
        PopupMenu(mContext, authorView).apply {
            this.menu.apply {
                StringUtils.getStringArray(mContext, R.array.install_package_options)
                    .forEachIndexed { index, s ->
                        this.add(0, index, index, s)
                    }
            }
            this.setOnMenuItemClickListener {
                when (it.itemId) {
                    0 -> {
                        FsUtils.deleteFileOrDir(apksInfo.filePath)
                        onClickViewHolderCallback?.delete(apkAssetBean)
                    }
                    1 -> aboutApksInfo(apksInfo)
                    else -> {
                    }
                }
                return@setOnMenuItemClickListener true
            }
            this.show()
        }
    }

    private fun aboutAppInfo(apkInfo: ApkInfo) {
        MaterialDialog(mContext).show {
            val copyInfo = StringUtils.fromHtml(context.getString(R.string.menu_app_info_hit
                , "${apkInfo.versionName}(${apkInfo.versionCode})"
                , apkInfo.packageName
                , apkInfo.path
                , FormatUtils.formatFileLength(apkInfo.appSize)
                , JodaTimeUtils.formatDataToShortDateInfo(JodaTimeUtils.longToDate(apkInfo.lastModified))))
            this.title(null, apkInfo.label)
            this@show.message(null, copyInfo)
            this.negativeButton(android.R.string.copy) {
                ClipboardUtil.getInstance(mContext).text = copyInfo
                SimpleToast.defaultShow(mContext, R.string.copy_successfully)
            }
            this.positiveButton(android.R.string.ok)
        }
    }

    private fun aboutAppInfo(xapkInfo: XApkInfo) {
        MaterialDialog(mContext).show {
            val copyInfo = StringUtils.fromHtml(context.getString(R.string.menu_app_info_hit
                , "${xapkInfo.versionName}(${xapkInfo.versionCode})"
                , xapkInfo.packageName
                , xapkInfo.path
                , FormatUtils.formatFileLength(xapkInfo.appSize)
                , JodaTimeUtils.formatDataToShortDateInfo(JodaTimeUtils.longToDate(xapkInfo.lastModified))))
            this.title(null, xapkInfo.label)
            this@show.message(null, copyInfo)
            this.negativeButton(android.R.string.copy) {
                ClipboardUtil.getInstance(mContext).text = copyInfo
                SimpleToast.defaultShow(mContext, R.string.copy_successfully)
            }
            this.positiveButton(android.R.string.ok)
        }
    }

    private fun aboutApksInfo(apksInfo: ApksInfo) {
        MaterialDialog(mContext).show {
            this.icon(null, IconicsUtils.getIcon(mContext, GoogleMaterial.Icon.gmd_android)
                    .color(ColorUtils.getThemeColor(mContext, R.attr.colorPrimary)))
            val copyInfo = StringUtils.fromHtml(context.getString(R.string.menu_apks_info_hit
                , apksInfo.filePath
                , FormatUtils.formatFileLength(apksInfo.fileSize)
                , JodaTimeUtils.formatDataToShortDateInfo(JodaTimeUtils.longToDate(apksInfo.lastModified))))
            this.title(null, apksInfo.fileName)
            this@show.message(null, copyInfo)
            this.negativeButton(android.R.string.copy) {
                ClipboardUtil.getInstance(mContext).text = copyInfo
                SimpleToast.defaultShow(mContext, R.string.copy_successfully)
            }
            this.positiveButton(android.R.string.ok)
        }
    }
}