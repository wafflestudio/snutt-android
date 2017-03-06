package com.wafflestudio.snutt_staging.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseActivity;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.model.Color;
import com.wafflestudio.snutt_staging.model.Lecture;

/**
 * Created by makesource on 2016. 3. 1..
 */
public class LectureMainActivity extends SNUTTBaseActivity
        implements FragmentManager.OnBackStackChangedListener, ColorPickerFragment.ColorChangedListener {

    private static final String TAG = "LECTURE_MAIN_ACTIVITY" ;

    public final static String TAG_FRAGMENT_LECTURE_DETAIL = "TAG_FRAGMENT_LECTURE_DETAIL";
    public final static String TAG_FRAGMENT_COLOR_PICKER = "TAG_FRAGMENT_COLOR_PICKER";
    public final static String TAG_FRAGMENT_CUSTOM_DETAIL = "TAG_FRAGMENT_CUSTOM_DETAIL";
    //public final static String TAG_FRAGMENT_TEST = "TAG_FRAGMENT_TEST";


    private final static String[] FRAGMENT_TAGS = {
            TAG_FRAGMENT_LECTURE_DETAIL,
            TAG_FRAGMENT_COLOR_PICKER,
            TAG_FRAGMENT_CUSTOM_DETAIL
            //TAG_FRAGMENT_TEST
    };

    public final static int FRAGMENT_ERROR = -1;
    public final static int FRAGMENT_LECTURE_DETAIL = 0;
    public final static int FRAGMENT_COLOR_PICKER = 1;
    public final static int FRAGMENT_CUSTOM_DETAIL = 2;
    //public final static int FRAGMENT_TEST = 2;
    public final static int FRAGMENT_ROOM_NUM = 3;  // Number of fragments

    public Lecture lecture = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_lecture_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        int position = getIntent().getIntExtra(INTENT_KEY_LECTURE_POSITION, -1);
        if (position == -1) { // create custom lecture
            lecture = null;
            setCustomDetailFragment();
        } else {
            lecture = LectureManager.getInstance().getLectures().get(position);
            if (lecture.isCustom()) setCustomDetailFragment();
            else setMainFragment();
        }
    }

    private void setMainFragment() {
        Fragment fragment = LectureDetailFragment.newInstance();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_lecture_main, fragment, TAG_FRAGMENT_LECTURE_DETAIL);
        transaction.commit();
        getSupportActionBar().setTitle("강의 상세 보기");
    }

    public void setColorPickerFragment() {
        showFragment(FRAGMENT_COLOR_PICKER, true);
    }

    public void setCustomDetailFragment() {
        Fragment fragment = CustomDetailFragment.newInstance();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_lecture_main, fragment, TAG_FRAGMENT_CUSTOM_DETAIL);
        transaction.commit();
        if (lecture == null) getSupportActionBar().setTitle("커스텀 강의 추가");
        else getSupportActionBar().setTitle("강의 상세 보기");
    }


    /**
     * Called whenever the contents of the back stack change.
     */
    @Override
    public void onBackStackChanged() {
        setTitle();
    }

    //public

    private int getCurrentFragmentIndex() {
        Fragment fragment;
        for (int i = 0; i < FRAGMENT_ROOM_NUM; i++) {
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
            case FRAGMENT_LECTURE_DETAIL:
                getSupportActionBar().setTitle("강의 상세 보기");
                break;
            case FRAGMENT_COLOR_PICKER:
                getSupportActionBar().setTitle("강의 색상 변경");
                break;
            case FRAGMENT_CUSTOM_DETAIL:
                if (lecture == null) getSupportActionBar().setTitle("커스텀 강의 추가");
                else getSupportActionBar().setTitle("강의 상세 보기");
                break;
            default:
                Log.e(TAG, "Fragment error!!!!");
                break;
        }
    }

    private Fragment newFragment(int fragmentIdx) {
        switch (fragmentIdx) {
            case FRAGMENT_LECTURE_DETAIL:
                return LectureDetailFragment.newInstance();
            case FRAGMENT_COLOR_PICKER:
                return ColorPickerFragment.newInstance();
            case FRAGMENT_CUSTOM_DETAIL:
                return CustomDetailFragment.newInstance();
            default:
                Log.e(TAG, "Fragment index is out of range!!!");
                return null;
        }
    }

    private void showFragment(int fragmentIdx, boolean withBackStackPush) {
        Preconditions.checkArgument(fragmentIdx >= 0);
        Preconditions.checkArgument(fragmentIdx < FRAGMENT_ROOM_NUM);

        String fragmentTag = FRAGMENT_TAGS[fragmentIdx];
        Fragment fragment = newFragment(fragmentIdx);

        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(
                R.anim.layout_rightin, R.anim.layout_leftout,
                R.anim.layout_leftin, R.anim.layout_rightout);
        transaction.replace(R.id.activity_lecture_main, fragment, fragmentTag);
        if (withBackStackPush) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lecture_detail, menu);
        return true;
    }*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorChanged(Color color) {
        if (lecture == null || lecture.isCustom()) {
            CustomDetailFragment fragment = (CustomDetailFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_CUSTOM_DETAIL);
            fragment.setLectureColor(color);
        } else {
            LectureDetailFragment fragment = (LectureDetailFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_LECTURE_DETAIL);
            fragment.setLectureColor(color);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
