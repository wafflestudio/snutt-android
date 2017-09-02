package com.wafflestudio.snutt_staging.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.manager.UserManager;

import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 3. 26..
 */
public class SignUpFragment extends SNUTTBaseFragment {
    private static final String TAG = "SIGN_UP_FRAGMENT";

    private EditText idEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private EditText emailEditText;
    private TextView termsTextView;
    private Button signupButton;
    private LinearLayout facebookButton;
    private CallbackManager callbackManager;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        setTitle("회원가입");
        idEditText = (EditText) rootView.findViewById(R.id.input_id);
        passwordEditText = (EditText) rootView.findViewById(R.id.input_password);
        passwordConfirmEditText = (EditText) rootView.findViewById(R.id.input_password_confirm);
        emailEditText = (EditText) rootView.findViewById(R.id.input_email);
        signupButton = (Button) rootView.findViewById(R.id.button_sign_up);
        facebookButton = (LinearLayout) rootView.findViewById(R.id.button_facebook);
        termsTextView = (TextView) rootView.findViewById(R.id.terms_textview);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = idEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String passwordConfirm = passwordConfirmEditText.getText().toString();
                final String email = emailEditText.getText().toString();

                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(getApp(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserManager.getInstance().postSingUp(id, password, email, new Callback<Map<String, String>>() {
                    @Override
                    public void success(Map<String, String> response, Response response2) {
                        getBaseActivity().startMain();
                        getBaseActivity().finishAll();
                    }
                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(SignUpFragment.this, null);
            }
        });

        termsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getString(R.string.api_server) + getString(R.string.terms);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                String id = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();
                UserManager.getInstance().postLoginFacebook(id, token, new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        getBaseActivity().startMain();
                        getBaseActivity().finishAll();
                    }
                    @Override
                    public void failure(RetrofitError error) {

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
