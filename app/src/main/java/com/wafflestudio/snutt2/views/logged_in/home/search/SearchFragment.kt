package com.wafflestudio.snutt2.views.logged_in.home.search

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.paging.LoadState
import com.jakewharton.rxbinding4.view.clicks
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentSearchBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.getFittingTableTrimParam
import com.wafflestudio.snutt2.lib.rx.loadingState
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.kotlin.Observables
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
            onShowSyllabus = {
                searchViewModel.getCourseBookUrl()
                    .bindUi(
                        this,
                        onSuccess = { result ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url))
                            requireContext().startActivity(intent)
                        },
                        onError = apiOnError
                    )
            },
            onShowReviews = {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.lecture_review_not_ready),
                    Toast.LENGTH_SHORT
                ).show()
            },
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

        searchResultAdapter.loadingState()
            .bindUi(this) {
                if (it.refresh is LoadState.NotLoading && it.refresh.endOfPaginationReached.not() && searchResultAdapter.itemCount < 1) {
                    binding.placeholder.root.isVisible = true
                    binding.lectureList.isVisible = false
                    binding.empty.root.isVisible = false
                } else if (it.refresh is LoadState.NotLoading && it.append.endOfPaginationReached && searchResultAdapter.itemCount < 1) {
                    binding.placeholder.root.isVisible = false
                    binding.lectureList.isVisible = false
                    binding.empty.root.isVisible = true
                } else if (it.refresh is LoadState.Error) {
                    binding.placeholder.root.isVisible = false
                    binding.lectureList.isVisible = false
                    binding.empty.root.isVisible = true

                } else {
                    binding.placeholder.root.isVisible = false
                    binding.lectureList.isVisible = true
                    binding.empty.root.isVisible = false

                }
            }

        binding.placeholder.searchIcon.throttledClicks()
            .bindUi(this) {
                binding.textEdit.requestFocus()
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.textEdit, InputMethodManager.SHOW_IMPLICIT)
            }

        searchViewModel.selectedLecture
            .distinctUntilChanged()
            .bindUi(this) {
                binding.timetable.selectedLecture = it.get()
            }

        timetableViewModel.currentTimetable
            .distinctUntilChanged()
            .bindUi(this) {
                binding.timetable.theme = it.theme
                binding.timetable.lectures = it.lectureList
            }

        Observables.combineLatest(
            timetableViewModel.currentTimetable,
            timetableViewModel.trimParam
        )
            .distinctUntilChanged()
            .bindUi(this) { (table, trimParam) ->
                binding.timetable.trimParam =
                    if (trimParam.forceFitLectures) table.lectureList.getFittingTableTrimParam()
                    else trimParam
            }
    }
}
