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
import com.wafflestudio.snutt_staging.adapter.LectureDetailAdapter;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.wafflestudio.snutt_staging.model.LectureItem.ViewType.ItemDetail;

/**
 * Created by makesource on 2016. 9. 4..
 */
public class LectureDetailFragment extends SNUTTBaseFragment {
    private static final String TAG = "LECTURE_DETAIL_FRAGMENT";
    private Lecture lecture;
    private ListView detailList;
    private ArrayList<LectureItem> lists;
    private LectureDetailAdapter adapter;
    private boolean editable = false;

    public static LectureDetailFragment newInstance() {
        return new LectureDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        lecture = getLectureMainActivity().lecture;
        if (lecture == null) {
            Log.e(TAG, "lecture refers to null point!!");
            return;
        }

        lists = new ArrayList<>();
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem("강의명", lecture.getCourse_title(), LectureItem.Type.Title));
        lists.add(new LectureItem("교수", lecture.getInstructor(), LectureItem.Type.Instructor));
        lists.add(new LectureItem("색상", lecture.getColor(), LectureItem.Type.Color));
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem("학과", lecture.getDepartment(), LectureItem.Type.Department));
        lists.add(new LectureItem("학년", lecture.getAcademic_year(), "학점", String.valueOf(lecture.getCredit()), LectureItem.Type.AcademicYearCredit));
        lists.add(new LectureItem("분류", lecture.getClassification(), "구분", lecture.getCategory(), LectureItem.Type.ClassificationCategory));
        lists.add(new LectureItem("강좌번호", lecture.getCourse_number(), "분반번호", lecture.getLecture_number(), LectureItem.Type.CourseNumberLectureNumber));
        lists.add(new LectureItem(LectureItem.Type.Header));

        int count = 0;
        for (JsonElement element : lecture.getClass_time_json()) {
            JsonObject jsonObject = element.getAsJsonObject();
            ClassTime classTime = new ClassTime(jsonObject);
            lists.add(new LectureItem(classTime, LectureItem.Type.ClassTime));
            count ++;
        }
        if (count > 0) {
            lists.add(new LectureItem(LectureItem.Type.Header));
        }
        lists.add(new LectureItem(LectureItem.Type.Syllabus));
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem(LectureItem.Type.Remove));
        lists.add(new LectureItem(LectureItem.Type.Header));
        adapter = new LectureDetailAdapter(getActivity(), lists, lecture);
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
        if (item.getViewType() != ItemDetail) return true;
        if (item.getType() != LectureItem.Type.CourseNumberLectureNumber) return true;
        return false;
    }

    private LectureMainActivity getLectureMainActivity() {
        Activity activity = getActivity();
        Preconditions.checkArgument(activity instanceof LectureMainActivity);
        return (LectureMainActivity) activity;
    }

}
