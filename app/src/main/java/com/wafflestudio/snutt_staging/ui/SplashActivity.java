package com.wafflestudio.snutt_staging.ui;

import android.os.Bundle;
import android.os.Handler;

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseActivity;

/**
 * Created by makesource on 2017. 4. 1..
 */

public class SplashActivity extends SNUTTBaseActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startMain();
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
