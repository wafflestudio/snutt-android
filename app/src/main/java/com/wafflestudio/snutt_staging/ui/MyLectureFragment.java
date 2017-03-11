package com.wafflestudio.snutt_staging.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.adapter.MyLectureListAdapter;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.model.Lecture;
import com.wafflestudio.snutt_staging.view.DividerItemDecoration;

import java.util.List;

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

    private static final String DIALOG_DETAIL = "상세보기";
    private static final String DIALOG_SYLLABUS = "강의계획서";
    private static final String DIALOG_DELETE = "삭제";

    private RecyclerView recyclerView;
    private MyLectureListAdapter mAdapter;
    private List<Lecture> lectures;

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
        lectures = LectureManager.getInstance().getLectures();
        mAdapter = new MyLectureListAdapter(lectures);
        mAdapter.setOnItemClickListener(new MyLectureListAdapter.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, String.valueOf(position) + "-th item clicked!");
                getMainActivity().startLectureMain(position);
            }
        });
        mAdapter.setOnItemLongClickListener(new MyLectureListAdapter.LongClickListener() {
            @Override
            public void onLongClick(View v, final int position) {
                Log.d(TAG, String.valueOf(position) + "-th item long clicked!");
                final Lecture lecture = lectures.get(position);
                final CharSequence[] items = {DIALOG_DETAIL, DIALOG_SYLLABUS, DIALOG_DELETE};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(lecture.getCourse_title())
                       .setItems(items, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int index){
                                if (items[index].equals(DIALOG_DETAIL)) {
                                    getMainActivity().startLectureMain(position);
                                } else if (items[index].equals(DIALOG_SYLLABUS)) {
                                     Toast.makeText(getContext(), "강의계획서!", Toast.LENGTH_SHORT).show();
                                } else {
                                    LectureManager.getInstance().removeLecture(lecture, null);
                                }
                            }
                       });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
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
