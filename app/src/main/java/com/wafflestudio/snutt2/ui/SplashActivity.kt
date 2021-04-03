package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.os.Handler
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity

/**
 * Created by makesource on 2017. 4. 1..
 */
class SplashActivity : SNUTTBaseActivity() {
    private val SPLASH_DISPLAY_LENGTH = 1000
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            startMain()
            finish()
        }, SPLASH_DISPLAY_LENGTH.toLong())
    }
}