package com.wafflestudio.snutt.ui;

import android.content.Intent;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.model.ClassTime;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.LectureItem;
import com.wafflestudio.snutt.ui.adapter.LectureAdapter;

import java.util.ArrayList;

/**
 * Created by makesource on 2016. 9. 4..
 */
public class LectureDetailTemp extends SNUTTBaseFragment {
    private ListView lectureList;
    private ArrayList<LectureItem> lists;
    private LectureAdapter adapter;
    private boolean editable = false;


    public static LectureDetailTemp newInstance() {
        return new LectureDetailTemp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_lecture_detail_temp, container, false);
        setHasOptionsMenu(true);
        lectureList = (ListView) rootView.findViewById(R.id.lecture_list);

        Intent intent = getActivity().getIntent();
        int position = intent.getIntExtra(INTENT_KEY_LECTURE_POSITION, -1);

        // throws exception when the list position is out of range
        Preconditions.checkPositionIndex(position,  LectureManager.getInstance().getLectures().size());
        Lecture lecture = LectureManager.getInstance().getLectures().get(position);

        lists = new ArrayList<>();
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem("강의명", lecture.getCourse_title(), LectureItem.Type.ItemTitle));
        lists.add(new LectureItem("교수", lecture.getInstructor(), LectureItem.Type.ItemTitle));
        lists.add(new LectureItem("색상", lecture.getColor(), LectureItem.Type.ItemColor));
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem("학과", lecture.getDepartment(), LectureItem.Type.ItemDetail));
        lists.add(new LectureItem("학년", lecture.getAcademic_year(), "학점", String.valueOf(lecture.getCredit()), LectureItem.Type.ItemDetail));
        lists.add(new LectureItem("분류", lecture.getClassification(), "구분", "", LectureItem.Type.ItemDetail));
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

        adapter = new LectureAdapter(getContext(), lists);
        lectureList.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_lecture_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_edit :
                if (editable) {
                    item.setTitle("편집");
                    editable = false;
                    for (LectureItem it : lists) {
                        it.setEditable(false);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    item.setTitle("완료");
                    editable = true;
                    for (LectureItem it : lists) {
                        it.setEditable(true);
                    }
                    adapter.notifyDataSetChanged();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
