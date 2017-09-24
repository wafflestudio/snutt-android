package com.wafflestudio.snutt_staging.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.GoogleApiAvailability;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.manager.UserManager;
import com.wafflestudio.snutt_staging.model.SettingsItem;
import com.wafflestudio.snutt_staging.model.Version;
import com.wafflestudio.snutt_staging.adapter.SettingsAdapter;

import java.util.ArrayList;
import java.util.List;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.wafflestudio.snutt_staging.ui.SettingsMainActivity.FRAGMENT_ACCOUNT;
import static com.wafflestudio.snutt_staging.ui.SettingsMainActivity.FRAGMENT_DEVELOPER;
import static com.wafflestudio.snutt_staging.ui.SettingsMainActivity.FRAGMENT_LICENSE;
import static com.wafflestudio.snutt_staging.ui.SettingsMainActivity.FRAGMENT_PRIVACY;
import static com.wafflestudio.snutt_staging.ui.SettingsMainActivity.FRAGMENT_REPORT;
import static com.wafflestudio.snutt_staging.ui.SettingsMainActivity.FRAGMENT_TERMS;
import static com.wafflestudio.snutt_staging.ui.SettingsMainActivity.FRAGMENT_TIMETABLE;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SettingsFragment extends SNUTTBaseFragment {
    /**
      * The fragment argument representing the section number for this
      * fragment.
      */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "SETTINGS_FRAGMENT";
    private List<SettingsItem> lists;
    private SettingsAdapter adapter;
    private SettingsAdapter.ClickListener clickListener;

    public SettingsFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SettingsFragment newInstance(int sectionNumber) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lists = new ArrayList<>();
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("계정관리", SettingsItem.Type.Account));
        lists.add(new SettingsItem("시간표 설정",  SettingsItem.Type.Timetable));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("버전 정보", SettingsItem.Type.Version));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("개발자 정보", SettingsItem.Type.Developer));
        lists.add(new SettingsItem("개발자 괴롭히기", SettingsItem.Type.BugReport));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("라이센스 정보", SettingsItem.Type.License));
        lists.add(new SettingsItem("서비스 약관", SettingsItem.Type.Terms));
        lists.add(new SettingsItem("개인정보처리방침", SettingsItem.Type.Private));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("로그아웃", SettingsItem.Type.Logout));
        //lists.add(new SettingsItem(SettingsItem.Type.Header));

        adapter = new SettingsAdapter(lists);
        clickListener = new SettingsAdapter.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, String.valueOf(position) + "-th item clicked!");
                SettingsItem.Type type = lists.get(position).getType();
                switch (type) {
                    case Account: // account setting
                        getMainActivity().startSettingsMain(FRAGMENT_ACCOUNT);
                        break;
                    case Timetable: // timetable setting
                        getMainActivity().startSettingsMain(FRAGMENT_TIMETABLE);
                        break;
                    case Developer: // developer
                        getMainActivity().startSettingsMain(FRAGMENT_DEVELOPER);
                        break;
                    case BugReport: // bug report
                        getMainActivity().startSettingsMain(FRAGMENT_REPORT);
                        break;
                    case License: // license
                        showLicenseDialog();
                        break;
                    case Terms: // terms
                        getMainActivity().startSettingsMain(FRAGMENT_TERMS);
                        break;
                    case Private: // private
                        getMainActivity().startSettingsMain(FRAGMENT_PRIVACY);
                        break;
                    case Logout: // logout
                        performLogout();
                        break;
                    default:
                        break;
                }
            }
        };

        /*
        // 서버에서 version name 가져옴
        UserManager.getInstance().getAppVersion(new Callback<Version>() {
            @Override
            public void success(Version version, Response response) {
                updateVersion(version.getVersion());
            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
        */
        try {
            String packageName = getApp().getPackageName();
            String appVersion = getApp().getPackageManager().getPackageInfo(packageName, 0).versionName;
            updateVersion(appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            updateVersion("2.0.0");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        //ListView listView = (ListView) rootView.findViewById(R.id.settings_list);
        //listView.setAdapter(adapter);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.settings_recyclerView);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "on resume called!");
        super.onResume();
        adapter.setOnItemClickListener(clickListener);
    }

    private void updateVersion(String version) {
        int position = -1;
        for (int i = 0;i < lists.size();i ++) {
            if (lists.get(i).getType() == SettingsItem.Type.Version) position = i;
        }
        if (position == -1) {
            Log.e(TAG, "Version item does not exists!");
            return;
        }
        lists.get(position).setDetail(version);
        adapter.notifyItemChanged(position);
    }

    private void performLogout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("로그아웃");
        alert.setMessage("로그아웃 하시겠습니까?");
        alert.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final ProgressDialog progressDialog = ProgressDialog.show(getContext(), "로그아웃", "잠시만 기다려 주세요", true, false);
                UserManager.getInstance().deleteFirebaseToken(new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        UserManager.getInstance().performLogout();
                        getMainActivity().startIntro();
                        getMainActivity().finishAll();
                        progressDialog.dismiss();
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApp(), "로그아웃에 실패하였습니다.",Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
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

    private void showLicenseDialog() {
        new LicensesDialog.Builder(getContext())
                .setNotices(R.raw.notices)
                .setIncludeOwnLicense(true)
                .build()
                .show();
    }
}
