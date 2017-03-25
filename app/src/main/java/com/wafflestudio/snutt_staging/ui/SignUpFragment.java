package com.wafflestudio.snutt_staging.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.manager.UserManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 3. 26..
 */
public class SignUpFragment extends SNUTTBaseFragment {

    private OnSignUpSucceedListener mListener;
    private EditText et_id;
    private EditText et_password;
    private EditText et_email;
    private Button sign_up;

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    public interface OnSignUpSucceedListener {
        public void onSignUpSucceed(String id, String password);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        et_id = (EditText) rootView.findViewById(R.id.input_id);
        et_password = (EditText) rootView.findViewById(R.id.input_password);
        et_email = (EditText) rootView.findViewById(R.id.input_email);
        sign_up = (Button) rootView.findViewById(R.id.button_sign_up);
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = et_id.getText().toString();
                final String password = et_password.getText().toString();
                final String email = et_email.getText().toString();
                UserManager.getInstance().postSingUp(id, password, email, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(getContext(), "회원가입에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                        mListener.onSignUpSucceed(id , password);
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getContext(), "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (OnSignUpSucceedListener) activity;
    }

}
