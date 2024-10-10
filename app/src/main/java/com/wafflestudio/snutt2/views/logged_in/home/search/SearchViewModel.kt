package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepository
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.concatenate
import com.wafflestudio.snutt2.lib.flatMapToSearchTimeDto
import com.wafflestudio.snutt2.lib.isLectureNumberEquals
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.model.SearchTimeDto
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.views.logged_in.home.search.bookmark.SearchPageMode
import com.wafflestudio.snutt2.views.logged_in.home.search.search_option.clusterToTimeBlocks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val lectureSearchRepository: LectureSearchRepository,
    private val apiOnError: ApiOnError,
) : ViewModel() {

    var lazyListState = LazyListState(0, 0)

    private val _searchTitle = MutableStateFlow("")
    val searchTitle = _searchTitle.asStateFlow()

    private val _selectedLecture = MutableStateFlow<LectureDto?>(null)
    val selectedLecture = _selectedLecture.asStateFlow()

    private val _selectedTagType = MutableStateFlow<TagType>(TagType.SORT_CRITERIA)
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

    private val _pageMode = MutableStateFlow(SearchPageMode.Search)
    val pageMode: StateFlow<SearchPageMode> get() = _pageMode

    // 드래그하고 확정한 시간대 검색 격자 (월~금 5칸, 8시~22시 30분 간격 28칸)
    private val _draggedTimeBlock =
        MutableStateFlow(TableTrimParam.TimeBlockGridDefault)
    val draggedTimeBlock = _draggedTimeBlock.asStateFlow()

    // 검색 쿼리에 들어가는 시간대 검색 시간대 목록
    private val _searchTimeList = MutableStateFlow<List<SearchTimeDto>?>(null)

    val recentSearchedDepartments: StateFlow<List<TagDto>> = lectureSearchRepository.recentSearchedDepartments

    init {
        viewModelScope.launch {
            semesterChange.distinctUntilChanged().collectLatest {
                clear()
                _placeHolderState.emit(true)
                try {
                    fetchSearchTagList()
                    getBookmarkList()
                } catch (e: Exception) {
                    apiOnError(e)
                }
            }
        }
    }

    private val timeTags = listOf(TagDto.TIME_EMPTY, TagDto.TIME_SELECT)
    private val etcTags = listOf(TagDto.ETC_ENG, TagDto.ETC_MILITARY)

    val tagsByTagType: StateFlow<List<Selectable<TagDto>>> = combine(
        _searchTagList, _selectedTagType, _selectedTags,
    ) { tags, selectedTagType, selectedTags ->
        (tags + etcTags + timeTags).filter { it.type == selectedTagType }
            .map { it.toDataWithState(selectedTags.contains(it)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val selectableRecentSearchedDepartments = combine(
        tagsByTagType,
        recentSearchedDepartments,
    ) { tags, recentDepartments ->
        tags.filter { tag ->
            tag.item.type == TagType.DEPARTMENT && recentDepartments.contains(tag.item) // 사실 후자를 만족하려면 전자를 만족할 수 밖에 없긴 하다.
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList(),
    )

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
                times = _searchTimeList.value,
                timesToExclude = if (_selectedTags.value.contains(TagDto.TIME_EMPTY)) currentTable.lectureList.flatMapToSearchTimeDto() else null,
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
        if (_selectedTags.value.contains(tag)) {
            _selectedTags.emit(_selectedTags.value.filter { it != tag })

            if (tag == TagDto.TIME_SELECT) {
                _searchTimeList.emit(null)
            }
        } else {
            val selectedTags = if (tag.type.isExclusive) {
                concatenate(_selectedTags.value.filter { it.type != tag.type }, listOf(tag))
            } else {
                concatenate(_selectedTags.value, listOf(tag))
            }
            _selectedTags.emit(selectedTags)

            if (tag == TagDto.TIME_SELECT) {
                _draggedTimeBlock.value.clusterToTimeBlocks().let {
                    if (it.isEmpty()) {
                        _searchTimeList.emit(null)
                    } else {
                        _searchTimeList.emit(it)
                    }
                }
            }
        }
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

    suspend fun addBookmark(lecture: LectureDto) {
        currentTableRepository.addBookmark(lecture)
        getBookmarkList()
    }

    suspend fun deleteBookmark(lecture: LectureDto) {
        currentTableRepository.deleteBookmark(lecture)
        getBookmarkList()
    }

    suspend fun getBookmarkLecture(year: Long, semester: Long, lectureId: String): LectureDto? {
        return currentTableRepository.getBookmarksOfSemester(year, semester).find {
            it.id == lectureId
        }
    }

    suspend fun setDraggedTimeBlock(draggedTimeBlock: List<List<Boolean>>) {
        _draggedTimeBlock.emit(draggedTimeBlock)
        draggedTimeBlock.clusterToTimeBlocks().let {
            if (it.isEmpty()) {
                _searchTimeList.emit(null)
            } else {
                _searchTimeList.emit(it)
            }
        }
    }

    fun storeRecentSearchedDepartments() {
        _selectedTags.value.filter { it.type == TagType.DEPARTMENT }.forEach { tag ->
            lectureSearchRepository.storeRecentSearchedDepartment(tag)
        }
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

    fun togglePageMode() {
        _pageMode.value = _pageMode.value.toggled()
    }
}
