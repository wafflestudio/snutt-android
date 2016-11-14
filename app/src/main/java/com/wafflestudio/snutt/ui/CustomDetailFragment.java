package com.wafflestudio.snutt.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.model.Color;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.LectureItem;
import com.wafflestudio.snutt.model.Table;
import com.wafflestudio.snutt.ui.adapter.CustomLectureAdapter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 11. 10..
 */
public class CustomDetailFragment extends SNUTTBaseFragment {
    private Lecture lecture;
    private ListView detailList;
    private ArrayList<LectureItem> lists;
    private CustomLectureAdapter adapter;
    private boolean editable = false;
    private boolean add = false;

    public static CustomDetailFragment newInstance() {
        return new CustomDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        lecture = getLectureMainActivity().lecture;
        if (lecture == null) add = true;

        lists = new ArrayList<>();
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem("강의명", add ? "" : lecture.getCourse_title(), LectureItem.Type.ItemTitle));
        lists.add(new LectureItem("교수", add ? "" : lecture.getInstructor(), LectureItem.Type.ItemTitle));
        lists.add(new LectureItem("색상", add ? new Color() : lecture.getColor(), LectureItem.Type.ItemColor));
        lists.add(new LectureItem("학점", add ? "0" : String.valueOf(lecture.getCredit()), LectureItem.Type.ItemTitle));
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem(LectureItem.Type.ItemButton));
        for (LectureItem it : lists) {
            it.setEditable(add);
        }
        adapter = new CustomLectureAdapter(getActivity(), lists);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_lecture_detail, menu);
        MenuItem item = menu.getItem(0);
        if (editable || add) {
            item.setTitle("완료");
        } else {
            item.setTitle("편집");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_lecture_detail, container, false);
        detailList = (ListView) rootView.findViewById(R.id.lecture_detail_list);
        detailList.setAdapter(adapter);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_edit :
                if (add) {
                    adapter.createLecture(new Callback<Table>() {
                        @Override
                        public void success(Table table, Response response) {
                            getLectureMainActivity().finish();
                        }
                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getContext(), "강의 추가를 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (editable) {
                    adapter.updateLecture(lecture, new Callback<Table>() {
                        @Override
                        public void success(Table table, Response response) {
                            item.setTitle("편집");
                            editable = false;
                            for (LectureItem it : lists) {
                                it.setEditable(false);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getContext(), "강의 업데이트에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    item.setTitle("완료");
                    editable = true;
                    for (LectureItem it : lists) {
                        it.setEditable(true);
                    }
                    adapter.notifyDataSetChanged();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private LectureMainActivity getLectureMainActivity() {
        Activity activity = getActivity();
        Preconditions.checkArgument(activity instanceof LectureMainActivity);
        return (LectureMainActivity) activity;
    }

}
