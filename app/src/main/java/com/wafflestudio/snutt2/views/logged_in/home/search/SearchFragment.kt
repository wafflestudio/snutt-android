package com.wafflestudio.snutt2.views.logged_in.home.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.jakewharton.rxbinding4.view.clicks
import com.wafflestudio.snutt2.databinding.FragmentSearchBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    private val searchViewModel: SearchViewModel by activityViewModels()

    private val timetableViewModel: TimetableViewModel by activityViewModels()

    @Inject
    lateinit var apiOnError: ApiOnError

    private lateinit var binding: FragmentSearchBinding

    private lateinit var tagAdapter: TagAdapter

    private lateinit var searchResultAdapter: SearchResultAdapter

    private val bottomSheet = SearchOptionFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tagAdapter = TagAdapter { searchViewModel.toggleTag(it) }

        searchResultAdapter = SearchResultAdapter(
            onSelectLecture = { searchViewModel.toggleLectureSelection(it) },
            onToggleAddition = {
                timetableViewModel.toggleLecture(it)
                    .bindUi(
                        this,
                        onError = apiOnError,
                        onComplete = { searchViewModel.toggleLectureSelection(it) }
                    )
            },
            onShowSyllabus = { },
            onShowReviews = { },
        )

        binding.tagList.adapter = tagAdapter
        binding.lectureList.adapter = searchResultAdapter

        searchViewModel.searchTags
            .distinctUntilChanged()
            .bindUi(this) {
                binding.tagList.isVisible = it.isNotEmpty()
                tagAdapter.submitList(it)
            }

        searchViewModel.queryResults
            .distinctUntilChanged()
            .bindUi(this) {
                searchResultAdapter.submitData(lifecycle, it)
            }

        binding.searchButton.clicks()
            .bindUi(this) {
                searchViewModel.setTitle(binding.textEdit.text.toString())
                searchViewModel.refreshQuery()
            }

        binding.filterButton.clicks()
            .bindUi(this) {
                bottomSheet.show(parentFragmentManager, "tag_selector")
            }

        searchViewModel.searchTags
            .distinctUntilChanged()
            .bindUi(this) {
                binding.tagList.isVisible = it.isNotEmpty()
                tagAdapter.submitList(it)
            }


        binding.textEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchViewModel.setTitle(binding.textEdit.text.toString())
                searchViewModel.refreshQuery()
                true
            } else {
                false
            }
        }

        searchViewModel.selectedLecture
            .distinctUntilChanged()
            .bindUi(this) {
                binding.timetable.selectedLecture = it.get()
            }

        timetableViewModel.currentTimetable
            .distinctUntilChanged()
            .bindUi(this) {
                Timber.d(it.toString())
                binding.timetable.lectures = it.lectureList
            }

        timetableViewModel.trimParam
            .distinctUntilChanged()
            .bindUi(this) {
                binding.timetable.trimParam = it
            }
    }
}
