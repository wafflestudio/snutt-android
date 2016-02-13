package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.SNUTTUtils;
import com.wafflestudio.snutt.adapter.LectureListAdapter;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.view.TableView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SearchFragment extends SNUTTBaseFragment implements LectureManager.OnLectureChangedListener{ /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "search_fragment";
    private static TableView mInstance;

    private Button searchButton;
    private EditText searchEditText;
    private String searchText;
    private List<Lecture> lectureList;
    private RecyclerView recyclerView;
    private LectureListAdapter mAdapter;
    private Map query;

    private String year;
    private String semester;

    public SearchFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SearchFragment newInstance(int sectionNumber) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        searchButton = (Button) rootView.findViewById(R.id.search_button);
        searchEditText = (EditText) rootView.findViewById(R.id.search_editText);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.search_recyclerView);
        mInstance = (TableView) rootView.findViewById(R.id.timetable);
        lectureList = new ArrayList<>();

        year = getMainActivity().year;
        semester = getMainActivity().semester;

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApp());
        mAdapter = new LectureListAdapter(lectureList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = new HashMap();
                searchText = searchEditText.getText().toString();
                query.put("year",year);
                query.put("semester",semester);
                //query.put("title", searchText);
                query.put("title",searchText);

                getApp().getRestService().postSearchQuery(query, new Callback<List<Lecture>>() {
                    @Override
                    public void success(List<Lecture> lectures, Response response) {
                        Log.d(TAG, "post search query success!!");
                        System.out.println(lectures);
                        mAdapter.setLectures(lectures);;
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "post search query failed!!");
                        System.out.println(error);
                    }
                });
            }
        });
        return rootView;
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
    }

    @Override
    public void notifyLectureChanged() {
        mInstance.invalidate();
    }
}
