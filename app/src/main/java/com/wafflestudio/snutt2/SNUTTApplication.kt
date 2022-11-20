package com.wafflestudio.snutt2

import android.app.Application
import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
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
class SNUTTApplication : Application() {

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

    companion object {
        private const val TAG = "SNUTT_APPLICATION"
    }
}
