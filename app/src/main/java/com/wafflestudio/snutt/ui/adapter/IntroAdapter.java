package com.wafflestudio.snutt.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.wafflestudio.snutt.ui.SignInFragment;
import com.wafflestudio.snutt.ui.SignUpFragment;

/**
 * Created by makesource on 2016. 3. 26..
 */
public class IntroAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "INTRO_ADAPTER";
    private static final int NUM_ITEMS = 2;

    private static SignInFragment signInFragment;
    private static SignUpFragment signUpFragment;

    public IntroAdapter(FragmentManager fm) {
        super(fm);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (signInFragment == null) signInFragment = SignInFragment.newInstance();
                return signInFragment;
            case 1:
                if (signUpFragment == null) signUpFragment = signUpFragment.newInstance();
                return signUpFragment;
            default:
                Log.e (TAG, "intro fragment position is out of index!");
                return null;
        }
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "로그인";
            case 1:
                return "회원가입";
        }
        return null;
    }
}
