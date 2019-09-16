package io.xapk.apkinstaller.ui.base

import androidx.appcompat.widget.Toolbar
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import io.xapk.apkinstaller.model.event.AppInstalledStatusEvent
import io.xapk.apkinstaller.model.event.EventManager
import io.xapk.apkinstaller.utils.IconicsUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class IBaseActivity : BaseActivity() {
    override fun init() {
        super.init()
        setContentView(getLayout())
        EventManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeInstalled(appInstalledStatusEvent: AppInstalledStatusEvent) {
        onChangeInstalledCallback(appInstalledStatusEvent)
    }

    protected abstract fun getLayout(): Int

    protected open fun onChangeInstalledCallback(appInstalledStatusEvent: AppInstalledStatusEvent) = Unit

    protected fun initToolbarBack(toolbar: Toolbar?) {
        toolbar?.let {
            it.navigationIcon = IconicsUtils.getActionBarIcon(mContext, GoogleMaterial.Icon.gmd_arrow_back)
            it.setNavigationOnClickListener { onBackPressed() }
        }
    }
}