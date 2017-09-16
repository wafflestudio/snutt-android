package com.wafflestudio.snutt_staging.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.manager.UserManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 3. 26..
 */

public class SignInFragment extends SNUTTBaseFragment {

    public static SignInFragment newInstance() {
        return new SignInFragment();
    }

    private static final String TAG = "SIGN_IN_FRAGMENT";
    private EditText idEditText;
    private EditText passwordEditText;
    private Button signinButton;
    private LinearLayout facebookButton;
    private CallbackManager callbackManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
        setTitle("로그인");
        idEditText = (EditText) rootView.findViewById(R.id.input_id);
        passwordEditText = (EditText) rootView.findViewById(R.id.input_password) ;
        signinButton = (Button) rootView.findViewById(R.id.button_sign_in);
        facebookButton = (LinearLayout) rootView.findViewById(R.id.button_facebook);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                hideSoftKeyboard(getView());
                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "로그인", "잠시만 기다려 주세요", true, false);
                UserManager.getInstance().postSignIn(id, password, new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        getBaseActivity().startMain();
                        getBaseActivity().finishAll();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                    }
                });
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(SignInFragment.this, null);
            }
        });

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                String id = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();
                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "로그인", "잠시만 기다려 주세요", true, false);
                UserManager.getInstance().postLoginFacebook(id, token, new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        getBaseActivity().startMain();
                        getBaseActivity().finishAll();
                        progressDialog.dismiss();
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onCancel() {
                // App code
                Log.w(TAG, "Cancel");
            }

            @Override
            public void onError(FacebookException error) {
                // App code
                Log.e(TAG, "Error", error);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setTitle(String title) {
        getActivity().setTitle(title);
    }
}
