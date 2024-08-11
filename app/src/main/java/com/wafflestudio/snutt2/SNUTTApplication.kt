package com.wafflestudio.snutt2

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import com.facebook.hermes.reactexecutor.HermesExecutorFactory
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.JavaScriptExecutorFactory
import com.facebook.react.shell.MainReactPackage
import com.horcrux.svg.SvgPackage
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.naver.maps.map.NaverMapSdk
import com.reactnativecommunity.asyncstorage.AsyncStoragePackage
import com.reactnativecommunity.picker.RNCPickerPackage
import com.swmansion.gesturehandler.RNGestureHandlerPackage
import com.swmansion.reanimated.ReanimatedPackage
import com.swmansion.rnscreens.RNScreensPackage
import com.th3rdwave.safeareacontext.SafeAreaContextPackage
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by makesource on 2016. 1. 17..
 */
@HiltAndroidApp
class SNUTTApplication : Application(), ReactApplication {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))
        val keyHash = Utility.getKeyHash(this)
        Log.d("plgafhdtest",keyHash)
        Timber.plant(Timber.DebugTree())
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(getString(R.string.naver_map_client_id))
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
                RNCPickerPackage(),
                SafeAreaContextPackage(),
                ReanimatedPackage(),
                SvgPackage(),
                AsyncStoragePackage(),
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
