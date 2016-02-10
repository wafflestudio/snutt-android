package com.wafflestudio.snutt;


import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.google.common.base.Preconditions;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SNUTTBaseFragment extends Fragment {

    protected SNUTTApplication getApp() {
        return (SNUTTApplication) getActivity().getApplication();
    }
}
