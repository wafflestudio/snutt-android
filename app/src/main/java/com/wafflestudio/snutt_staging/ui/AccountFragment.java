package com.wafflestudio.snutt_staging.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.common.base.Strings;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseActivity;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.SNUTTUtils;
import com.wafflestudio.snutt_staging.manager.UserManager;
import com.wafflestudio.snutt_staging.model.Facebook;
import com.wafflestudio.snutt_staging.model.SettingsItem;
import com.wafflestudio.snutt_staging.model.User;
import com.wafflestudio.snutt_staging.adapter.SettingsAdapter;
import com.wafflestudio.snutt_staging.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2017. 1. 24..
 */

public class AccountFragment extends SNUTTBaseFragment {
    private static final String TAG = "ACCOUNT_FRAGMENT";
    private List<SettingsItem> lists;
    private SettingsAdapter adapter;
    private LayoutInflater inflater;
    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerFacebookCallback();

        lists = new ArrayList<>();
        adapter = new SettingsAdapter(lists);
        adapter.setOnItemClickListener(new SettingsAdapter.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, String.valueOf(position) + "-th item clicked!");
                SettingsItem.Type type = lists.get(position).getType();
                switch (type) {
                    case ChangePassword:
                        performChangePassword();
                        break;

                    case ChangeEmail:
                        performChangeEmail();
                        break;

                    case AddIdPassword:
                        performAddIdPassword();
                        break;

                    case LinkFacebook:
                        LoginManager.getInstance().logInWithReadPermissions(AccountFragment.this, null);
                        break;

                    case DeleteFacebook:
                        performDeleteFacebook();
                        break;

                    case Leave:
                        performLeave();
                        break;

                    default: {
                        break;
                    }
                }
            }
        });

        addSettingsList(UserManager.getInstance().getUser());
        adapter.notifyDataSetChanged();
        UserManager.getInstance().getUserInfo(new Callback<User>() {
            @Override
            public void success(final User user, Response response) {
                addSettingsList(user);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.account_recyclerView);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        this.inflater = inflater;
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void performChangePassword() {
        final View layout = inflater.inflate(R.layout.dialog_change_password, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("비밀번호 변경");
        alert.setView(layout);
        alert.setPositiveButton("변경", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing in here. because we override this button listener later
            }
        }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = alert.create();
        dialog.show();
        // change default button handler after dialog show.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassword = ((EditText) layout.findViewById(R.id.now_password)).getText().toString();
                String newPassword = ((EditText) layout.findViewById(R.id.new_password)).getText().toString();
                String newPasswordConfirm = ((EditText) layout.findViewById(R.id.new_password_confirm)).getText().toString();

                if (!newPassword.equals(newPasswordConfirm)) {
                    Toast.makeText(getContext(), "새 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    UserManager.getInstance().putUserPassword(oldPassword, newPassword, new Callback() {
                        @Override
                        public void success(Object o, Response response) {
                            Toast.makeText(getContext(), "비밀번호를 변경하였습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                }
            }
        });
    }

    private void performChangeEmail() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout2 = inflater.inflate(R.layout.dialog_change_email, null);
        AlertDialog.Builder alert2 = new AlertDialog.Builder(getContext());
        alert2.setTitle("이메일 변경");
        alert2.setView(layout2);
        alert2.setPositiveButton("변경", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing in here. because we override this button listener later
            }
        }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog2 = alert2.create();
        dialog2.show();
        dialog2.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = ((EditText) layout2.findViewById(R.id.email)).getText().toString();
                if (!Strings.isNullOrEmpty(email)) {
                    UserManager.getInstance().putUserInfo(email, new Callback() {
                        @Override
                        public void success(Object o, Response response) {
                            Toast.makeText(getContext(), "이메일 변경에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                            getEmailItem().setDetail(email);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getContext(), "이메일 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialog2.dismiss();
                } else {
                    Toast.makeText(getContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performAddIdPassword() {
        final View layout = inflater.inflate(R.layout.dialog_add_id, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("아이디 비빌번호 추가");
        alert.setView(layout);
        alert.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing in here. because we override this button listener later
            }
        }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = alert.create();
        dialog.show();
        // change default button handler after dialog show.
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = ((EditText) layout.findViewById(R.id.id)).getText().toString();
                final String password = ((EditText) layout.findViewById(R.id.password)).getText().toString();
                final String passwordConfirm = ((EditText) layout.findViewById(R.id.password_confirm)).getText().toString();

                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                } else {
                    UserManager.getInstance().postUserPassword(id, password, new Callback() {
                        @Override
                        public void success(Object o, Response response) {
                            Toast.makeText(getContext(), "아이디를 추가하였습니다", Toast.LENGTH_SHORT).show();
                            updateNewId(id);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                        }
                    });
                    dialog.dismiss();
                }
            }
        });
    }

    private void performDeleteFacebook() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("페이스북 연동 끊기");
        alert.setMessage("페이스북 연동을 끊겠습니까?");
        alert.setPositiveButton("끊기", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                UserManager.getInstance().deleteUserFacebook(new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        Toast.makeText(getContext(), "페이스북 연동이 끊어졌습니다", Toast.LENGTH_SHORT).show();
                        LoginManager.getInstance().logOut();
                        updateDeleteFacebook();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void performLeave() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("회원탈퇴");
        alert.setMessage("SNUTT 회원 탈퇴를 하겠습니까?");
        alert.setPositiveButton("회원탈퇴", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                UserManager.getInstance().deleteFirebaseToken(new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        UserManager.getInstance().deleteUserAccount(new Callback() {
                            @Override
                            public void success(Object o, Response response) {
                                UserManager.getInstance().performLogout();
                                getSNUTTBaseActivity().startIntro();
                                getSNUTTBaseActivity().finishAll();
                            }
                            @Override
                            public void failure(RetrofitError error) {
                                Toast.makeText(getContext(), "회원탈퇴에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getContext(), "회원탈퇴에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private void updateNewId(String id) {
        int position = -1;
        for (int i = 0;i < lists.size(); i++) {
            if (lists.get(i).getType() == SettingsItem.Type.AddIdPassword) position = i;
        }
        if (position == -1) {
            Log.e(TAG, "Add id & password item not exists!!");
            return;
        }
        lists.remove(position);
        adapter.notifyItemRemoved(position);
        lists.add(position, new SettingsItem("비밀번호 변경", SettingsItem.Type.ChangePassword));
        adapter.notifyItemInserted(position);
        lists.add(position, new SettingsItem("아이디", id, SettingsItem.Type.Id));
        adapter.notifyItemInserted(position);
    }

    private void updateLinkFacebook() {
        int position = -1;
        for (int i = 0;i < lists.size(); i++) {
            if (lists.get(i).getType() == SettingsItem.Type.LinkFacebook) position = i;
        }
        if (position == -1) {
            Log.e(TAG, "link facebook item not exists!!");
            return;
        }
        lists.remove(position);
        adapter.notifyItemRemoved(position);
        lists.add(position, new SettingsItem("페이스북 연동 취소", SettingsItem.Type.DeleteFacebook));
        adapter.notifyItemInserted(position);
        lists.add(position, new SettingsItem("페이스북 이름", "", SettingsItem.Type.FacebookName));
        adapter.notifyItemInserted(position);

        final int pos = position;
        UserManager.getInstance().getUserFacebook(new Callback<Facebook>() {
            @Override
            public void success(Facebook facebook, Response response) {
                getFacebookNameItem().setDetail(facebook.getName());
                adapter.notifyItemChanged(pos);
            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void updateDeleteFacebook() {
        int position = -1;
        for (int i = 0;i < lists.size(); i++) {
            if (lists.get(i).getType() == SettingsItem.Type.FacebookName) position = i;
        }
        if (position == -1) {
            Log.e(TAG, "facebook name item not exists!!");
            return;
        }
        lists.remove(position);
        adapter.notifyItemRemoved(position);
        lists.remove(position);
        adapter.notifyItemRemoved(position);
        lists.add(position, new SettingsItem("페이스북 연동", SettingsItem.Type.LinkFacebook));
        adapter.notifyItemInserted(position);
    }

    private SettingsItem getFacebookNameItem() {
        for (SettingsItem item : lists) {
            if (item.getType() == SettingsItem.Type.FacebookName) return item;
        }
        Log.e(TAG, "facebook name row does not exists!!");
        return null;
    }

    private SettingsItem getEmailItem() {
        for (SettingsItem item : lists) {
            if (item.getType() == SettingsItem.Type.Email) return item;
        }
        Log.e(TAG, "email row does not exists!!");
        return null;
    }

    private void registerFacebookCallback() {
        Log.d(TAG, "register facebook callback called.");
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                String id = loginResult.getAccessToken().getUserId();
                String token = loginResult.getAccessToken().getToken();
                Log.i(TAG, "User ID: " + loginResult.getAccessToken().getUserId());
                Log.i(TAG, "Auth Token: " + loginResult.getAccessToken().getToken());
                UserManager.getInstance().postUserFacebook(id, token, new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        updateLinkFacebook();
                    }
                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
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
    }

    private void addSettingsList(User user) {
        lists.clear();
        if (Strings.isNullOrEmpty(user.getLocal_id())) {
            //lists.add(new SettingsItem(SettingsItem.Type.Header));
            lists.add(new SettingsItem("아이디 비번 추가", user.getLocal_id(), SettingsItem.Type.AddIdPassword));
            lists.add(new SettingsItem(SettingsItem.Type.Header));
            if (Strings.isNullOrEmpty(user.getFb_name())) { // 연동 x
                lists.add(new SettingsItem("페이스북 연동", user.getFb_name(), SettingsItem.Type.LinkFacebook));
            } else {
                lists.add(new SettingsItem("페이스북 이름", user.getFb_name(), SettingsItem.Type.FacebookName));
                lists.add(new SettingsItem("페이스북 연동 취소", SettingsItem.Type.DeleteFacebook));
            }
            lists.add(new SettingsItem(SettingsItem.Type.Header));
            lists.add(new SettingsItem("이메일", user.getEmail(), SettingsItem.Type.Email));
            lists.add(new SettingsItem("이메일 변경", SettingsItem.Type.ChangeEmail));
            lists.add(new SettingsItem(SettingsItem.Type.Header));
            lists.add(new SettingsItem("회원탈퇴", SettingsItem.Type.Leave));
            //lists.add(new SettingsItem(SettingsItem.Type.Header));
        } else {
            //lists.add(new SettingsItem(SettingsItem.Type.Header));
            lists.add(new SettingsItem("아이디", user.getLocal_id(), SettingsItem.Type.Id));
            lists.add(new SettingsItem("비밀번호 변경", SettingsItem.Type.ChangePassword));
            lists.add(new SettingsItem(SettingsItem.Type.Header));
            if (Strings.isNullOrEmpty(user.getFb_name())) { // 연동 x
                lists.add(new SettingsItem("페이스북 연동", SettingsItem.Type.LinkFacebook));
            } else {
                lists.add(new SettingsItem("페이스북 이름", user.getFb_name(), SettingsItem.Type.FacebookName));
                lists.add(new SettingsItem("페이스북 연동 취소", SettingsItem.Type.DeleteFacebook));
            }
            lists.add(new SettingsItem(SettingsItem.Type.Header));
            lists.add(new SettingsItem("이메일", user.getEmail(), SettingsItem.Type.Email));
            lists.add(new SettingsItem("이메일 변경", SettingsItem.Type.ChangeEmail));
            lists.add(new SettingsItem(SettingsItem.Type.Header));
            lists.add(new SettingsItem("회원탈퇴", SettingsItem.Type.Leave));
            //lists.add(new SettingsItem(SettingsItem.Type.Header));
        }
    }

    private SNUTTBaseActivity getSNUTTBaseActivity() {
        return (SNUTTBaseActivity) getActivity();
    }
}
