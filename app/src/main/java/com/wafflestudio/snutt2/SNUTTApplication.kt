package com.wafflestudio.snutt2

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import com.facebook.hermes.reactexecutor.HermesExecutorFactory
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.JavaScriptExecutorFactory
import com.facebook.react.shell.MainReactPackage
import com.horcrux.svg.SvgPackage
import com.kakao.sdk.common.KakaoSdk
import com.naver.maps.map.NaverMapSdk
import com.reactnativecommunity.asyncstorage.AsyncStoragePackage
import com.reactnativecommunity.picker.RNCPickerPackage
import com.swmansion.gesturehandler.RNGestureHandlerPackage
import com.swmansion.reanimated.ReanimatedPackage
import com.swmansion.rnscreens.RNScreensPackage
import com.th3rdwave.safeareacontext.SafeAreaContextPackage
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import com.wafflestudio.snutt2.react_native.event.RNEventEmitterPackage
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
                RNEventEmitterPackage(),
            )

            override fun getJSMainModuleName(): String = "friends"

            override fun getJavaScriptExecutorFactory(): JavaScriptExecutorFactory {
                return HermesExecutorFactory()
            }
        }
    }

    // targerSDK 34 대응 (https://github.com/joltup/rn-fetch-blob/issues/866#issuecomment-2227436658)
    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?): Intent? {
        return if (Build.VERSION.SDK_INT >= 34 && applicationInfo.targetSdkVersion >= 34) {
            super.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            super.registerReceiver(receiver, filter)
        }
    }

    companion object {
        private const val TAG = "SNUTT_APPLICATION"
    }
}
