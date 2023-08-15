package com.wafflestudio.snutt2

import android.app.Application
import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import com.facebook.hermes.reactexecutor.HermesExecutorFactory
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.JavaScriptExecutorFactory
import com.facebook.react.shell.MainReactPackage
import com.horcrux.svg.SvgPackage
import com.swmansion.gesturehandler.RNGestureHandlerPackage
import com.swmansion.reanimated.ReanimatedPackage
import com.swmansion.rnscreens.RNScreensPackage
import com.th3rdwave.safeareacontext.SafeAreaContextPackage
import com.wafflestudio.snutt2.lib.rx.DirectFirstHandleScheduler
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import rxdogtag2.RxDogTag
import timber.log.Timber

/**
 * Created by makesource on 2016. 1. 17..
 */
@HiltAndroidApp
class SNUTTApplication : Application(), ReactApplication {

    override fun onCreate() {
        super.onCreate()
        RxDogTag.install()
        Timber.plant(Timber.DebugTree())
        RxAndroidPlugins.setMainThreadSchedulerHandler {
            DirectFirstHandleScheduler(true)
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        TimetableWidgetProvider.refreshWidget(applicationContext)
    }

    override fun getReactNativeHost(): ReactNativeHost {
        return object : ReactNativeHost(this) {
            override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

            override fun getPackages(): List<ReactPackage> = listOf(
                MainReactPackage(),
                RNScreensPackage(),
                RNGestureHandlerPackage(),
                SafeAreaContextPackage(),
                ReanimatedPackage(),
                SvgPackage()
            )

            override fun getJSMainModuleName(): String = "friends"

            override fun getJavaScriptExecutorFactory(): JavaScriptExecutorFactory {
                return HermesExecutorFactory()
            }
        }
    }

    companion object {
        private const val TAG = "SNUTT_APPLICATION"
    }
}
