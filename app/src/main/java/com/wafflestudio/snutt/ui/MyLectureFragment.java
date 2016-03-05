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
import com.wafflestudio.snutt.ui.adapter.MyLectureListAdapter;
import com.wafflestudio.snutt.manager.LectureManager;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class MyLectureFragment extends SNUTTBaseFragment implements LectureManager.OnLectureChangedListener{ /**
 * The fragment argument representing the section number for this
 * fragment.
 */
    private static final String TAG = "MY_LECTURE_FRAGMENT" ;
    private static final String ARG_SECTION_NUMBER = "section_number";

    private RecyclerView recyclerView;
    private MyLectureListAdapter mAdapter;

    public MyLectureFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyLectureFragment newInstance(int sectionNumber) {
        MyLectureFragment fragment = new MyLectureFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_lecture, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_lecture_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApp());
        mAdapter = new MyLectureListAdapter(LectureManager.getInstance().getLectures());
        mAdapter.setOnItemClickListener(new MyLectureListAdapter.ViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, String.valueOf(position) + "-th item clicked!");
                getMainActivity().startLectureDetail(position);
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void notifyLectureChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        LectureManager.getInstance().removeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        LectureManager.getInstance().addListener(this);
    }
}
