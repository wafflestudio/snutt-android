package com.wafflestudio.snutt2;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.common.base.Preconditions;
import com.wafflestudio.snutt2.ui.MainActivity;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SNUTTBaseFragment extends Fragment {

    private Activity mActivity;
    private static final String TAG = "SNUTT_BASE_FRAGMENT";
    public static final String INTENT_KEY_TABLE_ID = "INTENT_KEY_TABLE_ID";
    public static final String INTENT_KEY_LECTURE_POSITION = "INTENT_KEY_LECTURE_POSITION";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    protected SNUTTApplication getApp() {
        return (SNUTTApplication) mActivity.getApplication();
    }

    public MainActivity getMainActivity() {
        Activity activity = mActivity;
        Preconditions.checkState(activity instanceof MainActivity);
        return (MainActivity) activity;
    }

    public SNUTTBaseActivity getBaseActivity() {
        Activity activity = mActivity;
        Preconditions.checkState(activity instanceof SNUTTBaseActivity);
        return (SNUTTBaseActivity) activity;
    }

    protected void hideSoftKeyboard(View view) {
        if (mActivity == null) return;

        InputMethodManager mgr = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
