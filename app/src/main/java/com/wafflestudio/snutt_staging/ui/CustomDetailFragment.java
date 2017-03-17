package com.wafflestudio.snutt_staging.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.model.ClassTime;
import com.wafflestudio.snutt_staging.model.Color;
import com.wafflestudio.snutt_staging.model.Lecture;
import com.wafflestudio.snutt_staging.model.LectureItem;
import com.wafflestudio.snutt_staging.model.Table;
import com.wafflestudio.snutt_staging.adapter.CustomLectureAdapter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 11. 10..
 */
public class CustomDetailFragment extends SNUTTBaseFragment {
    private static final String TAG = "CUSTOM_DETAIL_FRAGMENT";

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
        lists.add(new LectureItem("강의명", add ? "" : lecture.getCourse_title(), LectureItem.Type.Title));
        lists.add(new LectureItem("교수", add ? "" : lecture.getInstructor(), LectureItem.Type.Instructor));
        lists.add(new LectureItem("색상", add ? new Color() : lecture.getColor(), LectureItem.Type.Color));
        lists.add(new LectureItem("학점", add ? "0" : String.valueOf(lecture.getCredit()), LectureItem.Type.Credit));
        lists.add(new LectureItem(LectureItem.Type.Header));

        if (!add) {
            for (JsonElement element : lecture.getClass_time_json()) {
                JsonObject jsonObject = element.getAsJsonObject();
                ClassTime classTime = new ClassTime(jsonObject);
                lists.add(new LectureItem(classTime, LectureItem.Type.ClassTime));
            }
            lists.add(new LectureItem(LectureItem.Type.Header));
            lists.add(new LectureItem(LectureItem.Type.RemoveLecture));
            lists.add(new LectureItem(LectureItem.Type.Header));

        } else {
            lists.add(new LectureItem(LectureItem.Type.AddClassTime));
            lists.add(new LectureItem(LectureItem.Type.Header));
        }

        for (LectureItem it : lists) {
            it.setEditable(add);
        }
        adapter = new CustomLectureAdapter(getActivity(), lists, lecture);
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
                            getLectureMainActivity().finish();
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

    public void setLectureColor(Color color) {
        getColorItem().setColor(color); // 색상
        adapter.notifyDataSetChanged();
    }

    public LectureItem getColorItem() {
        for (LectureItem item : lists) {
            if (item.getType() == LectureItem.Type.Color) return item;
        }
        Log.e(TAG, "can't find color item");
        return null;
    }


    private LectureMainActivity getLectureMainActivity() {
        Activity activity = getActivity();
        Preconditions.checkArgument(activity instanceof LectureMainActivity);
        return (LectureMainActivity) activity;
    }

}
