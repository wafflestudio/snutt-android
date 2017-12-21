package com.wafflestudio.snutt2.ui;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.view.MenuItemCompat;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.wafflestudio.snutt2.R;
import com.wafflestudio.snutt2.SNUTTBaseFragment;
import com.wafflestudio.snutt2.adapter.LectureListAdapter;
import com.wafflestudio.snutt2.adapter.SuggestionAdapter;
import com.wafflestudio.snutt2.listener.EndlessRecyclerViewScrollListener;
import com.wafflestudio.snutt2.adapter.TagListAdapter;
import com.wafflestudio.snutt2.manager.LectureManager;
import com.wafflestudio.snutt2.manager.TagManager;
import com.wafflestudio.snutt2.model.Lecture;
import com.wafflestudio.snutt2.model.Tag;
import com.wafflestudio.snutt2.model.TagType;
import com.wafflestudio.snutt2.view.DividerItemDecoration;
import com.wafflestudio.snutt2.view.TableView;

import java.util.List;

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
    private LinearLayoutManager lectureLayoutManager;
    private RecyclerView suggestionRecyclerView;
    private LectureListAdapter lectureAdapter;
    private SuggestionAdapter suggestionAdapter;
    private TagListAdapter tagAdapter;

    /**
     * This Variable is used for SearchView
     */
    enum TagMode {
        DEFAULT_MODE, TAG_MODE;
    }
    private static TagMode tagMode = TagMode.DEFAULT_MODE;

    private MenuItem searchMenuItem;
    private SearchView searchView;
    private String last_query = "";
    private SearchView.SearchAutoComplete editText;
    private ImageView clearButton;
    private boolean isSearchViewExpanded = false;
    private boolean isSearching = false;

    /*
     * This variables for tagHelper
     */
    private LinearLayout tagHelper;
    private LinearLayout lectureLayout;
    private LinearLayout suggestionLayout;
    private LinearLayout emptyClass;
    private RadioGroup radioGroup;
    private TextView emptyClassStatus;

    /*
     * This variables for placeholder
     */
    private LinearLayout placeholder;
    private LinearLayout help;
    private LinearLayout popup;

    /*
     * This variables for main content
     */
    private LinearLayout mainContainer;
    private LinearLayout emptyPlaceholder;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called!");
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        setHasOptionsMenu(true);

        tagHelper = (LinearLayout) rootView.findViewById(R.id.tag_suggestion);
        lectureRecyclerView = (RecyclerView) rootView.findViewById(R.id.search_recyclerView);
        suggestionRecyclerView = (RecyclerView) rootView.findViewById(R.id.suggestion_recyclerView);
        tagRecyclerView = (RecyclerView) rootView.findViewById(R.id.tag_recyclerView);
        mInstance = (TableView) rootView.findViewById(R.id.timetable);

        lectureLayoutManager = new LinearLayoutManager(getContext());
        lectureAdapter = new LectureListAdapter(LectureManager.getInstance().getSearchedLectures());
        lectureRecyclerView.setLayoutManager(lectureLayoutManager);
        lectureRecyclerView.setItemAnimator(null);
        lectureRecyclerView.setAdapter(lectureAdapter);
        lectureRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), R.drawable.search_lecture_divider));
        lectureRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(lectureLayoutManager) {
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
            public void onClick(View v, Tag tag) {
                Log.d(TAG, "suggestion item clicked!");
                TagManager.getInstance().addTag(tag);
                tagAdapter.notifyItemInserted(0);
                tagRecyclerView.scrollToPosition(0);
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
        if (TagManager.getInstance().getMyTags().size() > 0) {
            tagRecyclerView.setVisibility(View.VISIBLE);
        }

        lectureLayout = (LinearLayout) rootView.findViewById(R.id.lecture_layout);
        suggestionLayout = (LinearLayout) rootView.findViewById(R.id.suggestion_layout);
        setTagHelper();

        mainContainer = (LinearLayout) rootView.findViewById(R.id.main_container);
        emptyPlaceholder = (LinearLayout) rootView.findViewById(R.id.empty_placeholder);
        placeholder = (LinearLayout) rootView.findViewById(R.id.placeholder);
        popup = (LinearLayout) rootView.findViewById(R.id.popup);
        help = (LinearLayout) rootView.findViewById(R.id.help);
        setPlaceholder();
        setMainContainer();

        showPlaceholder();

        return rootView;
    }

    private void setKeyboardListener() {
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.d(TAG, "keyboard up");
                    showMainContainer(true);
                    tagHelper.setVisibility(View.VISIBLE);
                    getMainActivity().hideTabLayout();

                } else {
                    Log.d(TAG, "keyboard down");
                    if (isSearchViewExpanded) {
                        showMainContainer(false);
                    }
                    tagHelper.setVisibility(View.GONE);
                    getMainActivity().showTabLayout();
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
        final TextView tag = (TextView) tagHelper.findViewById(R.id.tag);
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableTagMode(false);
            }
        });
        final TextView cancel = (TextView) tagHelper.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableDefaultMode();
            }
        });
        radioGroup = (RadioGroup) tagHelper.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == -1) {
                    suggestionAdapter.toggleButton(null);
                } else {
                    View radioButton = radioGroup.findViewById(checkedId);
                    int idx = radioGroup.indexOfChild(radioButton);
                    suggestionAdapter.toggleButton(TagType.values()[idx]);
                }
            }
        });
        emptyClass = (LinearLayout) tagHelper.findViewById(R.id.empty_class);
        emptyClassStatus = (TextView) tagHelper.findViewById(R.id.status);
        emptyClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean b = TagManager.getInstance().toggleSearchEmptyClass();
                emptyClassStatus.setText(b ? "ON" : "OFF");
                emptyClassStatus.setTextColor(b ? Color.rgb(0,0,0) : Color.argb(76, 0, 0, 0));
            }
        });
    }

    private void setPlaceholder() {
        help.findViewById(R.id.search_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMenuItem.expandActionView();
                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        help.findViewById(R.id.help_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                help.setVisibility(View.GONE);
                popup.setVisibility(View.VISIBLE);
            }
        });
        popup.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.setVisibility(View.GONE);
                help.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setMainContainer() {
        emptyPlaceholder.findViewById(R.id.search_icon_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMenuItem.expandActionView();
                searchView.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    private void showPlaceholder() {
        mainContainer.setVisibility(View.GONE);
        popup.setVisibility(View.GONE);
        placeholder.setVisibility(View.VISIBLE);
        help.setVisibility(View.VISIBLE);
    }

    private void showMainContainer(boolean isKeyboardUp) {
        placeholder.setVisibility(View.GONE);
        mainContainer.setVisibility(View.VISIBLE);

        if (isDefaultMode() && !isKeyboardUp && !isSearching && LectureManager.getInstance().getSearchedLectures().size() == 0) {
            lectureRecyclerView.setVisibility(View.GONE);
            emptyPlaceholder.setVisibility(View.VISIBLE);
        } else {
            emptyPlaceholder.setVisibility(View.GONE);
            lectureRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchMenuItem.getActionView();
        clearButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        clearButton.setImageResource(R.drawable.ic_close);
        editText = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setThreshold(0);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) { // to handle empty query
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = searchView.getQuery().toString();
                    if (isDefaultMode() && TextUtils.isEmpty(text)) {
                        postQuery(text);
                        searchView.clearFocus();
                    } else {
                        searchView.setQuery(text, true);
                    }
                }
                return true;
            }
        });
        searchView.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
        searchView.setOnSuggestionListener(suggestionListener);
        searchView.setOnQueryTextListener(queryTextListener);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

//        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
//        searchView.setMaxWidth(dm.widthPixels); // handle some high density devices and landscape mode

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        if (searchManager!=null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        }
        enableDefaultMode();

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                Log.d(TAG, "onMenuItemActionExpand");
                isSearchViewExpanded = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                Log.d(TAG, "onMenuItemActionCollapse");
                showPlaceholder();
                isSearchViewExpanded = false;
                return true;
            }
        });
        setKeyboardListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        LectureManager.getInstance().removeListener(this);
        TagManager.getInstance().unregisterListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        LectureManager.getInstance().addListener(this);
        TagManager.getInstance().registerListener(this);
        showPlaceholder();
    }

    @Override
    public void notifyLecturesChanged() {
        mInstance.invalidate();
        lectureAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifySearchedLecturesChanged() {
        Log.d(TAG, "notify Searched Lectures Changed");
        lectureAdapter.notifyDataSetChanged();
        lectureRecyclerView.scrollToPosition(0);
    }

    /**
     *  this method only for whether tags are zero or not
     *  handle tag insert, remove listener in other method
     */
    @Override
    public void notifyMyTagChanged(boolean anim) {
        Log.d(TAG, "notifyMyTagChanged called");
        if (TagManager.getInstance().getMyTags().size() == 0 && tagRecyclerView.getVisibility() == View.VISIBLE) {
            tagRecyclerView.setVisibility(View.GONE);
            if (anim) {
                Animation animation = AnimationUtils.loadAnimation(getApp(), R.anim.slide_up);
                tagRecyclerView.startAnimation(animation);
            }
            tagAdapter.notifyDataSetChanged();
        } else if (TagManager.getInstance().getMyTags().size() > 0 && tagRecyclerView.getVisibility() == View.GONE) {
            tagRecyclerView.setVisibility(View.VISIBLE);
            if (anim) {
                Animation animation = AnimationUtils.loadAnimation(getApp(), R.anim.slide_down);
                tagRecyclerView.startAnimation(animation);
            }
            tagAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyTagListChanged() {
        Log.d(TAG, "notifyTagListChanged called");
        if (suggestionAdapter != null) {
            suggestionAdapter.notifyDataSetChanged();
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
            if (isDefaultMode()) {
                postQuery(query);
                searchView.clearFocus();
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
            if (Strings.isNullOrEmpty(newText) && isTagMode()) {
                clearButton.setVisibility(View.VISIBLE);
            }

            if (newText.endsWith("#")) {
                enableTagMode(true);
            } else if (isTagMode()) {
                Log.d(TAG, "Query text : " + newText);
                suggestionAdapter.filter(newText);
            }
            return true;
        }
    };

    private void postQuery(String text) {
        isSearching = true;
        LectureManager.getInstance().postSearchQuery(text, new Callback<List<Lecture>>() {
            @Override
            public void success(List<Lecture> lectures, Response response) {
                isSearching = false;
                showMainContainer(false);
            }
            @Override
            public void failure(RetrofitError error) {
                isSearching = false;
                showMainContainer(false);
            }
        });
    }

    private void enableTagMode(boolean contains) {
        Log.d(TAG, "enableTagMode");
        tagMode = TagMode.TAG_MODE;

        LinearLayout layout1 = (LinearLayout) tagHelper.findViewById(R.id.tag_mode);
        LinearLayout layout2 = (LinearLayout) tagHelper.findViewById(R.id.default_mode);
        layout2.setVisibility(View.GONE);
        layout1.setVisibility(View.VISIBLE);

        suggestionAdapter.resetState();
        radioGroup.clearCheck();

        int len = searchView.getQuery().length();
        if (contains) last_query = searchView.getQuery().toString().substring(0, len-1);
        else last_query = searchView.getQuery().toString();
        searchView.setQuery("", false);
        searchView.setQueryHint("태그 검색");

        clearButton.setVisibility(View.VISIBLE);
        clearButton.setOnClickListener(mTagListener);

        lectureLayout.setVisibility(View.GONE);
        suggestionLayout.setVisibility(View.VISIBLE);
        suggestionAdapter.filter("");
    }

    private void enableDefaultMode() {
        Log.d(TAG, "enableDefaultMode");
        tagMode = TagMode.DEFAULT_MODE;

        LinearLayout layout1 = (LinearLayout) tagHelper.findViewById(R.id.tag_mode);
        LinearLayout layout2 = (LinearLayout) tagHelper.findViewById(R.id.default_mode);
        layout1.setVisibility(View.GONE);
        layout2.setVisibility(View.VISIBLE);

        searchView.setQuery(last_query, false);
        searchView.setQueryHint("강의명 검색");
        searchView.setSuggestionsAdapter(null);
        clearButton.setOnClickListener(mDefaultListener);

        suggestionLayout.setVisibility(View.GONE);
        lectureLayout.setVisibility(View.VISIBLE);
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

    private boolean isTagMode() {
        return tagMode == TagMode.TAG_MODE;
    }

    private boolean isDefaultMode() {
        return tagMode == TagMode.DEFAULT_MODE;
    }
}
