package com.wafflestudio.snutt2.lib.android.webview

import android.content.Context
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

class SyllabusWebViewContainer(context: Context) : WebViewContainer {
    override val webView: WebView = WebView(context).apply {
        this.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()

                // NOTE: 수강스누에서 Referer 헤더를 검사해서, 레퍼러가 수강스누가 아니면 홈으로 리다이렉트 시켜 버린다.
                view?.loadUrl(url, mapOf("Referer" to "https://sugang.snu.ac.kr/sugang/cc/cc100InterfaceSrch.action"))
                return true
            }
        }
        this.settings.javaScriptEnabled = true
    }

    override suspend fun openPage(url: String?) {
        CookieManager.getInstance().apply {
            // NOTE: 웹뷰에 다른 url을 로드했을 때 "이미 수강신청 프로그램을 사용중입니다"가 뜨는 것을 방지한다.
            removeAllCookies(null)
            flush()
        }
        webView.loadUrl(url ?: "https://https://sugang.snu.ac.kr/")
    }
}
