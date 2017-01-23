package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.model.SettingsItem;
import com.wafflestudio.snutt.ui.adapter.SettingsAdapter;
import com.wafflestudio.snutt.ui.adapter.SettingsAdapter2;
import com.wafflestudio.snutt.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SettingsFragment extends SNUTTBaseFragment { /**
 * The fragment argument representing the section number for this
 * fragment.
 */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "SETTINGS_FRAGMENT";
    private List<SettingsItem> lists;
    private SettingsAdapter adapter;
    private SettingsAdapter2 adapter2;

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

        //adapter = new SettingsAdapter(getActivity(), lists);
        adapter2 = new SettingsAdapter2(getActivity(), lists);
        adapter2.setOnItemClickListener(new SettingsAdapter2.TitleViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, String.valueOf(position) + "-th item clicked!");
            }
        });
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
        recyclerView.setAdapter(adapter2);
        return rootView;
    }
}
