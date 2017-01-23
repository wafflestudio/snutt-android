package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;

/**
 * Created by makesource on 2016. 11. 20..
 */

public class SettingsMainActivity extends SNUTTBaseActivity
        implements FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "SETTING_MAIN_ACTIVITY" ;

    private final static String TAG_FRAGMENT_ACCOUNT = "TAG_FRAGMENT_ACCOUNT";
    private final static String TAG_FRAGMENT_TIMETABLE = "TAG_FRAGMENT_TIMETABLE";
    private final static String TAG_FRAGMENT_DEVELOPER = "TAG_FRAGMENT_DEVELOPER";
    private final static String TAG_FRAGMENT_REPORT = "TAG_FRAGMENT_REPORT";
    private final static String TAG_FRAGMENT_LICENSE = "TAG_FRAGMENT_LICENSE";
    private final static String TAG_FRAGMENT_TERMS = "TAG_FRAGMENT_TERMS";

    private final static String[] FRAGMENT_TAGS = {
            TAG_FRAGMENT_ACCOUNT,
            TAG_FRAGMENT_TIMETABLE,
            TAG_FRAGMENT_DEVELOPER,
            TAG_FRAGMENT_REPORT,
            TAG_FRAGMENT_LICENSE,
            TAG_FRAGMENT_TERMS
    };

    private final static int FRAGMENT_ERROR = -1;
    private final static int FRAGMENT_ACCOUNT = 0;
    private final static int FRAGMENT_TIMETABLE = 1;
    private final static int FRAGMENT_DEVELOPER = 2;
    private final static int FRAGMENT_REPORT = 3;
    private final static int FRAGMENT_LICENSE = 4;
    private final static int FRAGMENT_TERMS = 5;
    private final static int FRAGMENT_NUMS = 6;

    private int getCurrentFragmentIndex() {
        Fragment fragment;
        for (int i = 0; i < FRAGMENT_NUMS; i++) {
            Verify.verifyNotNull(FRAGMENT_TAGS[i]);
            fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAGS[i]);
            if (fragment != null && fragment.isVisible()) {
                return i;
            }
        }
        Log.e(TAG, "Cannot find current visible fragment!!!!");
        return FRAGMENT_ERROR;
    }

    private void setTitle() {
        int index = getCurrentFragmentIndex();
        switch (index) {
            case FRAGMENT_ACCOUNT:
                getSupportActionBar().setTitle("계정관리");
                break;
            case FRAGMENT_TIMETABLE:
                getSupportActionBar().setTitle("시간표 설정");
                break;
            case FRAGMENT_DEVELOPER:
                getSupportActionBar().setTitle("개발자 정보");
                break;
            case FRAGMENT_REPORT:
                getSupportActionBar().setTitle("개발자 괴롭히기");
                break;
            case FRAGMENT_LICENSE:
                getSupportActionBar().setTitle("라이센스 정보");
                break;
            case FRAGMENT_TERMS:
                getSupportActionBar().setTitle("약관 보기");
                break;
            default:
                Log.e(TAG, "Fragment error!!!!");
                break;
        }
    }

    private void setAccountFragment() {

    }

    private void setTimetableFragment() {

    }

    private void setDeveloperFragment() {

    }

    private void setReportFragment() {

    }

    private void setLicenseFragment() {

    }

    private void setTermsFragment() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        int type = getIntent().getIntExtra(INTENT_KEY_SETTINGS_TYPE, -1);
        Preconditions.checkArgument(type != -1);

       /* if (position == -1) { // c
            lecture = null;
            setCustomDetailFragment();
        } else {
            lecture = LectureManager.getInstance().getLectures().get(position);
            if (lecture.isCustom()) setCustomDetailFragment();
            else setMainFragment();
        }*/
    }


    /**
     * Called whenever the contents of the back stack change.
     */
    @Override
    public void onBackStackChanged() {
        setTitle();
    }


}
