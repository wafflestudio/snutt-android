package com.wafflestudio.snutt2.data.lecture_search

import androidx.compose.foundation.lazy.LazyListState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.lib.*
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModelNew @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val lectureSearchRepository: LectureSearchRepository
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

    private val currentTable = currentTableRepository.currentTable.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), Defaults.defaultTableDto
    )

    private val semesterChange =
        currentTable.map { it.year * 10 + it.semester } // .distinctUntilChanged()

    private val _querySignal = MutableSharedFlow<Unit>(replay = 0)

    init {
        viewModelScope.launch {
            semesterChange.distinctUntilChanged().collectLatest {
                clear()
                try {
                    fetchSearchTagList()  // FIXME: 학기가 바뀔 때마다 불러주는 것으로 되어 있는데, 여기서 apiOnError 붙이기?
                } catch (e: Exception) { }
            }
        }
    }

    private val etcTags = listOf(TagDto.ETC_EMPTY, TagDto.ETC_ENG, TagDto.ETC_MILITARY)

    val tagsByTagType: StateFlow<List<Selectable<TagDto>>> = combine(
        _searchTagList, _selectedTagType, _selectedTags
    ) { tags, selectedTagType, selectedTags ->
        (tags + etcTags).filter { it.type == selectedTagType }
            .map { it.toDataWithState(selectedTags.contains(it)) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val queryResults = combine(
        _querySignal.flatMapLatest {
            lectureSearchRepository.getLectureSearchResultStream(
                year = currentTable.value.year,
                semester = currentTable.value.semester,
                title = _searchTitle.value,
                tags = _selectedTags.value,
                lecturesMask = currentTable.value.lectureList.getClassTimeMask()
            ).cachedIn(viewModelScope)
        },
        _selectedLecture, currentTable
    ) { pagingData, selectedLecture, currentTable ->
        pagingData.map { searchedLecture ->
            searchedLecture.toDataWithState(
                LectureStateNew(
                    selected = selectedLecture == searchedLecture,
                    contained = currentTable.lectureList.any { lectureOfCurrentTable ->
                        lectureOfCurrentTable.isLectureNumberEquals(searchedLecture)
                    }
                )
            )
        }
    }

    suspend fun setTitle(title: String) {
        _searchTitle.emit(title)
    }

    suspend fun setTagType(tagType: TagType) {
        _selectedTagType.emit(tagType)
    }

    suspend fun toggleLectureSelection(lecture: LectureDto) {
        if (lecture == _selectedLecture.value) _selectedLecture.emit(null)
        else _selectedLecture.emit(lecture)
    }

    suspend fun toggleTag(tag: TagDto) {
        _selectedTags.emit(
            if (_selectedTags.value.contains(tag)) _selectedTags.value.filter { it != tag }
            else concatenate(_selectedTags.value, listOf(tag))
        )
    }

    suspend fun query() {
        _querySignal.emit(Unit)
        lazyListState = LazyListState(0, 0)
    }

    suspend fun clearEditText() {
        _searchTitle.emit("")
    }

    private suspend fun clear() {
        _searchTitle.emit("")
        _selectedLecture.emit(null)
        _searchTagList.emit(emptyList())
        lazyListState = LazyListState(0, 0)
        /* TODO
         * 기존 구현은 보고 있는 학기가 바뀔 때 query signal 을 줘서 검색어 "" 로 재검색을 하게 한다.
         * 하지만 검색을 안 한 상태(loadState 가 NotLoading 인 상태)로 되돌리는 게 더 낫지 않을까?
         */
    }

    suspend fun fetchSearchTagList() {
        _searchTagList.emit(
            lectureSearchRepository.getSearchTags(
                currentTable.value.year, currentTable.value.semester
            )
        )
    }
}

data class LectureStateNew(
    val selected: Boolean,
    val contained: Boolean
)
