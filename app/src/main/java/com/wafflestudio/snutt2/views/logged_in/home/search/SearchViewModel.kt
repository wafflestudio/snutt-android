package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepository
import com.wafflestudio.snutt2.lib.*
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val lectureSearchRepository: LectureSearchRepository,
) : ViewModel() {

    var lazyListState = LazyListState(0, 0)

    private val _searchTitle = MutableStateFlow("")
    val searchTitle = _searchTitle.asStateFlow()

    private val _selectedLecture = MutableStateFlow<LectureDto?>(null)
    val selectedLecture = _selectedLecture.asStateFlow()

    private val _selectedTagType = MutableStateFlow(TagType.ACADEMIC_YEAR)
    val selectedTagType = _selectedTagType.asStateFlow()

    private val _selectedTags = MutableStateFlow(listOf<TagDto>())
    val selectedTags = _selectedTags.asStateFlow()

    private val _searchTagList = MutableStateFlow(listOf<TagDto>())

    private val currentTable = currentTableRepository.currentTable

    private val semesterChange =
        currentTable
            .filterNotNull()
            .map { it.year * 10 + it.semester } // .distinctUntilChanged()

    private val _querySignal = MutableSharedFlow<Unit>(replay = 0)
    private val _getBookmarkListSignal = MutableSharedFlow<Unit>(replay = 0)

    private val _placeHolderState = MutableStateFlow(true)
    val placeHolderState = _placeHolderState.asStateFlow()

    init {
        viewModelScope.launch {
            semesterChange.distinctUntilChanged().collectLatest {
                clear()
                _placeHolderState.emit(true)
                try {
                    fetchSearchTagList() // FIXME: 학기가 바뀔 때마다 불러주는 것으로 되어 있는데, 여기서 apiOnError 붙이기?
                    getBookmarkList()
                } catch (e: Exception) { }
            }
        }
    }

    private val etcTags = listOf(TagDto.ETC_EMPTY, TagDto.ETC_ENG, TagDto.ETC_MILITARY)

    val tagsByTagType: StateFlow<List<Selectable<TagDto>>> = combine(
        _searchTagList, _selectedTagType, _selectedTags,
    ) { tags, selectedTagType, selectedTags ->
        (tags + etcTags).filter { it.type == selectedTagType }
            .map { it.toDataWithState(selectedTags.contains(it)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val bookmarkList = combine(
        _getBookmarkListSignal.flatMapLatest {
            try {
                flowOf(currentTableRepository.getBookmarks())
            } catch (e: Exception) {
                flowOf(emptyList())
            }
        },
        _selectedLecture,
        currentTable.filterNotNull(),
    ) { bookmarks, selectedLecture, currentTable ->
        bookmarks.map { bookmarkedLecture ->
            bookmarkedLecture.toDataWithState(
                LectureState(
                    selected = selectedLecture == bookmarkedLecture,
                    contained = currentTable.lectureList.any { lectureOfCurrentTable ->
                        lectureOfCurrentTable.isLectureNumberEquals(bookmarkedLecture)
                    },
                ),
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList(),
    )

    val queryResults = combine(
        _querySignal.flatMapLatest {
            val currentTable = currentTable.filterNotNull().first()
            lectureSearchRepository.getLectureSearchResultStream(
                year = currentTable.year,
                semester = currentTable.semester,
                title = _searchTitle.value,
                tags = _selectedTags.value,
                times = null, // TODO: 시간대 검색
                timesToExclude = if (_selectedTags.value.contains(TagDto.ETC_EMPTY)) currentTable.lectureList.flatMapToSearchTimeDto() else null,
            ).cachedIn(viewModelScope)
        },
        _selectedLecture, currentTable.filterNotNull(),
    ) { pagingData, selectedLecture, currentTable ->
        pagingData.map { searchedLecture ->
            searchedLecture.toDataWithState(
                LectureState(
                    selected = selectedLecture == searchedLecture,
                    contained = currentTable.lectureList.any { lectureOfCurrentTable ->
                        lectureOfCurrentTable.isLectureNumberEquals(searchedLecture)
                    },
                ),
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        PagingData.empty(),
    )

    suspend fun setTitle(title: String) {
        _searchTitle.emit(title)
    }

    suspend fun setTagType(tagType: TagType) {
        _selectedTagType.emit(tagType)
    }

    suspend fun toggleLectureSelection(lecture: LectureDto) {
        if (lecture == _selectedLecture.value) {
            _selectedLecture.emit(null)
        } else {
            _selectedLecture.emit(lecture)
        }
    }

    suspend fun toggleTag(tag: TagDto) {
        _selectedTags.emit(
            if (_selectedTags.value.contains(tag)) {
                _selectedTags.value.filter { it != tag }
            } else {
                concatenate(_selectedTags.value, listOf(tag))
            },
        )
    }

    suspend fun query() {
        _querySignal.emit(Unit)
        _placeHolderState.emit(false)
        lazyListState = LazyListState(0, 0)
    }

    suspend fun getBookmarkList() {
        _getBookmarkListSignal.emit(Unit)
    }

    suspend fun clearEditText() {
        _searchTitle.emit("")
    }

    suspend fun getLectureReviewUrl(lecture: LectureDto): String? {
        return lectureSearchRepository.getLectureReviewUrl(
            courseNumber = lecture.course_number ?: return null,
            instructor = lecture.instructor,
        )
    }

    suspend fun addBookmark(lecture: LectureDto) {
        currentTableRepository.addBookmark(lecture)
        getBookmarkList()
    }

    suspend fun deleteBookmark(lecture: LectureDto) {
        currentTableRepository.deleteBookmark(lecture)
        getBookmarkList()
    }

    private suspend fun clear() {
        _searchTitle.emit("")
        _selectedLecture.emit(null)
        _searchTagList.emit(emptyList())
        _selectedTags.emit(emptyList())
        lazyListState = LazyListState(0, 0)
        /* TODO
         * 기존 구현은 보고 있는 학기가 바뀔 때 query signal 을 줘서 검색어 "" 로 재검색을 하게 한다.
         * 하지만 검색을 안 한 상태(loadState 가 NotLoading 인 상태)로 되돌리는 게 더 낫지 않을까?
         */
    }

    private suspend fun fetchSearchTagList() {
        val currentTable = currentTable.filterNotNull().first()
        _searchTagList.emit(
            lectureSearchRepository.getSearchTags(
                currentTable.year, currentTable.semester,
            ),
        )
    }
}
