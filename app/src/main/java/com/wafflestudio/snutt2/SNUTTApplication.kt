package com.wafflestudio.snutt2

import android.app.Application
import com.facebook.FacebookSdk
import com.wafflestudio.snutt2.lib.rx.DirectFirstHandlerScheduler
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import rxdogtag2.RxDogTag
import timber.log.Timber

/**
 * Created by makesource on 2016. 1. 17..
 */
@HiltAndroidApp
class SNUTTApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FacebookSdk.sdkInitialize(this)
        RxDogTag.install()
        Timber.plant(Timber.DebugTree())
        RxAndroidPlugins.setMainThreadSchedulerHandler {
            DirectFirstHandlerScheduler(true)
        }
    }

    companion object {
        private const val TAG = "SNUTT_APPLICATION"
    }
}
