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
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.paging.LoadState
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.focusChanges
import com.wafflestudio.snutt2.databinding.FragmentSearchBinding
import com.wafflestudio.snutt2.lib.SnuttUrls
import com.wafflestudio.snutt2.lib.android.HomePage
import com.wafflestudio.snutt2.lib.android.HomePagerController
import com.wafflestudio.snutt2.lib.android.ReviewUrlController
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.call_adapter.ErrorParsedHttpException
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.rx.hideSoftKeyboard
import com.wafflestudio.snutt2.lib.rx.loadingState
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    private val searchViewModel: SearchViewModel by activityViewModels()

    private val selectedTimetableViewModel: SelectedTimetableViewModel by activityViewModels()

    @Inject
    lateinit var apiOnError: ApiOnError

    @Inject
    lateinit var homePagerController: HomePagerController

    @Inject
    lateinit var reviewUrlController: ReviewUrlController

    @Inject
    lateinit var apiService: SNUTTRestApi

    @Inject
    lateinit var snuttUrls: SnuttUrls

    private lateinit var binding: FragmentSearchBinding

    private lateinit var tagAdapter: TagAdapter

    private lateinit var searchResultAdapter: SearchResultAdapter

    private val bottomSheet = SearchOptionFragment()

    private val LECTURE_TIME_OVERLAP = 0x300C

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tagAdapter = TagAdapter { searchViewModel.toggleTag(it) }

        searchResultAdapter = SearchResultAdapter(
            onSelectLecture = {
                searchViewModel.toggleLectureSelection(it)
                hideSoftKeyboard()
            },
            onToggleAddition = {
                selectedTimetableViewModel.toggleLecture(it, false)
                    .bindUi(
                        this,
                        onError = { error ->
                            when (error) {
                                is ErrorParsedHttpException -> {
                                    if (error.errorDTO?.code == LECTURE_TIME_OVERLAP) {
                                        overwriteLectureDialog(it, error.errorDTO.ext!!["confirm_message"])
                                    } else apiOnError
                                }
                                else -> apiOnError
                            }
                        },
                        onComplete = {
                            searchViewModel.toggleLectureSelection(it)
                            hideSoftKeyboard()
                        }
                    )
            },
            onShowSyllabus = {
                searchViewModel.getCourseBookUrl(it)
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
                apiService.getLecturesId(
                    it.course_number ?: "",
                    it.instructor
                )
                    .bindUi(
                        this,
                        onSuccess = { result ->
                            homePagerController.update(HomePage.Review)
                            reviewUrlController.update(snuttUrls.getReviewDetail(result.id))
                        }, onError = apiOnError
                    )
            },
        )

        binding.tagList.adapter = tagAdapter
        binding.lectureList.adapter = searchResultAdapter

        searchViewModel.searchTags.asObservable()
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
                hideSoftKeyboard()
            }

        binding.filterButton.clicks()
            .bindUi(this) {
                if(bottomSheet.isAdded.not()) bottomSheet.show(parentFragmentManager, "tag_selector")
            }

        binding.clearButton.clicks()
            .bindUi(this) {
                binding.textEdit.text?.clear()
                binding.textEdit.clearFocus()
                searchViewModel.setTitle("")
                hideSoftKeyboard()
            }

        binding.textEdit.focusChanges()
            .bindUi(this) { hasFocus ->
                binding.clearButton.visibility = if (hasFocus) View.VISIBLE else View.INVISIBLE
                binding.filterButton.visibility = if (hasFocus) View.INVISIBLE else View.VISIBLE
            }

        binding.textEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchViewModel.setTitle(binding.textEdit.text.toString())
                searchViewModel.refreshQuery()
                hideSoftKeyboard()
                true
            } else {
                false
            }
        }

        searchResultAdapter.loadingState()
            .bindUi(this) {
                if (it.refresh is LoadState.NotLoading && it.append.endOfPaginationReached.not() && searchResultAdapter.itemCount < 1) {
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

        Observable.merge(
            binding.placeholder.searchIcon.throttledClicks(),
            binding.empty.searchIcon.throttledClicks()
        )
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

        selectedTimetableViewModel.lastViewedTable.asObservable().filterEmpty()
            .distinctUntilChanged()
            .bindUi(this) {
                binding.timetable.theme = it.theme
                binding.timetable.lectures = it.lectureList
            }

        selectedTimetableViewModel.trimParam.asObservable()
            .distinctUntilChanged()
            .bindUi(this) {
                binding.timetable.trimParam = it.copy(forceFitLectures = true)
            }
    }

    private fun overwriteLectureDialog(dto: LectureDto, message: String?) {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("시간대 겹침")
            .setMessage(message)
            .setPositiveButton("확인") { _, _ ->
                selectedTimetableViewModel.toggleLecture(dto, true)
                    .bindUi(
                        this,
                        onError = apiOnError,
                        onComplete = {
                            searchViewModel.toggleLectureSelection(dto)
                            hideSoftKeyboard()
                        }
                    )
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        val dialog = alert.create()
        dialog.show()
    }
}
