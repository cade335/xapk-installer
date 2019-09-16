package io.xapk.apkinstaller.ui.base

import android.content.Context
import android.os.Bundle
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity
import io.xapk.apkinstaller.utils.LanguageUtils

abstract class BaseActivity : RxAppCompatActivity() {
    protected val logTag: String by lazy { javaClass.simpleName }
    protected val mContext by lazy { this }
    protected val mActivity: BaseActivity by lazy { this }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageUtils.attachBaseContext(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        nextStep()
    }

    protected open fun init() {}

    protected open fun nextStep() {}
}