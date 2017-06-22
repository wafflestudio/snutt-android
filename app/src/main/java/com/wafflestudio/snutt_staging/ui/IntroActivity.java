package com.wafflestudio.snutt_staging.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.CirclePageIndicator;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseActivity;
import com.wafflestudio.snutt_staging.adapter.IntroPagerAdapter;

/**
 * Created by makesource on 2017. 6. 22..
 */

public class IntroActivity extends SNUTTBaseActivity {
    private IntroPagerAdapter mIntroPagerAdapter;
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_intro);

        mIntroPagerAdapter = new IntroPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mIntroPagerAdapter);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
    }
}
