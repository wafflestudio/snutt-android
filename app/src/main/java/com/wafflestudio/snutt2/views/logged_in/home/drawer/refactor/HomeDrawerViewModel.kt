package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.lib.toggleIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeDrawerViewModel @Inject constructor(
    private val courseBookRepository: CourseBookRepository,
    private val tableRepository: TableRepository,
    private val currentTableRepository: CurrentTableRepository,
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<HomeDrawerUiEvent>(1)
    private val _uiState =
        MutableStateFlow(
            HomeDrawerUiState(
                courseBookDrawerItemList = emptyList(),
                selectedTable = currentTableRepository.currentTable.value?.let {
                    Table.fromTableDto(it).summary
                },
                homeDrawerBottomSheetType = HomeDrawerBottomSheetType.Hidden
            )
        )

    val uiEvent = _uiEvent.asSharedFlow()
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            courseBookRepository.getCourseBookNew()
                .onFailure {
                    // TODO
                    return@launch
                }
                // 콜백지옥인데..
                .onSuccess { coursebookList ->
                    tableRepository.tableSummaryList.collect { tableSummaryList ->
                        // 유저의 전체 시간표 목록을 학기 별로 그룹핑 한 뒤 학기 순으로 정렬
                        val tableSummariesOfEachCourseBook = tableSummaryList.groupBy {
                            it.courseBook
                        }.toMutableMap()

                        // 가장 최신 학기는 생성된 시간표가 없어도 서랍에서 보여주기
                        val mostRecentCourseBook = coursebookList.first()
                        if (tableSummariesOfEachCourseBook.containsKey(mostRecentCourseBook)
                                .not()
                        ) {
                            tableSummariesOfEachCourseBook[mostRecentCourseBook] = emptyList()
                        }

                        // 기존 각 학기의 펼침 상태 map
                        val courseBookDrawerItemListMap =
                            _uiState.value.courseBookDrawerItemList.associate { (item, expanded) ->
                                item.courseBook to expanded
                            }

                        _uiState.value = _uiState.value.copy(
                            courseBookDrawerItemList = tableSummariesOfEachCourseBook
                                .toList()
                                .sortedBy { (coursebook, _) -> coursebook }
                                .map { (courseBook, tableSummaries) ->
                                    CoursebookDrawerItem(
                                        courseBook = courseBook,
                                        showNewCoursebookDot = (courseBook == mostRecentCourseBook) && tableSummaries.isEmpty(),
                                        tableList = tableSummaries
                                    ).toDataWithState(
                                        courseBookDrawerItemListMap[courseBook] ?: false
                                    )
                                }
                        )
                    }
                }
        }
        viewModelScope.launch {
            currentTableRepository.currentTable.collect { currentTable ->
                _uiState.value = _uiState.value.copy(
                    selectedTable = currentTable?.let { Table.fromTableDto(it).summary }
                )
            }
        }
    }

    fun toggleCourseBookDrawerItem(index: Int) {
        val state = _uiState.value
        _uiState.value = state.copy(
            courseBookDrawerItemList = state.courseBookDrawerItemList.toggleIndex(index)
        )
    }

    fun openCreateNewTableBottomSheet() {
        _uiState.value = _uiState.value.copy(
            homeDrawerBottomSheetType = HomeDrawerBottomSheetType.NewTable
        )
        viewModelScope.launch {
            _uiEvent.emit(HomeDrawerUiEvent.OpenBottomSheet)
        }
    }

    fun openCreateNewTableOfCourseBookBottomSheet(
        courseBook: CourseBook
    ) {
        _uiState.value = _uiState.value.copy(
            homeDrawerBottomSheetType = HomeDrawerBottomSheetType.NewTableOfCourseBook(courseBook)
        )
        viewModelScope.launch {
            _uiEvent.emit(HomeDrawerUiEvent.OpenBottomSheet)
        }
    }

    fun selectTable(tableId: String) {
        viewModelScope.launch {
            // 이걸 불러야 하는 게 참 암묵적이다
            // TODO: data layer 리팩토링 + 에러 처리
            tableRepository.fetchTableById(tableId)
            _uiEvent.emit(HomeDrawerUiEvent.CloseDrawer)
        }
    }

    fun copyTable(tableId: String) {
        viewModelScope.launch {
            // TODO: 에러 처리
            tableRepository.copyTable(tableId)
        }
    }

    fun openMoreActionBottomSheet(tableSummary: TableSummary) {
        _uiState.value = _uiState.value.copy(
            homeDrawerBottomSheetType = HomeDrawerBottomSheetType.MoreAction(tableSummary)
        )
        viewModelScope.launch {
            _uiEvent.emit(HomeDrawerUiEvent.OpenBottomSheet)
        }
    }
}

sealed interface HomeDrawerUiEvent {
    data object OpenBottomSheet : HomeDrawerUiEvent
    data object CloseDrawer : HomeDrawerUiEvent
}

data class HomeDrawerUiState(
    val courseBookDrawerItemList: List<Selectable<CoursebookDrawerItem>>,
    val selectedTable: TableSummary?,
    val homeDrawerBottomSheetType: HomeDrawerBottomSheetType
)

// 이렇게 uiState 용 data class 를 만드는 건 어떨까? 위치는?
data class CoursebookDrawerItem(
    val courseBook: CourseBook,
    val showNewCoursebookDot: Boolean,
    val tableList: List<TableSummary>
)

sealed class HomeDrawerBottomSheetType {
    data object Hidden : HomeDrawerBottomSheetType()
    data object SelectTheme : HomeDrawerBottomSheetType()
    data object CreateNewTheme : HomeDrawerBottomSheetType()
    data object NewTable : HomeDrawerBottomSheetType()
    data class NewTableOfCourseBook(
        val courseBook: CourseBook
    ) : HomeDrawerBottomSheetType()

    data class MoreAction(
        val tableSummary: TableSummary
    ) : HomeDrawerBottomSheetType()
}

