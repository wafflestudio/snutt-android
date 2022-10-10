package com.wafflestudio.snutt2

import android.app.Application
import com.facebook.FacebookSdk
import com.wafflestudio.snutt2.lib.rx.DirectFirstHandleScheduler
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
        RxDogTag.install()
        Timber.plant(Timber.DebugTree())
        RxAndroidPlugins.setMainThreadSchedulerHandler {
            DirectFirstHandleScheduler(true)
        }
    }

    companion object {
        private const val TAG = "SNUTT_APPLICATION"
    }
}
