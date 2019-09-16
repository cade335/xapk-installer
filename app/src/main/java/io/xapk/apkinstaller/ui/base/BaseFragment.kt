package io.xapk.apkinstaller.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.trello.rxlifecycle3.components.support.RxFragment
import io.xapk.apkinstaller.model.event.AppInstalledStatusEvent
import io.xapk.apkinstaller.model.event.EventManager
import io.xapk.apkinstaller.utils.AppLogger
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

abstract class BaseFragment : RxFragment() {
    protected val logTag: String by lazy { javaClass.simpleName }
    protected lateinit var mContext: Context
    protected lateinit var mActivity: FragmentActivity
    private var rootView: View? = null
    private var isLoadData: Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = activity!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = rootView ?: inflater.inflate(getLayout(), container, false)
        val parent = rootView!!.parent
        if (parent != null && parent is ViewGroup) {
            parent.removeView(rootView)
        }
        EventManager.register(this)
        initView(rootView!!)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (this.userVisibleHint && !isLoadData) {
            isLoadData = true
            onLazyLoad()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (view != null && isVisibleToUser) {
            if (!isLoadData) {
                isLoadData = true
                onLazyLoad()
            } else {
                onPageVisible()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventManager.unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeInstalled(appInstalledStatusEvent: AppInstalledStatusEvent) {
        onChangeInstalledCallback(appInstalledStatusEvent)
    }

    protected abstract fun getLayout(): Int

    protected open fun initView(rootView: View) {
        AppLogger.d(logTag, "initView")
    }

    protected open fun onLazyLoad() {
        AppLogger.d(logTag, "onLazyLoad")
    }
    protected open fun onChangeInstalledCallback(appInstalledStatusEvent: AppInstalledStatusEvent) = Unit
    protected open fun onPageVisible() = Unit
    open fun scrollToBeginning() = Unit
}
