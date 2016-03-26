package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;

/**
 * Created by makesource on 2016. 3. 26..
 */

public class SignInFragment extends SNUTTBaseFragment {

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    private EditText et_id;
    private EditText et_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signin, container, false);

        et_id = (EditText) rootView.findViewById(R.id.input_id);
        et_password = (EditText) rootView.findViewById(R.id.input_password) ;


        return rootView;
    }

}
