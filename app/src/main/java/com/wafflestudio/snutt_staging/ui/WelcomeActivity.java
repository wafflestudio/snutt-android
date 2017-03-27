package com.wafflestudio.snutt_staging.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseActivity;
import com.wafflestudio.snutt_staging.adapter.IntroAdapter;

/**
 * Created by makesource on 2016. 3. 18..
 */
public class WelcomeActivity extends SNUTTBaseActivity implements SignUpFragment.OnSignUpSucceedListener {

    private ViewPager mViewPager;
    private IntroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //activityList.add(this);
        //finishAll();
        setContentView(R.layout.activity_welcome);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new IntroAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onSignUpSucceed(String id, String password) {
        mViewPager.setCurrentItem(0);
        SignInFragment fragment = (SignInFragment) adapter.getItem(0);
        fragment.setInfo(id, password);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //activityList.remove(this);
    }
}
