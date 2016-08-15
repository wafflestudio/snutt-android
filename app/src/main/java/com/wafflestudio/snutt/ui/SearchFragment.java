package com.wafflestudio.snutt.ui;

import android.app.SearchManager;
import android.content.Context;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.common.base.Strings;
import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTApplication;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.manager.PrefManager;
import com.wafflestudio.snutt.ui.adapter.LectureListAdapter;
import com.wafflestudio.snutt.ui.adapter.TagListAdapter;
import com.wafflestudio.snutt.manager.LectureManager;
import com.wafflestudio.snutt.manager.TagManager;
import com.wafflestudio.snutt.model.Lecture;
import com.wafflestudio.snutt.model.Tag;
import com.wafflestudio.snutt.view.TableView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class SearchFragment extends SNUTTBaseFragment
        implements LectureManager.OnLectureChangedListener, TagManager.OnTagChangedListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "search_fragment";
    private static TableView mInstance;

    private List<Lecture> lectureList;
    private RecyclerView tagRecyclerView;
    private RecyclerView lectureRecyclerView;
    private LectureListAdapter mAdapter;
    private TagListAdapter tagAdapter;
    private Map query;

    /**
     * This Variable is used for SearchView
     */
    private static final int DEFAULT_MODE = 1;
    private static final int TAG_MODE = 2;

    private int mode = DEFAULT_MODE;
    private SearchView searchView;
    private String last_query = "";
    private SearchView.SearchAutoComplete editText;
    private ImageView clearButton;
    private SearchSuggestionsAdapter suggestionAdapter;


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
        setHasOptionsMenu(true);

        lectureRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_recyclerView);
        tagRecyclerView = (RecyclerView) rootView.findViewById(R.id.tag_recyclerView);
        mInstance = (TableView) rootView.findViewById(R.id.timetable);
        lectureList = new ArrayList<>();

        //LinearLayoutManager layoutManager
        //        = new LinearLayoutManager(getApp(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApp());
        mAdapter = new LectureListAdapter(lectureList);
        lectureRecyclerView.setLayoutManager(layoutManager);
        lectureRecyclerView.setItemAnimator(null);
        lectureRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getApp(), LinearLayoutManager.HORIZONTAL, false);
        tagRecyclerView.setLayoutManager(horizontalLayoutManager);
        tagAdapter = new TagListAdapter(getContext(), TagManager.getInstance().getMyTags());
        tagRecyclerView.setAdapter(tagAdapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        clearButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        editText = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setThreshold(0);
        suggestionAdapter = new SearchSuggestionsAdapter(getContext());
        searchView.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        searchView.setOnSuggestionListener(suggestionListener);
        searchView.setOnQueryTextListener(queryTextListener);
        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        searchView.setMaxWidth(dm.widthPixels); // handle some high density devices and landscape mode

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchManager!=null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }
        enableDefaultMode();

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                enableDefaultMode();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        LectureManager.getInstance().removeListener(this);
        TagManager.getInstance().unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        LectureManager.getInstance().addListener(this);
        TagManager.getInstance().registerListener(this);

    }

    @Override
    public void notifyLectureChanged() {
        mInstance.invalidate();
    }

    @Override
    public void notifyTagChanged() {
        if (TagManager.getInstance().getMyTags().size() == 0 && tagRecyclerView.getVisibility() == View.VISIBLE) {
            tagRecyclerView.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(getApp(), R.anim.slide_up);
            tagRecyclerView.startAnimation(animation);
        } else if (TagManager.getInstance().getMyTags().size() > 0 && tagRecyclerView.getVisibility() == View.GONE) {
            tagRecyclerView.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getApp(), R.anim.slide_down);
            tagRecyclerView.startAnimation(animation);
        }
    }


    private SearchView.OnSuggestionListener suggestionListener = new SearchView.OnSuggestionListener() {
        @Override
        public boolean onSuggestionClick(int position) {
            Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
            String query = cursor.getString(1);
            searchView.setQuery(query, false);
            return true;
        }

        @Override
        public boolean onSuggestionSelect(int position) {
            return false;
        }
    };

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            if (mode == DEFAULT_MODE) {
                searchView.clearFocus();
                postQuery(query);
                return false;
            } else {
                if (TagManager.getInstance().addTag(query)) {
                    tagAdapter.notifyItemInserted(0);
                    tagRecyclerView.scrollToPosition(0);
                }
                enableDefaultMode();
                return true;
            }
        }
        @Override
        public boolean onQueryTextChange(String newText) {
            if (Strings.isNullOrEmpty(newText) && mode == TAG_MODE) {
                clearButton.setVisibility(View.VISIBLE);
            }

            if (newText.endsWith("#")) {
                enableTagMode();
            }

            return true;
        }
    };

    private void postQuery(String text) {
        query = new HashMap();
        query.put("year", PrefManager.getInstance().getCurrentYear());
        query.put("semester", PrefManager.getInstance().getCurrentSemester());
        query.put("title", text);
        query.put("classification", TagManager.getInstance().getClassification());
        query.put("credit", TagManager.getInstance().getCredit());
        query.put("academic_year", TagManager.getInstance().getAcademic_year());
        query.put("instructor", TagManager.getInstance().getInstructor());
        query.put("department", TagManager.getInstance().getDepartment());
        query.put("category", TagManager.getInstance().getCategory());
        //query.put("time", TagManager.getInstance().getTime());

        getApp().getRestService().postSearchQuery(query, new Callback<List<Lecture>>() {
            @Override
            public void success(List<Lecture> lectures, Response response) {
                Log.d(TAG, "post search query success!!");
                System.out.println(lectures);
                mAdapter.setLectures(lectures);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "post search query failed!!");
                System.out.println(error);
            }
        });
    }

    private void enableTagMode() {
        mode = TAG_MODE;

        int len = searchView.getQuery().length();
        last_query = searchView.getQuery().toString().substring(0, len-1);
        searchView.setQuery("", false);
        searchView.setQueryHint("ex) 3학점, 컴공 등...");
        searchView.setSuggestionsAdapter(suggestionAdapter);
        SearchView.SearchAutoComplete text = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        Drawable img = getActivity().getResources().getDrawable(R.drawable.hash);
        text.setCompoundDrawablesWithIntrinsicBounds(img,
                null,
                null,
                null);
        text.setCompoundDrawablePadding((int) SNUTTApplication.dpTopx(5));

        clearButton.setVisibility(View.VISIBLE);
        clearButton.setOnClickListener(mTagListener);
    }

    private void enableDefaultMode() {
        mode = DEFAULT_MODE;

        searchView.setQuery(last_query, false);
        searchView.setQueryHint("#으로 태그검색!");
        suggestionAdapter.changeCursor(null);
        searchView.setSuggestionsAdapter(null);
        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        clearButton.setOnClickListener(mDefaultListener);
    }

    private View.OnClickListener mDefaultListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            searchView.setQuery("",false);
        }
    };

    private View.OnClickListener mTagListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enableDefaultMode();
        }
    };

    public static class SearchSuggestionsAdapter extends SimpleCursorAdapter
    {
        private static final String[] mFields  = { "_id" , "result" }; // _id field must exist
        private static final String[] mVisible = { "result" }; // db field name
        private static final int[]    mViewIds = { R.id.text1 };


        public SearchSuggestionsAdapter(Context context)
        {
            super(context, R.layout.cell_suggestion, null, mVisible, mViewIds, 0);
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint)
        {
            return new SuggestionsCursor(constraint);
        }

        private static class SuggestionsCursor extends AbstractCursor
        {
            private ArrayList<String> mResults;

            public SuggestionsCursor(CharSequence constraint)
            {
                /*final int count = 100;
                mResults = new ArrayList<String>(count);
                for(int i = 0; i < count; i++){
                    mResults.add("Result " + (i + 1));
                }*/
                mResults = new ArrayList<>();
                List<String> tags = TagManager.getInstance().getTags();
                for (String tag : tags) {
                    mResults.add(tag);
                }

                if(!TextUtils.isEmpty(constraint)){
                    String constraintString = constraint.toString().toLowerCase(Locale.ROOT);
                    Iterator<String> iter = mResults.iterator();
                    while(iter.hasNext()) {
                        if(!iter.next().toLowerCase(Locale.ROOT).startsWith(constraintString)) {
                            iter.remove();
                        }
                    }
                }
            }

            @Override
            public int getCount()
            {
                return mResults.size();
            }

            @Override
            public String[] getColumnNames()
            {
                return mFields;
            }

            @Override
            public long getLong(int column)
            {
                if(column == 0){
                    return mPos;
                }
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public String getString(int column)
            {
                if(column == 1){
                    return mResults.get(mPos);
                }
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public short getShort(int column)
            {
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public int getInt(int column)
            {
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public float getFloat(int column)
            {
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public double getDouble(int column)
            {
                throw new UnsupportedOperationException("unimplemented");
            }

            @Override
            public boolean isNull(int column)
            {
                return false;
            }
        }
    }


}
