package com.wafflestudio.snutt2

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.facebook.react.ReactActivity
import com.wafflestudio.snutt2.react_native.ReactNativeBundleManager
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import javax.inject.Inject

@AndroidEntryPoint
class RNModuleActivity : ReactActivity() {

    private val userViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var remoteConfig: RemoteConfig

    private val friendBundleManager by lazy {
        ReactNativeBundleManager(this, remoteConfig, userViewModel.accessToken.value)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            friendBundleManager.bundleLoadCompleteSignal.filter { it }.collect {
                withContext(Dispatchers.Main) {
                    setContentView(friendBundleManager.reactRootView)
                }
            }
        }
    }
}
