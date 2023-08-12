package com.wafflestudio.snutt2

import android.app.Application
import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Created by makesource on 2016. 1. 17..
 */
@HiltAndroidApp
class SNUTTApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    @OptIn(ExperimentalAnimationApi::class)
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        TimetableWidgetProvider.refreshWidget(applicationContext)
    }

    companion object {
        private const val TAG = "SNUTT_APPLICATION"
    }
}
