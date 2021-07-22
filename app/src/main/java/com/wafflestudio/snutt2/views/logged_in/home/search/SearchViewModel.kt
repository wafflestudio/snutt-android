package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.cachedIn
import androidx.paging.rxjava3.observable
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.data.SearchLectureRepository
import com.wafflestudio.snutt2.data.TagRepository
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.*
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchLectureRepository: SearchLectureRepository,
    private val myLectureRepository: MyLectureRepository,
    private val tagRepository: TagRepository,
    private val apiOnError: ApiOnError
) : ViewModel() {
    private val _searchTitle = BehaviorSubject.createDefault("")
    private val _searchTags = BehaviorSubject.createDefault<List<TagDto>>(listOf())
    private val _selectedLecture =
        BehaviorSubject.createDefault<Optional<LectureDto>>(Optional.empty())
    private val _queryRefreshSignal = BehaviorSubject.create<Unit>()
    private val _selectedTagType = BehaviorSubject.createDefault(TagType.ACADEMIC_YEAR)

    val searchTags: Observable<List<TagDto>> = _searchTags.hide()

    val tagsByTagType: Observable<List<Selectable<TagDto>>> =
        Observable.combineLatest(
            tagRepository.tags,
            _selectedTagType.hide(),
            _searchTags.hide()
        ) { tags, tagType, selected ->
            tags.filter { it.type == tagType }.map { it.toDataWithState(selected.contains(it)) }
        }

    val selectedTagType: Observable<TagType> = _selectedTagType.hide()

    val selectedLecture: Observable<Optional<LectureDto>> = _selectedLecture.hide()

    val queryResults: Observable<PagingData<DataWithState<LectureDto, LectureState>>> =
        Observable.combineLatest(
            _queryRefreshSignal.hide()
                .switchMap {
                    searchLectureRepository.getPagingSource(
                        _searchTitle.value,
                        _searchTags.value
                    ).observable.cachedIn(viewModelScope)
                },
            _selectedLecture.hide(),
            myLectureRepository.currentTable.map { it.lectureList }
        ) { list, selected, containeds ->
            list.map {
                if (containeds.contains(it)) {
                    Timber.d(it.toString())
                }
                it.toDataWithState(
                    LectureState(
                        selected = selected.get() == it,
                        contained = containeds.any { lec -> lec.isRegularlyEquals(it) }
                    )
                )
            }
        }

    fun setTitle(title: String) {
        _searchTitle.onNext(title)
    }

    fun setTagType(tagType: TagType) {
        _selectedTagType.onNext(tagType)
    }

    fun toggleLectureSelection(lecture: LectureDto) {
        if (_selectedLecture.value.get() == lecture) {
            _selectedLecture.onNext(Optional.empty())
        } else {
            _selectedLecture.onNext(lecture.toOptional())
        }
    }

    fun toggleTag(tag: TagDto) {
        _searchTags.onNext(
            if (_searchTags.value.contains(tag)) {
                _searchTags.value.filter { it != tag }
            } else {
                concatenate(_searchTags.value, listOf(tag))
            }
        )
    }

    fun refreshQuery() {
        _queryRefreshSignal.onNext(Unit)
    }

}
