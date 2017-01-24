package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.model.SettingsItem;
import com.wafflestudio.snutt.ui.adapter.SettingsAdapter;
import com.wafflestudio.snutt.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import static com.wafflestudio.snutt.ui.SettingsMainActivity.FRAGMENT_ACCOUNT;
import static com.wafflestudio.snutt.ui.SettingsMainActivity.FRAGMENT_DEVELOPER;
import static com.wafflestudio.snutt.ui.SettingsMainActivity.FRAGMENT_LICENSE;
import static com.wafflestudio.snutt.ui.SettingsMainActivity.FRAGMENT_REPORT;
import static com.wafflestudio.snutt.ui.SettingsMainActivity.FRAGMENT_TERMS;
import static com.wafflestudio.snutt.ui.SettingsMainActivity.FRAGMENT_TIMETABLE;

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
        lists.add(new SettingsItem("계정관리", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem("시간표 설정", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("버전 정보", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("개발자 정보", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem("개발자 괴롭히기", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("라이센스 정보", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem("약관 보기", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem(SettingsItem.Type.Header));
        lists.add(new SettingsItem("로그아웃", SettingsItem.Type.ItemTitle));
        lists.add(new SettingsItem(SettingsItem.Type.Header));

        adapter = new SettingsAdapter(getActivity(), lists);
        clickListener = new SettingsAdapter.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, String.valueOf(position) + "-th item clicked!");
                switch (position) {
                    case 1: // account setting
                        getMainActivity().startSettingsMain(FRAGMENT_ACCOUNT);
                        break;
                    case 2: // timetable setting
                        getMainActivity().startSettingsMain(FRAGMENT_TIMETABLE);
                        break;
                    case 6: // developer
                        getMainActivity().startSettingsMain(FRAGMENT_DEVELOPER);
                        break;
                    case 7: // bug report
                        getMainActivity().startSettingsMain(FRAGMENT_REPORT);
                        break;
                    case 9: // license
                        getMainActivity().startSettingsMain(FRAGMENT_LICENSE);
                        break;
                    case 10: // terms
                        getMainActivity().startSettingsMain(FRAGMENT_TERMS);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        //ListView listView = (ListView) rootView.findViewById(R.id.settings_list);
        //listView.setAdapter(adapter);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.settings_recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
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
}
