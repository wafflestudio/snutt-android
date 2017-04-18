package com.wafflestudio.snutt_staging.ui;

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
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.wafflestudio.snutt_staging.R;
import com.wafflestudio.snutt_staging.SNUTTApplication;
import com.wafflestudio.snutt_staging.SNUTTBaseFragment;
import com.wafflestudio.snutt_staging.adapter.LectureListAdapter;
import com.wafflestudio.snutt_staging.adapter.SuggestionAdapter;
import com.wafflestudio.snutt_staging.listener.EndlessRecyclerViewScrollListener;
import com.wafflestudio.snutt_staging.adapter.TagListAdapter;
import com.wafflestudio.snutt_staging.manager.LectureManager;
import com.wafflestudio.snutt_staging.manager.TagManager;
import com.wafflestudio.snutt_staging.model.Lecture;
import com.wafflestudio.snutt_staging.view.TableView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

    private RecyclerView tagRecyclerView;
    private RecyclerView lectureRecyclerView;
    private RecyclerView suggestionRecyclerView;
    private LectureListAdapter lectureAdapter;
    private SuggestionAdapter suggestionAdapter;
    private TagListAdapter tagAdapter;

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
    private SearchSuggestionsAdapter suggestionAdapter2;
    private LinearLayout tagHelper;
    private LinearLayout lectureLayout;
    private LinearLayout suggestionLayout;

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

        tagHelper = (LinearLayout) rootView.findViewById(R.id.tag_suggestion);
        lectureRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_recyclerView);
        suggestionRecyclerView = (RecyclerView) rootView.findViewById(R.id.suggestion_recyclerView);
        tagRecyclerView = (RecyclerView) rootView.findViewById(R.id.tag_recyclerView);
        mInstance = (TableView) rootView.findViewById(R.id.timetable);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        lectureAdapter = new LectureListAdapter(LectureManager.getInstance().getSearchedLectures());
        lectureRecyclerView.setLayoutManager(layoutManager);
        lectureRecyclerView.setItemAnimator(null);
        lectureRecyclerView.setAdapter(lectureAdapter);
        lectureRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {
                return LectureListAdapter.VIEW_TYPE.ProgressBar.getValue();
            }
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadNextDataFromApi(page, totalItemsCount);
                Log.d(TAG, "on Load More called. page : " + page + ", totalItemsCount : " + totalItemsCount);
            }
        });

        suggestionAdapter = new SuggestionAdapter(TagManager.getInstance().getTags());
        suggestionAdapter.setClickListener(new SuggestionAdapter.ClickListener() {
            @Override
            public void onClick(View v, int position) {
                Log.d(TAG, "suggestion item clicked!");
                TextView textView = (TextView) v.findViewById(R.id.suggestion_text);
                String text = textView.getText().toString();
                if (TagManager.getInstance().addTag(text)) {
                    tagAdapter.notifyItemInserted(0);
                    tagRecyclerView.scrollToPosition(0);
                }
                enableDefaultMode();
            }
        });
        suggestionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        suggestionRecyclerView.setAdapter(suggestionAdapter);

        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(getApp(), LinearLayoutManager.HORIZONTAL, false);
        tagRecyclerView.setLayoutManager(horizontalLayoutManager);
        tagAdapter = new TagListAdapter(getContext(), TagManager.getInstance().getMyTags());
        tagRecyclerView.setAdapter(tagAdapter);

        lectureLayout = (LinearLayout) rootView.findViewById(R.id.lecture_layout);
        suggestionLayout = (LinearLayout) rootView.findViewById(R.id.suggestion_layout);
        setTagHelper();

        return rootView;
    }

    private void setKeyboardListener() {
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d(TAG, "keyboard up");
                    //Animation animation = AnimationUtils.loadAnimation(getApp(), R.anim.fade_in);
                    //tagHelper.startAnimation(animation);
                    tagHelper.setVisibility(View.VISIBLE);
                    getMainActivity().hideTabLayout();
                } else {
                    Log.d(TAG, "keyboard down");
                    tagHelper.setVisibility(View.GONE);
                    getMainActivity().showTabLayout();
                    enableDefaultMode();
                }
            }
        });
    }

    private void loadNextDataFromApi(int page, final int totalItemsCount) {
        LectureManager.getInstance().addProgressBar();
        lectureAdapter.notifyDataSetChanged();
        LectureManager.getInstance().loadData(totalItemsCount, new Callback<List<Lecture>>() {
            @Override
            public void success(List<Lecture> searchedLectureList, Response response) {
                lectureAdapter.notifyDataSetChanged();
            }
            @Override
            public void failure(RetrofitError error) { }
        });
    }

    private void setTagHelper() {
        ImageView tag = (ImageView) tagHelper.findViewById(R.id.tag);
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableTagMode(false);
            }
        });
        TextView cancel = (TextView) tagHelper.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDefaultMode();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        clearButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        editText = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setThreshold(0);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { // to handle empty query
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = searchView.getQuery().toString();
                    if (mode == DEFAULT_MODE && TextUtils.isEmpty(text)) {
                        searchView.clearFocus();
                        postQuery(text);
                    } else {
                        searchView.setQuery(text, true);
                    }
                }
                return true;
            }
        });
        suggestionAdapter2 = new SearchSuggestionsAdapter(getContext());
        searchView.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        searchView.setOnSuggestionListener(suggestionListener);
        searchView.setOnQueryTextListener(queryTextListener);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

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
                enableDefaultMode();
                return true;
            }
        });
        setKeyboardListener();
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
            Log.d(TAG, "onQueryTextSubmit called!");
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
                enableTagMode(true);
            } else if (mode == TAG_MODE) {
                Log.d(TAG, "Query text : " + newText);
                suggestionAdapter.filter(newText);
            }
            return true;
        }
    };

    private void postQuery(String text) {
        LectureManager.getInstance().postSearchQuery(text, new Callback<List<Lecture>>() {
            @Override
            public void success(List<Lecture> lectures, Response response) {
                lectureAdapter.notifyDataSetChanged();
            }
            @Override
            public void failure(RetrofitError error) { }
        });
    }

    private void enableTagMode(boolean contains) {
        mode = TAG_MODE;

        LinearLayout layout1 = (LinearLayout) tagHelper.findViewById(R.id.tag_mode);
        LinearLayout layout2 = (LinearLayout) tagHelper.findViewById(R.id.default_mode);
        layout1.setVisibility(View.VISIBLE);
        layout2.setVisibility(View.GONE);

        int len = searchView.getQuery().length();
        if (contains) last_query = searchView.getQuery().toString().substring(0, len-1);
        else last_query = searchView.getQuery().toString();
        searchView.setQuery("", false);
        searchView.setQueryHint("ex) 3학점, 컴공 등...");

        clearButton.setVisibility(View.VISIBLE);
        clearButton.setOnClickListener(mTagListener);

        lectureLayout.setVisibility(View.GONE);
        suggestionLayout.setVisibility(View.VISIBLE);
        suggestionAdapter.filter("");
    }

    private void enableDefaultMode() {
        mode = DEFAULT_MODE;

        LinearLayout layout1 = (LinearLayout) tagHelper.findViewById(R.id.tag_mode);
        LinearLayout layout2 = (LinearLayout) tagHelper.findViewById(R.id.default_mode);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);

        searchView.setQuery(last_query, false);
        searchView.setQueryHint("#으로 태그검색!");
        suggestionAdapter2.changeCursor(null);
        searchView.setSuggestionsAdapter(null);
        clearButton.setOnClickListener(mDefaultListener);

        lectureLayout.setVisibility(View.VISIBLE);
        suggestionLayout.setVisibility(View.GONE);
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
        private static final int[]    mViewIds = { /*R.id.text1*/ };


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
                /*mResults = new ArrayList<>();
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
                }*/
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
