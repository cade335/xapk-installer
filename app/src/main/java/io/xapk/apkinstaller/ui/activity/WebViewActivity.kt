package io.xapk.apkinstaller.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import io.xapk.apkinstaller.R
import io.xapk.apkinstaller.ui.base.IBaseActivity
import io.xapk.apkinstaller.utils.IconicsUtils

/**
 * @author xiongke
 * @date 2019-09-17
 */
class WebViewActivity : IBaseActivity(), Toolbar.OnMenuItemClickListener {
    private lateinit var toolbar: Toolbar
    private lateinit var webView: WebView
    private lateinit var webProgressbar: ProgressBar
    private var webUrl: String = String()
    private var mTitle: String? = null

    companion object {
        const val KEY_WEB_URL = "web_url_key"
        const val KEY_WEB_TITLE = "web_title_key"
        fun newInstanceIntent(context: Context, url: String, title: String? = null): Intent {
            return Intent(context, WebViewActivity::class.java).apply {
                this.putExtra(KEY_WEB_URL, url)
                title?.let {
                    this.putExtra(KEY_WEB_TITLE, it)
                }
            }
        }
    }

    override fun getLayout(): Int {
        return R.layout.activity_webview
    }

    override fun init() {
        super.init()
        webView = findViewById(R.id.web_view)
        toolbar = findViewById(R.id.tool_bar)
        webProgressbar = findViewById(R.id.web_loading_progressbar)
    }

    override fun nextStep() {
        super.nextStep()
        intent.getStringExtra(KEY_WEB_URL)?.let {
            webUrl = it
        }
        intent.getStringExtra(KEY_WEB_TITLE)?.let {
            mTitle = it
        }
        toolbar.apply {
            this.title = if (TextUtils.isEmpty(mTitle)) mContext.getString(R.string.q_loading) else mTitle
            this.inflateMenu(R.menu.menu_web)
            this.setOnMenuItemClickListener(this@WebViewActivity)
            this.navigationIcon = IconicsUtils.getActionBarIcon(mContext, GoogleMaterial.Icon.gmd_close)
            this.setNavigationOnClickListener { finish() }
        }
        webView.apply {
            initWebViewsSetting()
            this.webViewClient = WebViewClient()
            this.webChromeClient = object : WebChromeClient() {
                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    mTitle ?: let {
                        toolbar.title = title
                    }
                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    if (newProgress < 100) {
                        webProgressbar.visibility = View.VISIBLE
                        webProgressbar.progress = newProgress
                    } else {
                        webProgressbar.visibility = View.GONE
                    }
                }
            }
            this.loadUrl(webUrl)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_refresh -> webView.reload()
            else -> {
            }
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewsSetting() {
        webView.settings.apply {
            this.javaScriptCanOpenWindowsAutomatically = true
            this.pluginState = WebSettings.PluginState.ON_DEMAND
            this.cacheMode = WebSettings.LOAD_NO_CACHE
            this.javaScriptEnabled = true
            this.setSupportZoom(true)
            this.builtInZoomControls = false
            this.savePassword = false
            this.setRenderPriority(WebSettings.RenderPriority.HIGH)
            this.textZoom = 100
            this.databaseEnabled = true
            this.setAppCacheEnabled(true)
            this.loadsImagesAutomatically = true
            this.setSupportMultipleWindows(false)
            this.blockNetworkImage = false
            this.allowFileAccess = true
            this.allowFileAccessFromFileURLs = false
            this.allowUniversalAccessFromFileURLs = false
            this.javaScriptCanOpenWindowsAutomatically = true
            this.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            this.loadWithOverviewMode = true
            this.useWideViewPort = true
            this.domStorageEnabled = true
            this.setNeedInitialFocus(true)
        }
    }
}