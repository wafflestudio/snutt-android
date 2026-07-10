package com.wafflestudio.snutt2.feature.syllabus

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.lib.android.webview.LoadState
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer

/**
 * 강의계획서(수강신청 시스템)를 인앱 WebView 로 띄우기 위한 컨테이너.
 *
 * 강의계획서 URL(noProxyUrl)은 sugang.snu.ac.kr 을 직접 가리키며, 해당 서버가
 * Referer 를 검증하기 때문에 요청에 Referer 헤더를 실어야 페이지가 열린다.
 * (강의평 WebView 와 달리 SNUTT 인증 쿠키는 주입하지 않는다 — 외부 도메인이다.)
 *
 * WebView 는 그 자체로 sugang 과 대화하는 미니 브라우저다. 페이지 로드의 Referer 도, 페이지
 * 안에서 트리거되는 파일 다운로드도 이 WebView 세션(쿠키/Referer/UA)의 연속이므로, 다운로드까지
 * 이 컨테이너 안에서 완결한다. ViewModel/Repository 는 관여하지 않는다.
 */
class SyllabusWebViewContainer(
    context: Context,
    private val onDownloadStart: () -> Unit,
) : WebViewContainer {
    val loadState: MutableState<LoadState> = mutableStateOf(LoadState.Loading(0))

    private var lastUrl: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override val webView: WebView = WebView(context).apply {
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        this.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (loadState.value != LoadState.Error) {
                    loadState.value = LoadState.Success
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                loadState.value = LoadState.Loading(0)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?,
            ) {
                loadState.value = LoadState.Error
            }
        }
        this.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                when (loadState.value) {
                    is LoadState.Loading -> LoadState.Loading(newProgress)
                    else -> null
                }?.let {
                    loadState.value = it
                }
            }
        }
        this.settings.javaScriptEnabled = true
        // noProxyUrl 자체는 항상 HTML 페이지지만, 그 페이지 안에서 PDF 등 첨부 파일 다운로드가
        // 트리거되면 WebView 가 렌더할 수 없으므로 WebView 세션째 다운로드로 처리한다.
        setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            SyllabusDownloader.enqueue(
                context = context,
                url = url,
                userAgent = userAgent,
                contentDisposition = contentDisposition,
                mimeType = mimeType,
                referer = SUGANG_REFERER,
            )
            onDownloadStart()
        }
    }

    override suspend fun openPage(url: String?) {
        lastUrl = url
        loadWithReferer(url)
    }

    fun reload() {
        loadWithReferer(lastUrl)
    }

    private fun loadWithReferer(url: String?) {
        val target = url ?: return
        loadState.value = LoadState.Loading(0)
        webView.loadUrl(target, mapOf("Referer" to SUGANG_REFERER))
    }

    companion object {
        // 이 Referer 가 있어야 sugang 서버가 강의계획서 페이지를 내려준다.
        private const val SUGANG_REFERER =
            "https://sugang.snu.ac.kr/sugang/cc/cc100InterfaceExcel.action"
    }
}
