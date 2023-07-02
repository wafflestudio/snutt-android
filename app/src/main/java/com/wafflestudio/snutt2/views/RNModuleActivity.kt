package com.wafflestudio.snutt2.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.facebook.hermes.reactexecutor.HermesExecutorFactory
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.shell.MainReactPackage
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class RNModuleActivity : AppCompatActivity() {

    private val rnViewModel: RNViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rnViewModel.done.observe(this) { done ->
            if (done) {
                val jsBundleFile = File(applicationContext.cacheDir, "android.jsbundle")
                val reactInstanceManager = ReactInstanceManager.builder()
                    .setApplication(application)
                    .setCurrentActivity(this@RNModuleActivity)
                    .setJSBundleFile(jsBundleFile.absolutePath)
                    .addPackage(MainReactPackage())
                    .setInitialLifecycleState(LifecycleState.RESUMED)
                    .setJavaScriptExecutorFactory(HermesExecutorFactory())
                    .build()

                val rootView = ReactRootView(this@RNModuleActivity)
                rootView.startReactApplication(reactInstanceManager, "friends", null)
                setContentView(rootView)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
