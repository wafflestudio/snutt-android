package com.wafflestudio.snutt_staging.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseActivity;
import com.wafflestudio.snutt_staging.adapter.CustomLectureAdapter;
import com.wafflestudio.snutt_staging.adapter.LectureDetailAdapter;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.model.Color;
import com.wafflestudio.snutt_staging.model.Lecture;
import com.wafflestudio.snutt_staging.model.LectureItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_lecture_main);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        int position = getIntent().getIntExtra(INTENT_KEY_LECTURE_POSITION, -1);
        if (position == -1) { // create custom lecture
            LectureManager.getInstance().setCurrentLecture(null);
            setCustomDetailFragment();
        } else {
            Lecture lecture = LectureManager.getInstance().getLectures().get(position);
            LectureManager.getInstance().setCurrentLecture(lecture);
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

    public void setColorPickerFragment(LectureItem item) {
        Bundle bundle = new Bundle();
        bundle.putInt("index", item.getColorIndex());
        showFragment(FRAGMENT_COLOR_PICKER, true, bundle);
    }

    public void setCustomDetailFragment() {
        Fragment fragment = CustomDetailFragment.newInstance();
        final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.activity_lecture_main, fragment, TAG_FRAGMENT_CUSTOM_DETAIL);
        transaction.commit();

        Lecture lecture = LectureManager.getInstance().getCurrentLecture();
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
        Lecture lecture = LectureManager.getInstance().getCurrentLecture();
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

    private void showFragment(int fragmentIdx, boolean withBackStackPush, Bundle bundle) {
        Preconditions.checkArgument(fragmentIdx >= 0);
        Preconditions.checkArgument(fragmentIdx < FRAGMENT_ROOM_NUM);

        String fragmentTag = FRAGMENT_TAGS[fragmentIdx];
        Fragment fragment = newFragment(fragmentIdx);
        fragment.setArguments(bundle);

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
    public void onBackPressed() {
        Log.d(TAG, "on back pressed called");
        int index = getCurrentFragmentIndex();
        if (index == FRAGMENT_LECTURE_DETAIL) {
            final LectureDetailFragment fragment = (LectureDetailFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAGS[index]);
            if (fragment.getEditable()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fragment.refreshFragment();
                        dialog.dismiss();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setTitle("편집을 취소하시겠습니까?");
                alert.show();
                return;
            }
        } else if (index == FRAGMENT_CUSTOM_DETAIL) {
            final CustomDetailFragment fragment = (CustomDetailFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAGS[index]);
            if (fragment.getEditable()) {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fragment.refreshFragment();
                        dialog.dismiss();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setTitle("편집을 취소하시겠습니까?");
                alert.show();
                return;
            }
        }
        super.onBackPressed();

    }

    @Override
    public void onColorChanged(int index, Color color) {
        Lecture lecture = LectureManager.getInstance().getCurrentLecture();
        if (lecture == null || lecture.isCustom()) {
            CustomDetailFragment fragment = (CustomDetailFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_CUSTOM_DETAIL);
            fragment.setLectureColor(index, color);
        } else {
            LectureDetailFragment fragment = (LectureDetailFragment) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_LECTURE_DETAIL);
            fragment.setLectureColor(index, color);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }
}
