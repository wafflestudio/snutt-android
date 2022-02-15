package com.wafflestudio.snutt2.views.logged_in.home.reviews

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.jakewharton.rxbinding4.view.clicks
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.databinding.FragmentReviewsBinding
import com.wafflestudio.snutt2.lib.SnuttUrls
import com.wafflestudio.snutt2.lib.android.HomePage
import com.wafflestudio.snutt2.lib.android.HomePagerController
import com.wafflestudio.snutt2.lib.android.ReviewUrlController
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.toOptional
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx3.asObservable
import java.net.URL
import javax.inject.Inject

@AndroidEntryPoint
class ReviewsFragment : BaseFragment() {

    private lateinit var binding: FragmentReviewsBinding

    @Inject
    lateinit var storage: SNUTTStorage

    @Inject
    lateinit var homePagerController: HomePagerController

    @Inject
    lateinit var reviewUrlController: ReviewUrlController

    @Inject
    lateinit var snuttUrls: SnuttUrls

    private val loadState: MutableStateFlow<LoadState> =
        MutableStateFlow(LoadState.InitialLoading(0))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewsBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            loadState.collect {
                setVisibility(it)
            }
        }

        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        if (loadState.value == LoadState.InitialLoading(0)) {
            reloadWebView(snuttUrls.getReviewMain())
        }

        binding.webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                if (loadState.value != LoadState.Error) {
                    loadState.value = LoadState.Success
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                loadState.value = when (loadState.value) {
                    is LoadState.InitialLoading -> LoadState.InitialLoading(0)
                    else -> LoadState.Loading(0)
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                loadState.value = LoadState.Error
            }
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                when (loadState.value) {
                    is LoadState.InitialLoading -> LoadState.InitialLoading(newProgress)
                    is LoadState.Loading -> LoadState.Loading(newProgress)
                    else -> null
                }?.let {
                    loadState.value = it
                }
            }
        }

        binding.webView.settings.javaScriptEnabled = true

        binding.retryButton.clicks()
            .bindUi(this) {
                reloadWebView()
            }

        reviewUrlController.urlEvent
            .map { it.toOptional() }.asObservable()
            .bindUi(this) {
                if (it.value != null) {
                    reloadWebView(it.value)
                    reviewUrlController.flushAfterObserve()
                }
            }
    }

    private fun reloadWebView(url: String? = null) {
        val reviewUrlHost = URL(requireContext().getString(R.string.review_base_url)).host
        CookieManager.getInstance().apply {
            setCookie(
                reviewUrlHost,
                "x-access-apikey=${resources.getString(R.string.api_key)}"
            )
            setCookie(
                reviewUrlHost,
                "x-access-token=${storage.accessToken.get()}"
            )
        }.flush()
        if (url == null) binding.webView.reload()
        else binding.webView.loadUrl(url)
    }

    private fun setVisibility(state: LoadState) {
        when (state) {
            LoadState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.appBar.visibility = View.GONE
                binding.error.visibility = View.GONE
            }
            LoadState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.appBar.visibility = View.GONE
                binding.error.visibility = View.VISIBLE
            }
            is LoadState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.progress = state.progress
                binding.appBar.visibility = View.GONE
                binding.error.visibility = View.GONE
            }
            is LoadState.InitialLoading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.progress = state.progress
                binding.appBar.visibility = View.VISIBLE
                binding.error.visibility = View.GONE
            }
        }
    }

    private var backPressCallback: OnBackPressedCallback? = null

    override fun onResume() {
        super.onResume()
        backPressCallback = (
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.webView.canGoBack()) {
                        binding.webView.goBack()
                    } else {
                        homePagerController.update(HomePage.Timetable)
                    }
                }
            }
            ).also {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, it)
        }
    }

    override fun onPause() {
        super.onPause()
        backPressCallback?.remove()
        backPressCallback = null
    }

    sealed class LoadState {
        object Success : LoadState()
        object Error : LoadState()
        data class Loading(val progress: Int) : LoadState()
        data class InitialLoading(val progress: Int) : LoadState()
    }
}
