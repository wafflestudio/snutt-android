package com.wafflestudio.snutt2.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.wafflestudio.snutt2.R;
import com.wafflestudio.snutt2.SNUTTBaseActivity;

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
    private final static String TAG_FRAGMENT_PRIVACY = "TAG_FRAGMENT_PRIVACY";

    private final static String[] FRAGMENT_TAGS = {
            TAG_FRAGMENT_ACCOUNT,
            TAG_FRAGMENT_TIMETABLE,
            TAG_FRAGMENT_DEVELOPER,
            TAG_FRAGMENT_REPORT,
            TAG_FRAGMENT_LICENSE,
            TAG_FRAGMENT_TERMS,
            TAG_FRAGMENT_PRIVACY
    };

    protected final static int FRAGMENT_ERROR = -1;
    protected final static int FRAGMENT_ACCOUNT = 0;
    protected final static int FRAGMENT_TIMETABLE = 1;
    protected final static int FRAGMENT_DEVELOPER = 2;
    protected final static int FRAGMENT_REPORT = 3;
    protected final static int FRAGMENT_LICENSE = 4;
    protected final static int FRAGMENT_TERMS = 5;
    protected final static int FRAGMENT_PRIVACY = 6;
    private final static int FRAGMENT_NUMS = 7;

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
                getSupportActionBar().setTitle("서비스 약관");
                break;
            case FRAGMENT_PRIVACY:
                getSupportActionBar().setTitle("개인정보처리방침");
                break;
            default:
                Log.e(TAG, "Fragment error!!!!");
                break;
        }
    }

    private void setAccountFragment() {
        showFragment(FRAGMENT_ACCOUNT, false);
        getSupportActionBar().setTitle("계정관리");
    }

    private void setTimetableFragment() {
        showFragment(FRAGMENT_TIMETABLE, false);
        getSupportActionBar().setTitle("시간표 설정");
    }

    private void setDeveloperFragment() {
        showFragment(FRAGMENT_DEVELOPER, false);
        getSupportActionBar().setTitle("개발자 정보");
    }

    private void setReportFragment() {
        showFragment(FRAGMENT_REPORT, false);
        getSupportActionBar().setTitle("개발자 괴롭히기");
    }

    private void setLicenseFragment() {
        showFragment(FRAGMENT_LICENSE, false);
        getSupportActionBar().setTitle("라이센스 정보");
    }

    private void setTermsFragment() {
        showFragment(FRAGMENT_TERMS, false);
        getSupportActionBar().setTitle("서비스 약관");
    }

    private void setPrivacyFragment() {
        showFragment(FRAGMENT_PRIVACY, false);
        getSupportActionBar().setTitle("개인정보처리방침");
    }

    private Fragment newFragment(int fragmentIdx) {
        switch (fragmentIdx) {
            case FRAGMENT_ACCOUNT:
                return new AccountFragment();
            case FRAGMENT_TIMETABLE:
                return new TimetableFragment();
            case FRAGMENT_DEVELOPER:
                return new DeveloperFragment();
            case FRAGMENT_REPORT:
                return new ReportFragment();
            case FRAGMENT_LICENSE:
                return new LicenseFragment();
            case FRAGMENT_TERMS:
                return new TermsFragment();
            case FRAGMENT_PRIVACY:
                return new PrivacyFragment();
            default:
                Log.e(TAG, "Fragment index is out of range!!!");
                return null;
        }
    }

    private void showFragment(int fragmentIdx, boolean withBackStackPush) {
        Preconditions.checkArgument(fragmentIdx >= 0);
        Preconditions.checkArgument(fragmentIdx < FRAGMENT_NUMS);

        String fragmentTag = FRAGMENT_TAGS[fragmentIdx];
        Fragment fragment = newFragment(fragmentIdx);

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.settings_main_layout, fragment, fragmentTag);
        if (withBackStackPush) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_settings_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        int type = getIntent().getIntExtra(INTENT_KEY_SETTINGS_TYPE, -1);
        Preconditions.checkArgument(type != -1);
        switch (type) {
            case FRAGMENT_ACCOUNT:
                setAccountFragment();
                break;
            case FRAGMENT_TIMETABLE:
                setTimetableFragment();
                break;
            case FRAGMENT_DEVELOPER:
                setDeveloperFragment();
                break;
            case FRAGMENT_REPORT:
                setReportFragment();
                break;
            case FRAGMENT_LICENSE:
                setLicenseFragment();
                break;
            case FRAGMENT_TERMS:
                setTermsFragment();
                break;
            case FRAGMENT_PRIVACY:
                setPrivacyFragment();
                break;
            default:
                break;
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
