package com.wafflestudio.snutt2

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.kakao.sdk.common.KakaoSdk
import com.naver.maps.map.NaverMapSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by makesource on 2016. 1. 17..
 */
@HiltAndroidApp
class SNUTTApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, getString(R.string.kakao_native_app_key))
        Timber.plant(Timber.DebugTree())
        NaverMapSdk.getInstance(this).client =
            NaverMapSdk.NaverCloudPlatformClient(getString(R.string.naver_map_client_id))
    }

    // targerSDK 34 대응 (https://github.com/joltup/rn-fetch-blob/issues/866#issuecomment-2227436658)
    override fun registerReceiver(receiver: BroadcastReceiver?, filter: IntentFilter?): Intent? = if (Build.VERSION.SDK_INT >= 34 && applicationInfo.targetSdkVersion >= 34) {
        super.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
    } else {
        super.registerReceiver(receiver, filter)
    }

    companion object {
        private const val TAG = "SNUTT_APPLICATION"
    }
}
