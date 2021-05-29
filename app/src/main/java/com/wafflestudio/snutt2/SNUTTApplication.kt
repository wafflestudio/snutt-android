package com.wafflestudio.snutt2

import android.app.Application
import com.facebook.FacebookSdk
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by makesource on 2016. 1. 17..
 */
@HiltAndroidApp
class SNUTTApplication : Application() {

    override fun onCreate() {
        FacebookSdk.sdkInitialize(this)
        Timber.plant(Timber.DebugTree())
        super.onCreate()
    }

    companion object {
        private const val TAG = "SNUTT_APPLICATION"
    }
}
