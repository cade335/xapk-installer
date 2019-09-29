package io.xapk.apkinstaller.ui.activity

import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.tbruyelle.rxpermissions2.Permission
import io.reactivex.disposables.Disposable
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.misc.LinkUrlManager
import io.xapk.apkinstaller.misc.ViewPagerTabAdapter
import io.xapk.apkinstaller.model.bean.ApiException
import io.xapk.apkinstaller.model.event.ApkExportCompleteEvent
import io.xapk.apkinstaller.model.event.ApksExportCompleteEvent
import io.xapk.apkinstaller.model.event.XApkExportCompleteEvent
import io.xapk.apkinstaller.ui.base.BaseFragment
import io.xapk.apkinstaller.ui.base.IBaseActivity
import io.xapk.apkinstaller.ui.fragment.AppInstalledFragment
import io.xapk.apkinstaller.ui.fragment.InstallPackageFragment
import io.xapk.apkinstaller.utils.IntentUtils
import io.xapk.apkinstaller.utils.LaunchUtils
import io.xapk.apkinstaller.utils.firebase.FirebaseUtils
import io.xapk.apkinstaller.utils.rx.RxPermissionsUtils
import io.xapk.apkinstaller.utils.rx.RxSubscriber
import io.xapk.apkinstaller.utils.toast.Duration
import io.xapk.apkinstaller.utils.toast.SimpleToast
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : IBaseActivity(), Toolbar.OnMenuItemClickListener {
    private lateinit var appbarLayout: AppBarLayout
    private lateinit var toolBar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var mainVp: ViewPager
    private lateinit var mShareMenuItem: MenuItem
    private lateinit var mDevelopmentCodeMenuItem: MenuItem
    private lateinit var mPrivacyPolicyMenuItem: MenuItem
    private lateinit var mAboutMenuItem: MenuItem
    private var isWithPermission = false
    private var vpFragmentArrays: ArrayList<BaseFragment>? = null

    override fun getLayout(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        super.init()
        appbarLayout = findViewById(R.id.appbar_layout)
        toolBar = findViewById(R.id.tool_bar)
        tabLayout = findViewById(R.id.tab_layout)
        mainVp = findViewById(R.id.main_vp)
    }

    override fun nextStep() {
        super.nextStep()
        intent.data?.apply {
            LinkUrlManager.jumpToPage(mContext, this)
        }
        toolBar.apply {
            this.setTitle(R.string.xapk_install_name)
            this.inflateMenu(R.menu.menu_share)
            this.setOnMenuItemClickListener(this@MainActivity)
            mShareMenuItem = this.menu.findItem(R.id.action_share)
            mPrivacyPolicyMenuItem = this.menu.findItem(R.id.action_privacy_policy)
            mDevelopmentCodeMenuItem = this.menu.findItem(R.id.action_development_code)
            mAboutMenuItem = this.menu.findItem(R.id.action_about)
        }
        tabLayout.apply {
            this.tabGravity = TabLayout.GRAVITY_FILL
            this.tabMode = TabLayout.MODE_FIXED
            this.setupWithViewPager(mainVp)
            this.addOnTabSelectedListener(onTabSelectedListener)
        }
        mainVp.apply {
            val vpTitleArrays = arrayListOf(
                mContext.getString(R.string.installed),
                mContext.getString(R.string.apk_xapk)
            )
            vpFragmentArrays = arrayListOf(
                    AppInstalledFragment.newInstance(),
                    InstallPackageFragment.newInstance()
            )
            val pagerAdapter = ViewPagerTabAdapter(
                    supportFragmentManager,
                    vpFragmentArrays!!.filterNotNull().toTypedArray(),
                    vpTitleArrays.filterNotNull().toTypedArray()
            )
            this.adapter = pagerAdapter
            this.currentItem = 0
        }
        requestPermission()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.data?.apply {
            LinkUrlManager.jumpToPage(mContext, this)
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item) {
            mShareMenuItem -> {
                IntentUtils.shareText(mActivity, mContext.getString(R.string.share_text))
                FirebaseUtils.clickShare()
            }
            mPrivacyPolicyMenuItem -> {
                LaunchUtils.startPrivacyPolicyActivity(mContext)
            }
            mDevelopmentCodeMenuItem -> {
                LaunchUtils.startDevelopmentCodeActivity(mContext)
            }
            mAboutMenuItem -> {
                LaunchUtils.startAboutActivity(mContext)
            }
        }
        return false
    }

    private fun requestPermission() {
        RxPermissionsUtils.requestStoragePermission(mActivity)
            .subscribe(object : RxSubscriber<Permission>() {
                override fun onSubscribe(d: Disposable) {
                    super.onSubscribe(d)
                    isWithPermission = false
                }

                override fun rxOnNext(t: Permission) {
                    if (t.granted && !isWithPermission) {
                        isWithPermission = true
                    }
                }

                override fun onComplete() {
                    super.onComplete()
                    if (!isWithPermission) {
                        SimpleToast.defaultShow(
                            mContext,
                            mContext.getString(R.string.read_write_permission_prompt),
                            Duration.Long
                        )
                    }
                }

                override fun rxOnError(apiException: ApiException) = Unit
            })
    }

    private val onTabSelectedListener by lazy {
        object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab?) = Unit

            override fun onTabSelected(tab: TabLayout.Tab?) = Unit

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab?.apply {
                    val position = this.position
                    vpFragmentArrays?.apply {
                        if (position < this.size) {
                            val currentFragment = this[position]
                            val selectFragment = this[mainVp.currentItem]
                            if (currentFragment == selectFragment) {
                                currentFragment.scrollToBeginning()
                            }
                        }
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun apkExportComplete(apkExportCompleteEvent: ApkExportCompleteEvent) {
        SimpleToast.defaultShow(mContext,"${apkExportCompleteEvent.appInfo.label} ${mContext.getString(R.string.apk_export_complete)}")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun apksExportComplete(apksExportCompleteEvent: ApksExportCompleteEvent) {
        SimpleToast.defaultShow(mContext,"${apksExportCompleteEvent.appInfo.label} ${mContext.getString(R.string.apks_export_complete)}")
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun xApkExportComplete(xApkExportCompleteEvent: XApkExportCompleteEvent) {
        SimpleToast.defaultShow(mContext,"${xApkExportCompleteEvent.appInfo.label} ${mContext.getString(R.string.xapk_output_complete)}")
    }
}
