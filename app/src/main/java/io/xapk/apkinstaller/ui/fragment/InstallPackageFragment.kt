package io.xapk.apkinstaller.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ezy.ui.layout.LoadingLayout
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.misc.LoadImageDefaultScrollListener
import io.xapk.apkinstaller.model.bean.ApiException
import io.xapk.apkinstaller.ui.base.BaseFragment
import io.xapk.apkinstaller.ui.fragment.adapter.InstallPackageAdapter
import io.xapk.apkinstaller.ui.fragment.contract.InstallPackageFragContract
import io.xapk.apkinstaller.ui.fragment.presenter.InstallPackageFragPresenter
import io.xapk.apkinstaller.ui.widget.ColorSwipeRefreshLayout
import io.xapk.apkinstaller.utils.bean.ApkAssetBean

class InstallPackageFragment : BaseFragment(), InstallPackageFragContract.StepView {
    private lateinit var colorSwipeRefreshLayout: ColorSwipeRefreshLayout
    private lateinit var installPackageRecyclerView: RecyclerView
    private lateinit var loadingLayout: LoadingLayout
    private val installPackageAdapter by lazy { InstallPackageAdapter(arrayListOf()) }
    private val installPackageFragPresenter by lazy { InstallPackageFragPresenter() }


    companion object {
        fun newInstance(): InstallPackageFragment {
            val args = Bundle()
            val fragment = InstallPackageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_install_package
    }

    override fun initView(rootView: View) {
        super.initView(rootView)
        colorSwipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout)
        installPackageRecyclerView = rootView.findViewById(R.id.install_package_recycler_view)
        loadingLayout = rootView.findViewById(R.id.loading_layout)
    }

    override fun onLazyLoad() {
        super.onLazyLoad()
        installPackageFragPresenter.attachView(this)
        installPackageRecyclerView.apply {
            this.adapter = installPackageAdapter
            this.setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(mContext)
            this.addOnScrollListener(LoadImageDefaultScrollListener())
        }
        loadingLayout.apply {
            this.setEmptyText(mContext.getString(R.string.empty_install_package))
            this.setRetryListener { getLoadInstallPackagedApps() }
        }
        installPackageAdapter.setOnClickViewHolderCallback(object : InstallPackageAdapter.OnClickViewHolderCallback {
            override fun delete(apkAssetBean: ApkAssetBean) {
                val position = installPackageAdapter.data.indexOf(apkAssetBean)
                if (position != -1) {
                    installPackageAdapter.remove(position)
                }
                if (installPackageAdapter.data.isEmpty()) {
                    loadingLayout.showEmpty()
                }
            }
        })
        colorSwipeRefreshLayout.setOnRefreshListener { getLoadInstallPackagedApps() }
        getLoadInstallPackagedApps()
    }

    override fun loadDataOnSubscribe() {
        colorSwipeRefreshLayout.isRefreshing = true
        loadingLayout.showContent()
    }

    override fun loadDataOnSuccess(apkAssetBeanList: List<ApkAssetBean>) {
        colorSwipeRefreshLayout.isRefreshing = false
        if (apkAssetBeanList.isEmpty()) {
            loadingLayout.showError()
        } else {
            installPackageAdapter.setNewData(apkAssetBeanList)
        }
    }

    override fun loadDataOnError(apiException: ApiException) {
        colorSwipeRefreshLayout.isRefreshing = false
        if (installPackageAdapter.data.isEmpty()) {
            if (!apiException.displayMessage.isNullOrEmpty()) {
                loadingLayout.setErrorText(apiException.displayMessage)
            }
            loadingLayout.showError()
        }
    }

    private fun getLoadInstallPackagedApps() = installPackageFragPresenter.loadLocalInstallPackageApks(mContext)

    override fun scrollToBeginning() {
        super.scrollToBeginning()
        installPackageRecyclerView.scrollToPosition(0)
    }

    override fun onDestroyView() {
        installPackageFragPresenter.detachView()
        super.onDestroyView()
    }
}