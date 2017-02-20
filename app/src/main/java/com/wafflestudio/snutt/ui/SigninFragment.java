package com.wafflestudio.snutt.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.manager.UserManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by makesource on 2016. 3. 26..
 */

public class SignInFragment extends SNUTTBaseFragment implements UserManager.OnUserDataChangedListener {

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    private static final String TAG = "SIGN_IN_FRAGMENT";
    private EditText et_id;
    private EditText et_password;
    private Button sign_in;

    private CallbackManager callbackManager;
    private LoginButton facebookLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
        et_id = (EditText) rootView.findViewById(R.id.input_id);
        et_password = (EditText) rootView.findViewById(R.id.input_password) ;
        sign_in = (Button) rootView.findViewById(R.id.button_sign_in);
        facebookLogin = (LoginButton) rootView.findViewById(R.id.login_button);

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = et_id.getText().toString();
                String password = et_password.getText().toString();

                UserManager.getInstance().postSignIn(id, password);
            }
        });

        callbackManager = CallbackManager.Factory.create();
        facebookLogin.setFragment(this);
        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                String id = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();
                UserManager.getInstance().postLoginFacebook(id, token, null);
                Log.i(TAG, "User ID: " + loginResult.getAccessToken().getUserId());
                Log.i(TAG, "Auth Token: " + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel()
            {
                // App code
                Log.w(TAG, "Cancel");
            }

            @Override
            public void onError(FacebookException exception)
            {
                // App code
                Log.e(TAG, "Error", exception);
            }
        });

        Log.d(TAG, "on create view finished");
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
    public void notifySignIn(boolean code) {
        if (code) {
            Toast.makeText(getContext(), "로그인 성공!", Toast.LENGTH_SHORT).show();
            getBaseActivity().startMain();
            getActivity().finish();
        } else {
            Toast.makeText(getContext(), "로그인 실패..", Toast.LENGTH_SHORT).show();
        }
    }

    public void setInfo(String id, String password) {
        Log.d(TAG, "set info method called");
        et_id.setText(id);
        et_password.setText(password);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
