package com.wafflestudio.snutt2.ui

import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.SearchAutoComplete
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.common.base.Strings
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.SNUTTUtils.displayWidth
import com.wafflestudio.snutt2.adapter.LectureListAdapter
import com.wafflestudio.snutt2.adapter.SuggestionAdapter
import com.wafflestudio.snutt2.adapter.TagListAdapter
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.listener.EndlessRecyclerViewScrollListener
import com.wafflestudio.snutt2.manager.LectureManager
import com.wafflestudio.snutt2.manager.LectureManager.OnLectureChangedListener
import com.wafflestudio.snutt2.manager.PrefStorage
import com.wafflestudio.snutt2.manager.TagManager
import com.wafflestudio.snutt2.manager.TagManager.OnTagChangedListener
import com.wafflestudio.snutt2.model.Tag
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.view.DividerItemDecoration
import com.wafflestudio.snutt2.view.TableView
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by makesource on 2016. 1. 16..
 */
@AndroidEntryPoint
class SearchFragment : SNUTTBaseFragment(), OnLectureChangedListener, OnTagChangedListener {

    @Inject
    lateinit var lectureManager: LectureManager

    @Inject
    lateinit var tagManager: TagManager

    @Inject
    lateinit var prefStorage: PrefStorage

    @Inject
    lateinit var apiOnError: ApiOnError

    private var tagRecyclerView: RecyclerView? = null
    private var lectureRecyclerView: RecyclerView? = null
    private var lectureLayoutManager: LinearLayoutManager? = null
    private var suggestionRecyclerView: RecyclerView? = null
    private var lectureAdapter: LectureListAdapter? = null
    private var suggestionAdapter: SuggestionAdapter? = null
    private var tagAdapter: TagListAdapter? = null

    /**
     * This Variable is used for SearchView
     */
    internal enum class TagMode {
        DEFAULT_MODE, TAG_MODE
    }

    private var searchMenuItem: MenuItem? = null
    private var searchView: SearchView? = null
    private var last_query = ""
    private var editText: SearchAutoComplete? = null
    private var clearButton: ImageView? = null
    private var isSearchViewExpanded = false
    private var isSearching = false

    /*
     * This variables for tagHelper
     */
    private var tagHelper: LinearLayout? = null
    private var lectureLayout: LinearLayout? = null
    private var suggestionLayout: LinearLayout? = null
    private var emptyClass: LinearLayout? = null
    private var radioGroup: RadioGroup? = null
    private var emptyClassStatus: TextView? = null

    /*
     * This variables for placeholder
     */
    private var placeholder: LinearLayout? = null
    private var help: LinearLayout? = null
    private var popup: LinearLayout? = null

    /*
     * This variables for main content
     */
    private var mainContainer: LinearLayout? = null
    private var emptyPlaceholder: LinearLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView called!")
        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        setHasOptionsMenu(true)
        tagHelper = rootView.findViewById<View>(R.id.tag_suggestion) as LinearLayout
        lectureRecyclerView = rootView.findViewById<View>(R.id.search_recyclerView) as RecyclerView
        suggestionRecyclerView = rootView.findViewById<View>(R.id.suggestion_recyclerView) as RecyclerView
        tagRecyclerView = rootView.findViewById<View>(R.id.tag_recyclerView) as RecyclerView
        mInstance = rootView.findViewById(R.id.timetable)

        lectureLayoutManager = LinearLayoutManager(context)
        lectureAdapter = LectureListAdapter(lectureManager.getSearchedLectures(), lectureManager, apiOnError, this)
        lectureRecyclerView!!.layoutManager = lectureLayoutManager
        lectureRecyclerView!!.itemAnimator = null
        lectureRecyclerView!!.adapter = lectureAdapter
        lectureRecyclerView!!.addItemDecoration(
            DividerItemDecoration(requireContext(), R.drawable.search_lecture_divider)
        )
        lectureRecyclerView!!.addOnScrollListener(
            object : EndlessRecyclerViewScrollListener(lectureLayoutManager!!) {
                override fun getFooterViewType(defaultNoFooterViewType: Int): Int {
                    return LectureListAdapter.VIEW_TYPE.ProgressBar.value
                }

                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    loadNextDataFromApi(page, totalItemsCount)
                    Log.d(TAG, "on Load More called. page : $page, totalItemsCount : $totalItemsCount")
                }
            }
        )
        suggestionAdapter = SuggestionAdapter(tagManager.getTags())
        suggestionAdapter!!.setClickListener(
            object : SuggestionAdapter.ClickListener {
                override fun onClick(v: View?, tag: Tag?) {
                    Log.d(TAG, "suggestion item clicked!")
                    tagManager.addTag(tag)
                    tagAdapter!!.notifyItemInserted(0)
                    tagRecyclerView!!.scrollToPosition(0)
                    enableDefaultMode()
                }
            }
        )
        suggestionRecyclerView!!.layoutManager = LinearLayoutManager(context)
        suggestionRecyclerView!!.adapter = suggestionAdapter
        val horizontalLayoutManager = LinearLayoutManager(
            app,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        tagRecyclerView!!.layoutManager = horizontalLayoutManager
        tagAdapter = TagListAdapter(requireContext(), tagManager.getMyTags(), tagManager)
        tagRecyclerView!!.adapter = tagAdapter
        if (tagManager.getMyTags().size > 0) {
            tagRecyclerView!!.visibility = View.VISIBLE
        }
        lectureLayout = rootView.findViewById<View>(R.id.lecture_layout) as LinearLayout
        suggestionLayout = rootView.findViewById<View>(R.id.suggestion_layout) as LinearLayout
        setTagHelper()
        mainContainer = rootView.findViewById<View>(R.id.main_container) as LinearLayout
        emptyPlaceholder = rootView.findViewById<View>(R.id.empty_placeholder) as LinearLayout
        placeholder = rootView.findViewById<View>(R.id.placeholder) as LinearLayout
        popup = rootView.findViewById<View>(R.id.popup) as LinearLayout
        help = rootView.findViewById<View>(R.id.help) as LinearLayout
        setPlaceholder()
        setMainContainer()
        showPlaceholder()
        return rootView
    }

    private fun setKeyboardListener() {
        searchView!!.setOnQueryTextFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                Log.d(TAG, "keyboard up")
                showMainContainer(true)
                tagHelper!!.visibility = View.VISIBLE
                mainActivity!!.hideTabLayout()
            } else {
                Log.d(TAG, "keyboard down")
                if (isSearchViewExpanded) {
                    showMainContainer(false)
                }
                tagHelper!!.visibility = View.GONE
                mainActivity!!.showTabLayout()
            }
        }
    }

    private fun loadNextDataFromApi(page: Int, totalItemsCount: Int) {
//        Refactoring FIXME: dirty progress
//        lectureManager.addProgressBar()
        lectureManager.loadData(totalItemsCount)
            .delay(3000L, TimeUnit.MILLISECONDS)
            .bindUi(
                this,
                onSuccess = {
                    lectureAdapter!!.notifyDataSetChanged()
                },
                onError = {
                    apiOnError(it)
                }
            )
    }

    private fun setTagHelper() {
        val tag = tagHelper!!.findViewById<View>(R.id.tag) as TextView
        tag.setOnClickListener { enableTagMode(false) }
        val cancel = tagHelper!!.findViewById<View>(R.id.cancel) as TextView
        cancel.setOnClickListener { enableDefaultMode() }
        radioGroup = tagHelper!!.findViewById<View>(R.id.radio_group) as RadioGroup
        radioGroup!!.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId == -1) {
                suggestionAdapter!!.toggleButton(null)
            } else {
                val radioButton = radioGroup!!.findViewById<View>(checkedId)
                val idx = radioGroup!!.indexOfChild(radioButton)
                suggestionAdapter!!.toggleButton(TagType.values()[idx])
            }
        }
        emptyClass = tagHelper!!.findViewById<View>(R.id.empty_class) as LinearLayout
        emptyClassStatus = tagHelper!!.findViewById<View>(R.id.status) as TextView
        emptyClass!!.setOnClickListener {
            val b = tagManager.toggleSearchEmptyClass()
            emptyClassStatus!!.text = if (b) "ON" else "OFF"
            emptyClassStatus!!.setTextColor(if (b) Color.rgb(0, 0, 0) else Color.argb(76, 0, 0, 0))
        }
    }

    private fun setPlaceholder() {
        help!!.findViewById<View>(R.id.search_icon).setOnClickListener {
            searchMenuItem!!.expandActionView()
            searchView!!.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
        help!!.findViewById<View>(R.id.help_text).setOnClickListener {
            help!!.visibility = View.GONE
            popup!!.visibility = View.VISIBLE
        }
        popup!!.findViewById<View>(R.id.button_close).setOnClickListener {
            popup!!.visibility = View.GONE
            help!!.visibility = View.VISIBLE
        }
    }

    private fun setMainContainer() {
        emptyPlaceholder!!.findViewById<View>(R.id.search_icon_empty).setOnClickListener {
            searchMenuItem!!.expandActionView()
            searchView!!.requestFocus()
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun showPlaceholder() {
        mainContainer!!.visibility = View.GONE
        popup!!.visibility = View.GONE
        placeholder!!.visibility = View.VISIBLE
        help!!.visibility = View.VISIBLE
    }

    private fun showMainContainer(isKeyboardUp: Boolean) {
        placeholder!!.visibility = View.GONE
        mainContainer!!.visibility = View.VISIBLE
        if (isDefaultMode && !isKeyboardUp && !isSearching && lectureManager.getSearchedLectures().size == 0) {
            lectureRecyclerView!!.visibility = View.GONE
            emptyPlaceholder!!.visibility = View.VISIBLE
        } else {
            emptyPlaceholder!!.visibility = View.GONE
            lectureRecyclerView!!.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        searchMenuItem = menu.findItem(R.id.action_search)
        searchView = searchMenuItem!!.getActionView() as SearchView
        clearButton = searchView!!.findViewById<View>(R.id.search_close_btn) as ImageView
        clearButton!!.setImageResource(R.drawable.ic_close)
        editText = searchView!!.findViewById<View>(R.id.search_src_text) as SearchAutoComplete
        editText!!.setThreshold(0)
        editText!!.setOnEditorActionListener { v, actionId, event -> // to handle empty query
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val text = searchView!!.query.toString()
                if (isDefaultMode && TextUtils.isEmpty(text)) {
                    postQuery(text)
                    searchView!!.clearFocus()
                } else {
                    searchView!!.setQuery(text, true)
                }
            }
            true
        }
        searchView!!.layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT
        )
        searchView!!.setOnSuggestionListener(suggestionListener)
        searchView!!.setOnQueryTextListener(queryTextListener)
        searchView!!.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        val width = requireContext().displayWidth.toInt()
        searchView!!.maxWidth = width // handle some high density devices and landscape mode
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        if (searchManager != null) {
            searchView!!.setSearchableInfo(
                searchManager.getSearchableInfo(requireActivity().componentName)
            )
        }
        enableDefaultMode()
        MenuItemCompat.setOnActionExpandListener(
            searchMenuItem,
            object : MenuItemCompat.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    Log.d(TAG, "onMenuItemActionExpand")
                    isSearchViewExpanded = true
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    Log.d(TAG, "onMenuItemActionCollapse")
                    showPlaceholder()
                    isSearchViewExpanded = false
                    return true
                }
            }
        )
        setKeyboardListener()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
        lectureManager.removeListener(this)
        tagManager.unregisterListener()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        lectureManager.addListener(this)
        tagManager.registerListener(this)
        showPlaceholder()
    }

    override fun notifyLecturesChanged() {
        requireActivity().runOnUiThread {
            mInstance!!.invalidate()
            lectureAdapter!!.notifyDataSetChanged()
        }
    }

    override fun notifySearchedLecturesChanged() {
        Log.d(TAG, "notify Searched Lectures Changed")
        requireActivity().runOnUiThread {
            lectureAdapter!!.notifyDataSetChanged()
            lectureRecyclerView!!.scrollToPosition(0)
        }
    }

    /**
     * this method only for whether tags are zero or not
     * handle tag insert, remove listener in other method
     */
    override fun notifyMyTagChanged(anim: Boolean) {
        Log.d(TAG, "notifyMyTagChanged called")
        if (tagManager.getMyTags().size == 0 && tagRecyclerView!!.visibility == View.VISIBLE) {
            tagRecyclerView!!.visibility = View.GONE
            if (anim) {
                val animation = AnimationUtils.loadAnimation(app, R.anim.slide_up)
                tagRecyclerView!!.startAnimation(animation)
            }
            tagAdapter!!.notifyDataSetChanged()
        } else if (tagManager.getMyTags().size > 0 && tagRecyclerView!!.visibility == View.GONE) {
            tagRecyclerView!!.visibility = View.VISIBLE
            if (anim) {
                val animation = AnimationUtils.loadAnimation(app, R.anim.slide_down)
                tagRecyclerView!!.startAnimation(animation)
            }
            tagAdapter!!.notifyDataSetChanged()
        }
    }

    override fun notifyTagListChanged() {
        Log.d(TAG, "notifyTagListChanged called")
        if (suggestionAdapter != null) {
            suggestionAdapter!!.notifyDataSetChanged()
        }
    }

    private val suggestionListener: SearchView.OnSuggestionListener = object : SearchView.OnSuggestionListener {
        override fun onSuggestionClick(position: Int): Boolean {
            val cursor = searchView!!.suggestionsAdapter.getItem(position) as Cursor
            val query = cursor.getString(1)
            searchView!!.setQuery(query, false)
            return true
        }

        override fun onSuggestionSelect(position: Int): Boolean {
            return false
        }
    }
    private val queryTextListener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            Log.d(TAG, "onQueryTextSubmit called!")
            return if (isDefaultMode) {
                postQuery(query)
                searchView!!.clearFocus()
                false
            } else {
                if (tagManager.addTag(query)) {
                    tagAdapter!!.notifyItemInserted(0)
                    tagRecyclerView!!.scrollToPosition(0)
                }
                enableDefaultMode()
                true
            }
        }

        override fun onQueryTextChange(newText: String): Boolean {
            if (Strings.isNullOrEmpty(newText) && isTagMode) {
                clearButton!!.visibility = View.VISIBLE
            }
            if (newText.endsWith("#")) {
                enableTagMode(true)
            } else if (isTagMode) {
                Log.d(TAG, "Query text : $newText")
                suggestionAdapter!!.filter(newText)
            }
            return true
        }
    }

    private fun postQuery(text: String) {
        isSearching = true
        lectureManager.postSearchQuery(text)
            .bindUi(
                this,
                onSuccess = {
                    isSearching = false
                    showMainContainer(false)
                },
                onError = {
                    apiOnError(it)
                    isSearching = false
                    showMainContainer(false)
                }
            )
    }

    private fun enableTagMode(contains: Boolean) {
        Log.d(TAG, "enableTagMode")
        tagMode = TagMode.TAG_MODE
        val layout1 = tagHelper!!.findViewById<View>(R.id.tag_mode) as LinearLayout
        val layout2 = tagHelper!!.findViewById<View>(R.id.default_mode) as LinearLayout
        layout2.visibility = View.GONE
        layout1.visibility = View.VISIBLE
        suggestionAdapter!!.resetState()
        radioGroup!!.clearCheck()
        val len = searchView!!.query.length
        last_query = if (contains) searchView!!.query.toString().substring(0, len - 1) else searchView!!.query.toString()
        searchView!!.setQuery("", false)
        searchView!!.queryHint = "태그 검색"
        clearButton!!.visibility = View.VISIBLE
        clearButton!!.setOnClickListener(mTagListener)
        lectureLayout!!.visibility = View.GONE
        suggestionLayout!!.visibility = View.VISIBLE
        suggestionAdapter!!.filter("")
    }

    private fun enableDefaultMode() {
        Log.d(TAG, "enableDefaultMode")
        tagMode = TagMode.DEFAULT_MODE
        val layout1 = tagHelper!!.findViewById<View>(R.id.tag_mode) as LinearLayout
        val layout2 = tagHelper!!.findViewById<View>(R.id.default_mode) as LinearLayout
        layout1.visibility = View.GONE
        layout2.visibility = View.VISIBLE
        searchView!!.setQuery(last_query, false)
        searchView!!.queryHint = "강의명 검색"
        searchView!!.suggestionsAdapter = null
        clearButton!!.setOnClickListener(mDefaultListener)
        suggestionLayout!!.visibility = View.GONE
        lectureLayout!!.visibility = View.VISIBLE
    }

    private val mDefaultListener = View.OnClickListener { searchView!!.setQuery("", false) }
    private val mTagListener = View.OnClickListener { enableDefaultMode() }
    private val isTagMode: Boolean
        private get() = tagMode == TagMode.TAG_MODE
    private val isDefaultMode: Boolean
        private get() = tagMode == TagMode.DEFAULT_MODE

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val TAG = "search_fragment"
        private var mInstance: TableView? = null
        private var tagMode = TagMode.DEFAULT_MODE

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}
