package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepository
import com.wafflestudio.snutt2.lib.*
import com.wafflestudio.snutt2.lib.data.DataProvider
import com.wafflestudio.snutt2.lib.data.SubjectDataValue
import com.wafflestudio.snutt2.lib.network.dto.GetCoursebooksOfficialResults
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.rx3.asFlow
import kotlinx.coroutines.rx3.asObservable
import kotlinx.coroutines.rx3.rxSingle
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val myLectureRepository: MyLectureRepository,
    private val lectureSearchRepository: LectureSearchRepository
) : ViewModel() {
    private val _searchTitle = SubjectDataValue("")
    private val _searchTags = SubjectDataValue<List<TagDto>>(listOf())
    private val _selectedLecture =
        SubjectDataValue<Optional<LectureDto>>(Optional.empty())
    private val _queryRefreshSignal = PublishSubject.create<Unit>()
    private val _selectedTagType = SubjectDataValue(TagType.ACADEMIC_YEAR)

    private var semesterChangeDisposable: Disposable? = null

    init {
        semesterChangeDisposable = myLectureRepository.currentTable.map { it.id }
            .distinctUntilChanged()
            .subscribe {
                clear()
            }
    }

    override fun onCleared() {
        super.onCleared()
        semesterChangeDisposable?.dispose()
    }

    private val currentYear get() = myLectureRepository.lastViewedTable.get().value?.year!!
    private val currentSemester get() = myLectureRepository.lastViewedTable.get().value?.semester!!

    val searchTags: DataProvider<List<TagDto>> = _searchTags

    val tagsByTagType: Observable<List<Selectable<TagDto>>> =
        Observable.combineLatest(
            rxSingle {
                lectureSearchRepository.getSearchTags(
                    currentYear,
                    currentSemester
                )
            }
                .map { it + listOf(TagDto.ETC_EMPTY, TagDto.ETC_ENG, TagDto.ETC_MILITARY) }
                .toObservable(),
            _selectedTagType.asObservable(),
            _searchTags.asObservable()
        ) { tags, tagType, selected ->
            tags.filter { it.type == tagType }.map { it.toDataWithState(selected.contains(it)) }
        }

    val selectedTagType: Observable<TagType> = _selectedTagType.asObservable()

    val selectedLecture: Observable<Optional<LectureDto>> = _selectedLecture.asObservable()

    val queryResults: Flow<PagingData<DataWithState<LectureDto, LectureState>>> =
        Observable.combineLatest(
            _queryRefreshSignal.hide()
                .switchMap {
                    lectureSearchRepository.getLectureSearchResultStream(
                        myLectureRepository.lastViewedTable.get().value?.year!!,
                        myLectureRepository.lastViewedTable.get().value?.semester!!,
                        _searchTitle.get(),
                        _searchTags.get(),
                        myLectureRepository.lastViewedTable.get().get()?.lectureList?.getClassTimeMask()
                    ).cachedIn(viewModelScope).asObservable()
                },
            _selectedLecture.asObservable(),
            myLectureRepository.currentTable.map { it.lectureList }
        ) { list, selected, alreadyContainingLectures ->
            list.map {
                it.toDataWithState(
                    LectureState(
                        selected = selected.get() == it,
                        contained = alreadyContainingLectures.any { lec ->
                            lec.isLectureNumberEquals(
                                it
                            )
                        }
                    )
                )
            }
        }.asFlow()

    fun setTitle(title: String) {
        _searchTitle.update(title)
    }

    fun setTagType(tagType: TagType) {
        _selectedTagType.update(tagType)
    }

    fun toggleLectureSelection(lecture: LectureDto) {
        if (_selectedLecture.get().value == lecture) {
            _selectedLecture.update(Optional.empty())
        } else {
            _selectedLecture.update(lecture.toOptional())
        }
    }

    fun toggleTag(tag: TagDto) {
        _searchTags.update(
            if (_searchTags.get().contains(tag)) {
                _searchTags.get().filter { it != tag }
            } else {
                concatenate(_searchTags.get(), listOf(tag))
            }
        )
    }

    fun clear() {
        _searchTags.update(listOf())
        _searchTitle.update("")
        _selectedLecture.update(Optional.empty())
        _queryRefreshSignal.onNext(Unit)
    }

    fun refreshQuery() {
        _queryRefreshSignal.onNext(Unit)
    }

    fun getCourseBookUrl(lecture: LectureDto): Single<GetCoursebooksOfficialResults> {
        return myLectureRepository.getLectureCourseBookUrl(
            lecture.course_number!!,
            lecture.lecture_number!!
        )
    }
}
