package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.adapter.IntroAdapter;

/**
 * Created by makesource on 2016. 3. 18..
 */
public class WelcomeActivity extends SNUTTBaseActivity {
    private ViewPager mViewPager;
    private IntroAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_welcome);
        int type = getIntent().getIntExtra(INTENT_KEY_FRAGMENT_TYPE, 0);
        switch (type) {
            case 0:
                setFragment(SignInFragment.newInstance());
                break;
            case 1:
                setFragment(SignUpFragment.newInstance());
                break;
            default:
                break;
        }
    }

    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
