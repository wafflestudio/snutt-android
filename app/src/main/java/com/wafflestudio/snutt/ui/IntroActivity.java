package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.viewpagerindicator.CirclePageIndicator;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.adapter.IntroPagerAdapter;

/**
 * Created by makesource on 2017. 6. 22..
 */

public class IntroActivity extends SNUTTBaseActivity {
    private IntroPagerAdapter mIntroPagerAdapter;
    private ViewPager mViewPager;
    private CirclePageIndicator mIndicator;
    private Button signIn, signUp;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        activityList.add(this);
        setContentView(R.layout.activity_intro);
        mIntroPagerAdapter = new IntroPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mIntroPagerAdapter);
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mViewPager);
        signIn = (Button) findViewById(R.id.sign_in);
        signUp = (Button) findViewById(R.id.sign_up);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWelcome(0);
            }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWelcome(1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
