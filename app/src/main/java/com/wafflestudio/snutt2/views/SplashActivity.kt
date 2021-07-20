package com.wafflestudio.snutt2.views

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseActivity
import com.wafflestudio.snutt2.lib.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by makesource on 2017. 4. 1..
 */

@AndroidEntryPoint
class SplashActivity : BaseActivity() {
    private val SPLASH_DISPLAY_LENGTH = 1000
    public override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(
            {
                startActivity(Intent(this, RootActivity::class.java))
                finish()
            },
            SPLASH_DISPLAY_LENGTH.toLong()
        )
    }
}
