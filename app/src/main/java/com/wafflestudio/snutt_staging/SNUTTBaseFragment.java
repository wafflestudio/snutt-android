package com.wafflestudio.snutt_staging;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.wafflestudio.snutt_staging.ui.MainActivity;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SNUTTBaseFragment extends Fragment {

    private static final String TAG = "SNUTT_BASE_FRAGMENT";
    public static final String INTENT_KEY_TABLE_ID = "INTENT_KEY_TABLE_ID";
    public static final String INTENT_KEY_LECTURE_POSITION = "INTENT_KEY_LECTURE_POSITION";

    protected SNUTTApplication getApp() {
        return (SNUTTApplication) getActivity().getApplication();
    }

    public MainActivity getMainActivity() {
        Activity activity = getActivity();
        Preconditions.checkState(activity instanceof MainActivity);
        return (MainActivity) activity;
    }

    public SNUTTBaseActivity getBaseActivity() {
        Activity activity = getActivity();
        Preconditions.checkState(activity instanceof SNUTTBaseActivity);
        return (SNUTTBaseActivity) activity;
    }
}
