package com.wafflestudio.snutt;


import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.google.common.base.Preconditions;
import com.wafflestudio.snutt.ui.MainActivity;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SNUTTBaseFragment extends Fragment {

    protected SNUTTApplication getApp() {
        return (SNUTTApplication) getActivity().getApplication();
    }

    public MainActivity getMainActivity() {
        Activity activity = getActivity();
        Preconditions.checkState(activity instanceof MainActivity);
        return (MainActivity) activity;
    }
}
