package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.manager.UserManager;

/**
 * Created by makesource on 2016. 3. 26..
 */

public class SignInFragment extends SNUTTBaseFragment implements UserManager.OnUserDataChangedListener {

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    private EditText et_id;
    private EditText et_password;
    private Button sign_in;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signin, container, false);

        et_id = (EditText) rootView.findViewById(R.id.input_id);
        et_password = (EditText) rootView.findViewById(R.id.input_password) ;
        sign_in = (Button) rootView.findViewById(R.id.button_sign_in);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_id.getText().toString();
                String password = et_password.getText().toString();

                UserManager.getInstance().postSignIn(id, password);
            }
        });


        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        UserManager.getInstance().removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        UserManager.getInstance().addListener(this);
    }

    @Override
    public void notifySignIn() {
        Toast.makeText(getContext(), "로그인 성공!", Toast.LENGTH_SHORT).show();
    }
}
