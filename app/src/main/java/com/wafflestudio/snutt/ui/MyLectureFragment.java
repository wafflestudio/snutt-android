package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.ui.adapter.MyLectureListAdapter;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.view.DividerItemDecoration;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class MyLectureFragment extends SNUTTBaseFragment implements LectureManager.OnLectureChangedListener{
    /**
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
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_lecture_recyclerView);
        mAdapter = new MyLectureListAdapter(LectureManager.getInstance().getLectures());
        mAdapter.setOnItemClickListener(new MyLectureListAdapter.ViewHolder.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, String.valueOf(position) + "-th item clicked!");
                getMainActivity().startLectureMain(position);
            }
        });
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void notifyLectureChanged() {
        Log.d (TAG, "notify lecture changed called");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_my_lecture, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            //getMainActivity().startTableList();
            Toast.makeText(getContext(), "custom lecture add clicked!!", Toast.LENGTH_SHORT).show();
            getMainActivity().startLectureMain();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        if (mAdapter != null) {
            // 강의 색상 변경시 fragment 이동 발생!

            mAdapter.notifyDataSetChanged();
        }
    }
}
