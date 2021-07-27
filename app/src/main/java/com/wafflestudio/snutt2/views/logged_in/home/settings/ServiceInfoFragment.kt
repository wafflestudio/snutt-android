package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.databinding.FragmentBrowserBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ServiceInfoFragment : BaseFragment() {

    private lateinit var binding: FragmentBrowserBinding

    @Inject
    lateinit var storage: SNUTTStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBrowserBinding.inflate(inflater, container, false)
        val headers = HashMap<String, String>()
        headers["x-access-apikey"] = resources.getString(R.string.api_key)
        headers["x-access-token"] = storage.accessToken.getValue()

        binding.title.text = "서비스 약관"

        binding.backButton.throttledClicks()
            .bindUi(this) {
                findNavController().popBackStack()
            }

        binding.webView.webViewClient = WebViewClient() // 이걸 안해주면 새창이 뜸
        binding.webView.loadUrl(
            getString(R.string.api_server) + getString(R.string.terms),
            headers
        )

        return binding.root
    }
}
