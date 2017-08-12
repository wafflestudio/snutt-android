package com.wafflestudio.snutt_staging.ui;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.adapter.LectureDetailAdapter;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.model.ClassTime;
import com.wafflestudio.snutt_staging.model.Color;
import com.wafflestudio.snutt_staging.model.Lecture;
import com.wafflestudio.snutt_staging.model.LectureItem;
import com.wafflestudio.snutt_staging.model.Table;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 9. 4..
 */
public class LectureDetailFragment extends SNUTTBaseFragment {
    private static final String TAG = "LECTURE_DETAIL_FRAGMENT";
    private RecyclerView detailView;
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
        Lecture lecture = LectureManager.getInstance().getCurrentLecture();
        if (lecture == null) {
            Log.e(TAG, "lecture refers to null point!!");
            return;
        }
        lists = new ArrayList<>();
        attachLectureDetailList(lecture);
        adapter = new LectureDetailAdapter(getLectureMainActivity(), this, lists);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_lecture_detail, container, false);
        detailView = (RecyclerView) rootView.findViewById(R.id.lecture_detail_view);
        detailView.setAdapter(adapter);
        detailView.setLayoutManager(new LinearLayoutManager(getContext()));
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        Log.d(TAG, "on create options menu called");
        inflater.inflate(R.menu.menu_lecture_detail, menu);
        MenuItem item = menu.getItem(0);
        if (editable) {
            item.setTitle("완료");
        } else {
            item.setTitle("편집");
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        Log.d(TAG, "on prepare options menu called");

        MenuItem item = menu.getItem(0);
        if (editable) {
            item.setTitle("완료");
        } else {
            item.setTitle("편집");
        }
    }

    @Override
    public synchronized boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_edit :
                if (editable) {
                    item.setEnabled(false);
                    adapter.updateLecture(LectureManager.getInstance().getCurrentLecture(), new Callback<Table>() {
                        @Override
                        public void success(Table table, Response response) {
                            item.setTitle("편집");
                            item.setEnabled(true);
                            setNormalMode();
                        }
                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getApp(), "강의 업데이트에 실패하였습니다", Toast.LENGTH_SHORT).show();
                            refreshFragment();
                        }
                    });
                } else {
                    item.setTitle("완료");
                    setEditMode();
                }
                break;
            case R.id.home:
                if (editable) {
                    refreshFragment();
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLectureColor(int index, Color color) {
        if (index > 0) {
            getColorItem().setColorIndex(index);
        } else {
            getColorItem().setColor(color); // 색상
        }
        adapter.notifyDataSetChanged();
    }

    public LectureItem getColorItem() {
        for (LectureItem item : lists) {
            if (item.getType() == LectureItem.Type.Color) return item;
        }
        Log.e(TAG, "can't find color item");
        return null;
    }

    public void refreshFragment() {
        editable = false;
        ActivityCompat.invalidateOptionsMenu(getActivity());

        lists.clear();
        attachLectureDetailList(LectureManager.getInstance().getCurrentLecture());
        adapter.notifyDataSetChanged();
    }

    public boolean getEditable() {
        return editable;
    }

    private LectureMainActivity getLectureMainActivity() {
        Activity activity = getActivity();
        Preconditions.checkArgument(activity instanceof LectureMainActivity);
        return (LectureMainActivity) activity;
    }

    private void setNormalMode() {
        hideSoftKeyboard(getView());
        editable = false;
        for (int i = 0;i < lists.size();i ++) {
            LectureItem it = lists.get(i);
            it.setEditable(false);
            adapter.notifyItemChanged(i);
        }

        int pos = getAddClassTimeItemPosition();
        lists.remove(pos);
        adapter.notifyItemRemoved(pos);

        lists.add(pos, new LectureItem(LectureItem.Type.Margin, false));
        adapter.notifyItemInserted(pos);
        lists.add(pos + 1, new LectureItem(LectureItem.Type.LongHeader, false));
        adapter.notifyItemInserted(pos + 1);
        lists.add(pos + 2, new LectureItem(LectureItem.Type.Syllabus, false));
        adapter.notifyItemInserted(pos + 2);

        // change button
        pos = getResetItemPosition();
        lists.remove(pos);
        lists.add(pos, new LectureItem(LectureItem.Type.RemoveLecture, false));
        adapter.notifyItemChanged(pos);
    }

    private void setEditMode() {
        editable = true;
        for (int i = 0;i < lists.size();i ++) {
            LectureItem it = lists.get(i);
            it.setEditable(true);
            adapter.notifyItemChanged(i);
        }

        int syllabusPosition = getSyllabusItemPosition();
        // remove syllabus
        lists.remove(syllabusPosition);
        adapter.notifyItemRemoved(syllabusPosition);
        // remove long header
        lists.remove(syllabusPosition - 1);
        adapter.notifyItemRemoved(syllabusPosition - 1);
        // remove margin
        lists.remove(syllabusPosition - 2);
        adapter.notifyItemRemoved(syllabusPosition - 2);

        int lastPosition = getLastClassItemPosition();
        // add button
        lists.add(lastPosition + 1, new LectureItem(LectureItem.Type.AddClassTime, true));
        adapter.notifyItemInserted(lastPosition + 1);

        // change button
        int removePosition = getRemoveItemPosition();
        lists.remove(removePosition);
        lists.add(removePosition, new LectureItem(LectureItem.Type.ResetLecture, true));
        adapter.notifyItemChanged(removePosition);
    }

    private void attachLectureDetailList(Lecture lecture) {
        lists.add(new LectureItem(LectureItem.Type.ShortHeader));
        lists.add(new LectureItem(LectureItem.Type.Margin));
        lists.add(new LectureItem("강의명", lecture.getCourse_title(), LectureItem.Type.Title));
        lists.add(new LectureItem("교수", lecture.getInstructor(), LectureItem.Type.Instructor));
        lists.add(new LectureItem("색상", lecture.getColorIndex(), lecture.getColor(), LectureItem.Type.Color));
        lists.add(new LectureItem(LectureItem.Type.Margin));
        lists.add(new LectureItem(LectureItem.Type.ShortHeader));
        lists.add(new LectureItem(LectureItem.Type.Margin));
        lists.add(new LectureItem("학과", lecture.getDepartment(), LectureItem.Type.Department));
        lists.add(new LectureItem("학년", lecture.getAcademic_year(), LectureItem.Type.AcademicYear));
        lists.add(new LectureItem("학점", String.valueOf(lecture.getCredit()), LectureItem.Type.Credit));
        lists.add(new LectureItem("분류", lecture.getClassification(), LectureItem.Type.Classification));
        lists.add(new LectureItem("구분", lecture.getCategory(), LectureItem.Type.Category));
        lists.add(new LectureItem("강좌번호", lecture.getCourse_number(), LectureItem.Type.CourseNumber));
        lists.add(new LectureItem("분반번호", lecture.getLecture_number(), LectureItem.Type.LectureNumber));
        lists.add(new LectureItem("비고", lecture.getRemark(), LectureItem.Type.Remark));
        lists.add(new LectureItem(LectureItem.Type.Margin));
        lists.add(new LectureItem(LectureItem.Type.ShortHeader));

        lists.add(new LectureItem(LectureItem.Type.Margin));
        lists.add(new LectureItem(LectureItem.Type.ClassTimeHeader));
        for (JsonElement element : lecture.getClass_time_json()) {
            JsonObject jsonObject = element.getAsJsonObject();
            ClassTime classTime = new ClassTime(jsonObject);
            lists.add(new LectureItem(classTime, LectureItem.Type.ClassTime));
        }
        lists.add(new LectureItem(LectureItem.Type.Margin));

        lists.add(new LectureItem(LectureItem.Type.LongHeader));

        lists.add(new LectureItem(LectureItem.Type.Syllabus));
        lists.add(new LectureItem(LectureItem.Type.ShortHeader));
        lists.add(new LectureItem(LectureItem.Type.RemoveLecture));
        lists.add(new LectureItem(LectureItem.Type.LongHeader));
    }

    private int getSyllabusItemPosition() {
        for (int i = 0;i < lists.size();i ++) {
            if (lists.get(i).getType() == LectureItem.Type.Syllabus) return i;
        }
        Log.e(TAG, "can't find syllabus item");
        return -1;
    }

    private int getAddClassTimeItemPosition() {
        for (int i = 0;i < lists.size();i ++) {
            if (lists.get(i).getType() == LectureItem.Type.AddClassTime) return i;
        }
        Log.e(TAG, "can't find add class time item");
        return -1;
    }


    private int getRemoveItemPosition() {
        for (int i = 0;i < lists.size();i ++) {
            if (lists.get(i).getType() == LectureItem.Type.RemoveLecture) return i;
        }
        Log.e(TAG, "can't find syllabus item");
        return -1;
    }

    private int getResetItemPosition() {
        for (int i = 0;i < lists.size();i ++) {
            if (lists.get(i).getType() == LectureItem.Type.ResetLecture) return i;
        }
        Log.e(TAG, "can't find reset item");
        return -1;
    }

    private int getClassTimeHeaderPosition() {
        for (int i = 0;i < lists.size();i ++) {
            if (lists.get(i).getType() == LectureItem.Type.ClassTimeHeader) return i;
        }
        Log.e(TAG, "can't find class time header item");
        return -1;
    }

    private int getLastClassItemPosition() {
        for (int i = getClassTimeHeaderPosition() + 1;i < lists.size();i ++) {
            if (lists.get(i).getType() != LectureItem.Type.ClassTime) return i - 1;
        }
        return lists.size() - 1;
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
