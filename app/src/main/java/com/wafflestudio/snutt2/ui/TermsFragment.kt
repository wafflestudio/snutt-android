package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.manager.PrefManager
import java.util.*

/**
 * Created by makesource on 2017. 1. 24..
 */
class TermsFragment : SNUTTBaseFragment() {
    private var webView: WebView? = null
    private var headers: MutableMap<String, String>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_webview, container, false)
        headers = HashMap<String, String>()
        headers!!["x-access-apikey"] = resources.getString(R.string.api_key)
        headers!!["x-access-token"] = PrefManager.instance!!.prefKeyXAccessToken!!
        webView = rootView.findViewById<View>(R.id.webview) as WebView
        webView!!.webViewClient = WebViewClient() // 이걸 안해주면 새창이 뜸
        webView!!.loadUrl(getString(R.string.api_server) + getString(R.string.terms), headers!!)
        return rootView
    }
}