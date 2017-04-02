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

    private Lecture lecture;
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
        lecture = getLectureMainActivity().lecture;
        if (lecture == null) add = true;

        lists = new ArrayList<>();
        attachLectureDetailList();
        for (LectureItem it : lists) it.setEditable(add);
        adapter = new CustomLectureAdapter(getActivity(), lecture, lists);
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
                            setNormalMode();
                        }
                        @Override
                        public void failure(RetrofitError error) {
                            Toast.makeText(getContext(), "강의 업데이트에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    item.setTitle("완료");
                    setEditMode();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLectureColor(Color color) {
        getColorItem().setColor(color); // 색상
        adapter.notifyDataSetChanged();
    }

    public void refreshFragment() {
        Log.d(TAG, "refresh fragment called.");
        editable = false;
        ActivityCompat.invalidateOptionsMenu(getActivity());
        lists.clear();
        attachLectureDetailList();
        adapter.notifyDataSetChanged();
    }

    public boolean getEditable() {
        return (!add && editable);
    }

    private void attachLectureDetailList() {
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem("강의명", add ? "" : lecture.getCourse_title(), LectureItem.Type.Title));
        lists.add(new LectureItem("교수", add ? "" : lecture.getInstructor(), LectureItem.Type.Instructor));
        lists.add(new LectureItem("색상", add ? new Color() : lecture.getColor(), LectureItem.Type.Color));
        lists.add(new LectureItem("학점", add ? "0" : String.valueOf(lecture.getCredit()), LectureItem.Type.Credit));
        lists.add(new LectureItem(LectureItem.Type.Header));
        lists.add(new LectureItem("비고", add ? "" : lecture.getRemark(), LectureItem.Type.Remark));
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
        lists.add(pos, new LectureItem(LectureItem.Type.Header, false));
        adapter.notifyItemInserted(pos);
        lists.add(pos + 1, new LectureItem(LectureItem.Type.RemoveLecture, false));
        adapter.notifyItemInserted(pos + 1);
    }

    private void setEditMode() {
        editable = true;
        for (int i = 0;i < lists.size();i ++) {
            LectureItem it = lists.get(i);
            it.setEditable(true);
            adapter.notifyItemChanged(i);
        }

        int pos = getRemoveItemPosition();
        lists.remove(pos - 1);
        adapter.notifyItemRemoved(pos - 1);
        lists.remove(pos - 1);
        adapter.notifyItemRemoved(pos - 1);

        int lastPosition = getLastClassItemPosition();
        Log.d(TAG, "last position : " + lastPosition);
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

    private int getLastClassItemPosition() {
        for (int i = 0;i < lists.size();i ++) {
            if (isLastClassItem(i)) return i;
        }
        Log.e(TAG, "can't find class time item");
        return -1;
    }

    private boolean isLastClassItem(int position) {
        if (position == lists.size() - 1) return false;
        return (lists.get(position).getType() == LectureItem.Type.ClassTime) && (lists.get(position + 1).getType() != LectureItem.Type.ClassTime);
    }

    protected void hideSoftKeyboard(View view) {
        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
