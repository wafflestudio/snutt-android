package com.wafflestudio.snutt_staging.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.adapter.CustomLectureAdapter;
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
 * Created by makesource on 2016. 11. 10..
 */
public class CustomDetailFragment extends SNUTTBaseFragment {
    private static final String TAG = "CUSTOM_DETAIL_FRAGMENT";

    private RecyclerView detailView;
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
        Lecture lecture = LectureManager.getInstance().getCurrentLecture();
        if (lecture == null) add = true;

        lists = new ArrayList<>();
        attachLectureDetailList(lecture);
        for (LectureItem it : lists) it.setEditable(add);
        adapter = new CustomLectureAdapter(getActivity(), lists);
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
        inflater.inflate(R.menu.menu_lecture_detail, menu);
        MenuItem item = menu.getItem(0);
        if (editable || add) {
            item.setTitle("완료");
        } else {
            item.setTitle("편집");
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_edit :
                if (add) {
                    item.setEnabled(false);
                    adapter.createLecture(new Callback<Table>() {
                        @Override
                        public void success(Table table, Response response) {
                            getLectureMainActivity().finish();
                        }
                        @Override
                        public void failure(RetrofitError error) {
                            item.setEnabled(true);
                        }
                    });
                } else if (editable) {
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
                            item.setEnabled(true);
                        }
                    });
                } else {
                    item.setTitle("완료");
                    setEditMode();
                }
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

    public void refreshFragment() {
        editable = false;

        ActivityCompat.invalidateOptionsMenu(getActivity());

        lists.clear();
        attachLectureDetailList(LectureManager.getInstance().getCurrentLecture());
        adapter.notifyDataSetChanged();
    }

    public boolean getEditable() {
        return (!add && editable);
    }

    private void attachLectureDetailList(Lecture lecture) {
        lists.add(new LectureItem(LectureItem.Type.ShortHeader));
        lists.add(new LectureItem(LectureItem.Type.Margin));
        lists.add(new LectureItem("강의명", add ? "" : lecture.getCourse_title(), LectureItem.Type.Title));
        lists.add(new LectureItem("교수", add ? "" : lecture.getInstructor(), LectureItem.Type.Instructor));
        lists.add(new LectureItem("색상", add ? 0 : lecture.getColorIndex(), add ? new Color() : lecture.getColor(), LectureItem.Type.Color));
        lists.add(new LectureItem("학점", add ? "0" : String.valueOf(lecture.getCredit()), LectureItem.Type.Credit));
        lists.add(new LectureItem(LectureItem.Type.Margin));
        lists.add(new LectureItem(LectureItem.Type.ShortHeader));
        lists.add(new LectureItem(LectureItem.Type.Margin));
        lists.add(new LectureItem("비고", add ? "" : lecture.getRemark(), LectureItem.Type.Remark));
        lists.add(new LectureItem(LectureItem.Type.Margin));
        lists.add(new LectureItem(LectureItem.Type.ShortHeader));

        lists.add(new LectureItem(LectureItem.Type.Margin));
        lists.add(new LectureItem(LectureItem.Type.ClassTimeHeader));
        if (!add) {
            for (JsonElement element : lecture.getClass_time_json()) {
                JsonObject jsonObject = element.getAsJsonObject();
                ClassTime classTime = new ClassTime(jsonObject);
                lists.add(new LectureItem(classTime, LectureItem.Type.ClassTime));
            }
            lists.add(new LectureItem(LectureItem.Type.Margin));

            lists.add(new LectureItem(LectureItem.Type.LongHeader));
            lists.add(new LectureItem(LectureItem.Type.RemoveLecture));
            lists.add(new LectureItem(LectureItem.Type.LongHeader));

        } else {
            lists.add(new LectureItem(LectureItem.Type.AddClassTime));
            lists.add(new LectureItem(LectureItem.Type.LongHeader));
        }
    }


    private void setNormalMode() {
        editable = false;
        hideSoftKeyboard(getView());
        for (int i = 0;i < lists.size();i ++) {
            LectureItem it = lists.get(i);
            it.setEditable(false);
            adapter.notifyItemChanged(i);
        }

        int pos = getAddClassTimeItemPosition();
        lists.remove(pos);
        adapter.notifyItemRemoved(pos);

        // add button & header
        pos = getLastItem();
        lists.add(pos, new LectureItem(LectureItem.Type.Margin, false));
        adapter.notifyItemInserted(pos);
        lists.add(pos + 1, new LectureItem(LectureItem.Type.LongHeader, false));
        adapter.notifyItemInserted(pos + 1);
        lists.add(pos + 2, new LectureItem(LectureItem.Type.RemoveLecture, false));
        adapter.notifyItemInserted(pos + 2);
    }

    private void setEditMode() {
        editable = true;
        for (int i = 0;i < lists.size();i ++) {
            LectureItem it = lists.get(i);
            it.setEditable(true);
            adapter.notifyItemChanged(i);
        }

        int pos = getRemoveItemPosition();
        // remove button
        lists.remove(pos);
        adapter.notifyItemRemoved(pos);
        // remove long header
        lists.remove(pos - 1);
        adapter.notifyItemRemoved(pos - 1);
        // remove margin
        lists.remove(pos - 2);
        adapter.notifyItemRemoved(pos - 2);

        int lastPosition = getLastClassItemPosition();
        // add button
        lists.add(lastPosition + 1, new LectureItem(LectureItem.Type.AddClassTime, true));
        adapter.notifyItemInserted(lastPosition + 1);
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

    private int getLastItem() {
        return lists.size() - 1;
    }


}
