package com.wafflestudio.snutt2.views

import android.os.Bundle
import androidx.activity.viewModels
import com.facebook.hermes.reactexecutor.HermesExecutorFactory
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class RNModuleActivity : ReactActivity() {

    private val rnViewModel: RNViewModel by viewModels()
    private var reactInstanceManager: ReactInstanceManager? = null
    private var rootView: ReactRootView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rnViewModel.done.observe(this) { done ->
            if (done) {
                val jsBundleFile = File(applicationContext.cacheDir, "android.jsbundle")
                reactInstanceManager = ReactInstanceManager.builder()
                    .setApplication(application)
                    .setCurrentActivity(this@RNModuleActivity)
                    .setJSBundleFile(jsBundleFile.absolutePath)
                    .addPackage(MainReactPackage())
                    .setInitialLifecycleState(LifecycleState.RESUMED)
                    .setJavaScriptExecutorFactory(HermesExecutorFactory())
                    .build()

                rootView = ReactRootView(this@RNModuleActivity)
                rootView?.startReactApplication(reactInstanceManager, "friends", null)
                setContentView(rootView)
            }
        }
    }

    override fun createReactActivityDelegate(): ReactActivityDelegate {
        return object : ReactActivityDelegate(this, mainComponentName) {
            override fun getLaunchOptions(): Bundle {
                return Bundle()
                    .apply {
                        putString("token", rnViewModel.token)
                    }
            }
        }
    }

//    override fun getMainComponentName(): String {
//        return "App"
//    }

    override fun onDestroy() {
        reactInstanceManager = null
        rootView = null
        super.onDestroy()
    }
}
