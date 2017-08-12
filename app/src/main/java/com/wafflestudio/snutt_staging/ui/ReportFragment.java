package com.wafflestudio.snutt_staging.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.manager.UserManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2017. 1. 24..
 */

public class ReportFragment extends SNUTTBaseFragment {

    private EditText emailText;
    private EditText detailText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_report, container, false);
        setHasOptionsMenu(true);
        emailText = (EditText) rootView.findViewById(R.id.email_editText);
        detailText = (EditText) rootView.findViewById(R.id.detail_editText);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_report, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            if (Strings.isNullOrEmpty(detailText.getText().toString())) {
                Toast.makeText(getApp(), "내용을 입력해 주세요", Toast.LENGTH_SHORT).show();
            } else {
                String email = emailText.getText().toString();
                String detail = detailText.getText().toString();
                UserManager.getInstance().postFeedback(email, detail, new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        Toast.makeText(getApp(), "전송하였습니다", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
                getActivity().finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
