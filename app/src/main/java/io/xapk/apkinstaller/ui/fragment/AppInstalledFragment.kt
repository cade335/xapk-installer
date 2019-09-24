package io.xapk.apkinstaller.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ezy.ui.layout.LoadingLayout
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.misc.LoadImageDefaultScrollListener
import io.xapk.apkinstaller.model.bean.ApiException
import io.xapk.apkinstaller.utils.bean.apk.AppInfo
import io.xapk.apkinstaller.model.event.AppInstalledStatusEvent
import io.xapk.apkinstaller.ui.base.BaseFragment
import io.xapk.apkinstaller.ui.fragment.adapter.AppInstalledAdapter
import io.xapk.apkinstaller.ui.fragment.contract.AppInstalledFragContract
import io.xapk.apkinstaller.ui.fragment.presenter.AppInstalledFragPresenter
import io.xapk.apkinstaller.ui.widget.ColorSwipeRefreshLayout

class AppInstalledFragment : BaseFragment(), AppInstalledFragContract.StepView {
    private lateinit var loadingLayout: LoadingLayout
    private lateinit var swipeRefreshLayout: ColorSwipeRefreshLayout
    private lateinit var installedRecyclerView: RecyclerView
    private val appInstalledFragPresenter by lazy { AppInstalledFragPresenter() }
    private val appInstalledAdapter by lazy { AppInstalledAdapter(mActivity, arrayListOf()) }

    companion object {
        fun newInstance(): AppInstalledFragment {
            val args = Bundle()
            val fragment = AppInstalledFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_installed_app
    }

    override fun initView(rootView: View) {
        super.initView(rootView)
        loadingLayout = rootView.findViewById(R.id.loading_layout)
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout)
        installedRecyclerView = rootView.findViewById(R.id.installed_recycler_view)
    }

    override fun onLazyLoad() {
        super.onLazyLoad()
        appInstalledFragPresenter.attachView(this)
        installedRecyclerView.apply {
            this.adapter = appInstalledAdapter
            this.setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(mContext)
            this.addOnScrollListener(LoadImageDefaultScrollListener())
        }
        loadingLayout.apply {
            this.setEmptyText(mContext.getString(R.string.empty_applications))
            this.setRetryListener { getLoadInstalledApps() }
        }
        swipeRefreshLayout.setOnRefreshListener {
            getLoadInstalledApps()
        }
        getLoadInstalledApps()
    }

    private fun getLoadInstalledApps() = appInstalledFragPresenter.getLoadInstalledApps(mContext)

    override fun loadDataOnSubscribe() {
        swipeRefreshLayout.isRefreshing = true
        loadingLayout.showContent()
    }

    override fun loadDataOnSuccess(appInfoList: List<AppInfo>) {
        swipeRefreshLayout.isRefreshing = false
        if (appInfoList.isEmpty()) {
            loadingLayout.showEmpty()
        } else {
            appInstalledAdapter.setNewData(appInfoList)
        }
    }

    override fun loadDataOnError(apiException: ApiException) {
        swipeRefreshLayout.isRefreshing = false
        if (appInstalledAdapter.data.isEmpty()) {
            if (!apiException.displayMessage.isNullOrEmpty()) {
                loadingLayout.setErrorText(apiException.displayMessage)
            }
            loadingLayout.showError()
        }
    }

    override fun scrollToBeginning() {
        super.scrollToBeginning()
        installedRecyclerView.scrollToPosition(0)
    }

    override fun onChangeInstalledCallback(appInstalledStatusEvent: AppInstalledStatusEvent) {
        super.onChangeInstalledCallback(appInstalledStatusEvent)
        val appInfo = appInstalledStatusEvent.appInfo
        if (appInstalledStatusEvent.status == AppInstalledStatusEvent.Status.INSTALL) {
            appInstalledAdapter.addData(0, appInfo)
            installedRecyclerView.scrollToPosition(0)
        } else if (appInstalledStatusEvent.status == AppInstalledStatusEvent.Status.REMOVE) {
            var appInfoIndex = -1
            val packageName = appInstalledStatusEvent.appInfo.packageName
            for (item in appInstalledAdapter.data.indices) {
                if (appInstalledAdapter.data[item].packageName == packageName) {
                    appInfoIndex = item
                    break
                }
            }
            if (appInfoIndex != -1) {
                appInstalledAdapter.remove(appInfoIndex)
            }
        }
        if (appInstalledAdapter.data.isEmpty()) {
            loadingLayout.showEmpty()
        }else{
            loadingLayout.showContent()
        }
    }

    override fun onDestroyView() {
        appInstalledFragPresenter.detachView()
        super.onDestroyView()
    }
}