package com.wafflestudio.snutt.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.model.ClassTime;
import com.wafflestudio.snutt.model.Color;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.LectureItem;
import com.wafflestudio.snutt.model.Table;
import com.wafflestudio.snutt.ui.adapter.LectureAdapter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 9. 4..
 */
public class LectureDetailTemp extends SNUTTBaseFragment {
    private Lecture lecture;
    private ListView lectureList;
    private ArrayList<LectureItem> lists;
    private LectureAdapter adapter;
    private boolean editable = false;

    public static LectureDetailTemp newInstance() {
        return new LectureDetailTemp();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Intent intent = getActivity().getIntent();
        int position = intent.getIntExtra(INTENT_KEY_LECTURE_POSITION, -1);

        // throws exception when the list position is out of range
        Preconditions.checkPositionIndex(position,  LectureManager.getInstance().getLectures().size());
        lecture = LectureManager.getInstance().getLectures().get(position);

        lists = new ArrayList<>();
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem("강의명", lecture.getCourse_title(), LectureItem.Type.ItemTitle));
        lists.add(new LectureItem("교수", lecture.getInstructor(), LectureItem.Type.ItemTitle));
        lists.add(new LectureItem("색상", lecture.getColor(), LectureItem.Type.ItemColor));
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem("학과", lecture.getDepartment(), LectureItem.Type.ItemDetail));
        lists.add(new LectureItem("학년", lecture.getAcademic_year(), "학점", String.valueOf(lecture.getCredit()), LectureItem.Type.ItemDetail));
        lists.add(new LectureItem("분류", lecture.getClassification(), "구분", lecture.getCategory(), LectureItem.Type.ItemDetail));
        lists.add(new LectureItem("강좌번호", lecture.getCourse_number(), "분반번호", lecture.getLecture_number(), LectureItem.Type.ItemDetail));
        lists.add(new LectureItem(LectureItem.Type.Header));

        int count = 0;
        for (JsonElement element : lecture.getClass_time_json()) {
            JsonObject jsonObject = element.getAsJsonObject();
            ClassTime classTime = new ClassTime(jsonObject);
            lists.add(new LectureItem(classTime, LectureItem.Type.ItemClass));
            count ++;
        }
        if (count > 0) {
            lists.add(new LectureItem(LectureItem.Type.Header));
        }
        lists.add(new LectureItem(LectureItem.Type.ItemButton));

        adapter = new LectureAdapter(getActivity(), lists);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_lecture_detail_temp, container, false);
        lectureList = (ListView) rootView.findViewById(R.id.lecture_list);
        lectureList.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_lecture_detail, menu);
        MenuItem item = menu.getItem(0);
        if (editable) {
            item.setTitle("완료");
        } else {
            item.setTitle("편집");
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_edit :
                if (editable) {
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
                            Toast.makeText(getContext(), "강의 업데이트에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    item.setTitle("완료");
                    editable = true;
                    for (LectureItem it : lists) {
                        if (!isEditable(it)) continue;
                        it.setEditable(true);
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLectureColor(Color color) {
        lists.get(3).setColor(color); // 색상
        adapter.notifyDataSetChanged();
    }

    private boolean isEditable(LectureItem item) {
        if (item.getType() != LectureItem.Type.ItemDetail) return true;
        if (!item.getTitle1().equals("강좌번호")) return true;
        return false;
    }

}
