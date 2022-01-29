package com.wafflestudio.snutt2.views.logged_in.home.reviews

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.jakewharton.rxbinding4.view.clicks
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.databinding.FragmentReviewsBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReviewsFragment : BaseFragment() {

    private lateinit var binding: FragmentReviewsBinding

    @Inject
    lateinit var storage: SNUTTStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewsBinding.inflate(inflater)
        return binding.root
    }

    private val loadState: MutableStateFlow<LoadState> = MutableStateFlow(LoadState.Loading)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            loadState.collect {
                setVisibility(it)
            }
        }

        WebView.setWebContentsDebuggingEnabled(true)

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                loadState.compareAndSet(LoadState.Loading, LoadState.Failure)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                loadState.compareAndSet(LoadState.Loading, LoadState.Success)
                super.onPageFinished(view, url)
            }
        }

        reloadWebView()

        binding.webView.settings.javaScriptEnabled = true
        binding.retryButton.clicks()
            .bindUi(this) {
                reloadWebView()
            }
    }

    private var backPressCallback: OnBackPressedCallback? = null

    override fun onResume() {
        super.onResume()
        backPressCallback = (object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    requireActivity().finish()
                }
            }
        }).also {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, it)
        }
    }

    override fun onPause() {
        super.onPause()
        backPressCallback?.remove()
        backPressCallback = null
    }


    private fun reloadWebView() {
        CookieManager.getInstance().apply {
            setCookie(
                "snutt-ev-web-dev.wafflestudio.com",
                "x-access-apikey=${resources.getString(R.string.api_key)}"
            )
            setCookie(
                "snutt-ev-web-dev.wafflestudio.com",
                "x-access-token=${storage.accessToken.get()}"
            )
        }.flush()
        loadState.value = LoadState.Loading
        binding.webView.loadUrl(getString(R.string.review_base_url) + "/main")
    }

    private fun setVisibility(state: LoadState) {
        when (state) {
            LoadState.Success -> {
                binding.webView.visibility = View.VISIBLE
                binding.placeholder.visibility = View.GONE
                binding.placeholderLoading.visibility = View.GONE
                binding.placeholderError.visibility = View.GONE
            }
            LoadState.Loading -> {
                binding.webView.visibility = View.GONE
                binding.placeholder.visibility = View.VISIBLE
                binding.placeholderLoading.visibility = View.VISIBLE
                binding.placeholderError.visibility = View.GONE
            }
            LoadState.Failure -> {
                binding.webView.visibility = View.GONE
                binding.placeholder.visibility = View.VISIBLE
                binding.placeholderLoading.visibility = View.GONE
                binding.placeholderError.visibility = View.VISIBLE
            }
        }
    }

    enum class LoadState {
        Success,
        Loading,
        Failure
    }
}
