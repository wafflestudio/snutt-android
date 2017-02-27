package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.widget.ListView;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseActivity;
import com.wafflestudio.snutt.model.Color;
import com.wafflestudio.snutt.model.LectureItem;
import com.wafflestudio.snutt.adapter.CustomLectureAdapter;

import java.util.ArrayList;

/**
 * Created by makesource on 2016. 5. 27..
 */
public class LectureCreateActivity extends SNUTTBaseActivity {

    private ListView detailList;
    private ArrayList<LectureItem> lists;
    private CustomLectureAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityList.add(this);
        setContentView(R.layout.activity_lecture_create);
        setTitle("커스텀 강의 추가");
        detailList = (ListView) findViewById(R.id.lecture_detail_list);

        lists = new ArrayList<>();
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem("강의명", "", LectureItem.Type.ItemTitle));
        lists.add(new LectureItem("교수", "", LectureItem.Type.ItemTitle));
        lists.add(new LectureItem("색상", new Color(), LectureItem.Type.ItemColor));
        lists.add(new LectureItem("학점", "0", LectureItem.Type.ItemTitle));
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem(LectureItem.Type.ItemButton));
        adapter = new CustomLectureAdapter(this, lists);

        detailList.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }

}
