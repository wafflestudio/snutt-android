package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.NotSelectedTimetable
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.lib.toDataWithState
import com.wafflestudio.snutt2.lib.toggleIndex
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeDrawerViewModel @Inject constructor(
    private val courseBookRepository: CourseBookRepository,
    private val tableRepository: TableRepository,
    private val currentTableRepository: CurrentTableRepository,
    private val themeRepository: ThemeRepository,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<HomeDrawerUiEvent>()
    private val _uiState = MutableStateFlow<HomeDrawerUiState>(HomeDrawerUiState.Loading)

    val uiEvent = _uiEvent.asSharedFlow()
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // TODO: 진짜 필요한건지 레거시코드 지우고 확인하기
            tableRepository.getTableList()
        }

        viewModelScope.launch {
            courseBookRepository.getCourseBookNew()
                .onFailure {
                    handleError(it)
                    return@launch
                }
                // 콜백지옥인데..
                .onSuccess { coursebookList ->
                    combine(
                        tableRepository.tableSummaryList.filter { it.isNotEmpty() },
                        currentTableRepository.currentTableRefactored.filterNotNull(),
                    ) { tableSummaryList, currentTable ->
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

                        _uiState.update { state ->
                            // 기존 각 학기의 펼침 상태 map
                            val courseBookDrawerItemListMap =
                                when (state) {
                                    is HomeDrawerUiState.Loading -> emptyMap()
                                    is HomeDrawerUiState.Loaded -> state.courseBookDrawerItemList.associate { (item, expanded) ->
                                        item.courseBook to expanded
                                    }
                                }

                            val courseBookDrawerItemList = tableSummariesOfEachCourseBook
                                .toList()
                                .sortedBy { (coursebook, _) -> coursebook }
                                .map { (courseBook, tableSummaries) ->
                                    CoursebookDrawerItem(
                                        courseBook = courseBook,
                                        showNewCoursebookDot = (courseBook == mostRecentCourseBook) && tableSummaries.isEmpty(),
                                        tableList = tableSummaries,
                                    ).toDataWithState(
                                        // FIXME: 로직 정리
                                        currentTable.summary.courseBook == courseBook ||
                                            courseBookDrawerItemListMap[courseBook] ?: false,
                                    )
                                }
                            when (state) {
                                is HomeDrawerUiState.Loaded -> state.copy(
                                    courseBookDrawerItemList = courseBookDrawerItemList,
                                    selectedTable = currentTable.summary,
                                )

                                is HomeDrawerUiState.Loading -> HomeDrawerUiState.Loaded(
                                    courseBookDrawerItemList = courseBookDrawerItemList,
                                    selectedTable = currentTable.summary,
                                    homeDrawerBottomSheetType = HomeDrawerBottomSheetType.Empty,
                                    dialogState = HomeDrawerUiState.DialogState.None,
                                )
                            }
                        }
                    }.collect()
                }
        }
    }

    fun toggleCourseBookDrawerItem(index: Int) {
        _uiState.update { state ->
            when (state) {
                is HomeDrawerUiState.Loaded -> state.copy(
                    courseBookDrawerItemList = state.courseBookDrawerItemList.toggleIndex(index),
                )

                else -> state
            }
        }
    }

    fun openCreateNewTableSheet() {
        viewModelScope.launch {
            // FIXME: 이거 매번 이렇게 가져와?
            // TODO: 에러 처리
            courseBookRepository.getCourseBookNew().onSuccess { allCourseBook ->
                _uiState.update { state ->
                    when (state) {
                        is HomeDrawerUiState.Loaded -> state.copy(
                            homeDrawerBottomSheetType = HomeDrawerBottomSheetType.CreateNewTable.SelectCourseBook(
                                initialCourseBook = state.selectedTable.courseBook,
                                allCourseBook = allCourseBook,
                            ),
                        )

                        else -> state
                    }
                }
                _uiEvent.emit(HomeDrawerUiEvent.OpenBottomSheet)
            }.onFailure {
                handleError(it)
            }
        }
    }

    fun openCreateNewTableOfSpecificCourseBookSheet(
        courseBook: CourseBook,
    ) {
        _uiState.update { state ->
            when (state) {
                is HomeDrawerUiState.Loaded -> state.copy(
                    homeDrawerBottomSheetType = HomeDrawerBottomSheetType.CreateNewTable.SpecificCourseBook(
                        courseBook = courseBook,
                    ),
                )

                else -> state
            }
        }
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
            tableRepository.copyTableNew(tableId).onFailure {
                handleError(it)
            }
        }
    }

    fun openMoreActionBottomSheet(tableSummary: TableSummary) {
        _uiState.update { state ->
            when (state) {
                is HomeDrawerUiState.Loaded -> state.copy(
                    homeDrawerBottomSheetType = HomeDrawerBottomSheetType.MoreAction(tableSummary),
                )

                else -> state
            }
        }
        viewModelScope.launch {
            _uiEvent.emit(HomeDrawerUiEvent.OpenBottomSheet)
        }
    }

    fun createNewTable(courseBook: CourseBook, title: String) {
        viewModelScope.launch {
            tableRepository.createTableNew(
                courseBook = courseBook,
                title = title,
            ).onFailure {
                handleError(it)
            }.onSuccess {
                _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
                _uiState.update { state ->
                    when (state) {
                        is HomeDrawerUiState.Loaded -> state.copy(
                            homeDrawerBottomSheetType = HomeDrawerBottomSheetType.Empty,
                        )

                        else -> state
                    }
                }
                _uiEvent.emit(HomeDrawerUiEvent.CloseDrawer)
            }
        }
    }

    fun openChangeTableNameDialog(tableSummary: TableSummary) {
        _uiState.update { state ->
            when (state) {
                is HomeDrawerUiState.Loaded -> state.copy(
                    dialogState = HomeDrawerUiState.DialogState.ChangeTableName(tableSummary),
                )

                else -> state
            }
        }
    }

    fun setPrimaryTable(tableSummary: TableSummary) {
        viewModelScope.launch {
            tableRepository.setPrimaryTableNew(tableSummary.id)
                .onFailure {
                    handleError(it)
                }
                .onSuccess {
                    // FIXME: 구 동작 일단 옮겨오기. 이걸 해야, 상태가 변한다.
                    tableRepository.getTableList()
                    currentTableRepository.updateCurrentTable()
                    _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
                }
        }
    }

    fun unsetPrimaryTable(tableSummary: TableSummary) {
        viewModelScope.launch {
            tableRepository.unsetPrimaryTableNew(tableSummary.id)
                .onFailure {
                    handleError(it)
                }
                .onSuccess {
                    // FIXME: 구 동작 일단 옮겨오기. 이걸 해야, 상태가 변한다.
                    tableRepository.getTableList()
                    currentTableRepository.updateCurrentTable()
                    _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
                }
        }
    }

    fun onClickSetThemeSheet(tableSummary: TableSummary) {
        viewModelScope.launch {
            val currentTable = currentTableRepository.currentTable.value
            if (currentTable?.id == tableSummary.id) {
                _uiEvent.emit(HomeDrawerUiEvent.CloseDrawer)
                openSelectThemeSheet()
            } else {
                handleError(NotSelectedTimetable)
            }
        }
    }

    fun openSelectThemeSheet() {
        viewModelScope.launch {
            val customThemes = themeRepository.customThemes.value
            val builtInThemes = themeRepository.builtInThemes.value
            // FIXME: 에러 처리하기. silent 하게 해도 될까?
            val selectedTheme = getCurrentTableThemeUseCase().first()

            _uiState.update { state ->
                when (state) {
                    is HomeDrawerUiState.Loaded -> state.copy(
                        homeDrawerBottomSheetType = HomeDrawerBottomSheetType.SelectTheme(
                            customThemes = customThemes,
                            builtInThemes = builtInThemes,
                            selectedPreviewTheme = selectedTheme,
                        ),
                    )

                    else -> state
                }
            }
            _uiEvent.emit(HomeDrawerUiEvent.OpenBottomSheet)
        }
    }

    fun setPreviewTheme(theme: TableTheme) {
        _uiState.update { state ->
            when (state) {
                is HomeDrawerUiState.Loaded -> when (state.homeDrawerBottomSheetType) {
                    is HomeDrawerBottomSheetType.SelectTheme -> state.copy(
                        homeDrawerBottomSheetType = state.homeDrawerBottomSheetType.copy(
                            selectedPreviewTheme = theme,
                        ),
                    )

                    else -> state
                }

                else -> state
            }
        }
    }

    fun applyTheme() {
        viewModelScope.launch {
            val currentTable = currentTableRepository.currentTable.value
            val previewTheme = (_uiState.value as? HomeDrawerUiState.Loaded)
                ?.homeDrawerBottomSheetType
                ?.let { it as? HomeDrawerBottomSheetType.SelectTheme }
                ?.selectedPreviewTheme
            if (currentTable != null && previewTheme != null) {
                when (previewTheme) {
                    is BuiltInTheme -> tableRepository.updateTableTheme(
                        currentTable.id,
                        previewTheme.code,
                    )

                    is CustomTheme -> tableRepository.updateTableTheme(
                        currentTable.id,
                        previewTheme.id,
                    )
                }
            }
            _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
        }
    }

    fun navigateToThemeDetail() {
        viewModelScope.launch {
            _uiEvent.emit(HomeDrawerUiEvent.NavigateToThemeDetail)
        }
    }

    fun onChangeSheetType(sheetType: HomeDrawerBottomSheetType) {
        _uiState.update { state ->
            when (state) {
                is HomeDrawerUiState.Loaded -> state.copy(
                    homeDrawerBottomSheetType = sheetType,
                )

                else -> state
            }
        }
    }

    fun openDeleteTableDialog(tableSummary: TableSummary) {
        _uiState.update { state ->
            when (state) {
                is HomeDrawerUiState.Loaded -> state.copy(
                    dialogState = HomeDrawerUiState.DialogState.DeleteTable(tableSummary),
                )

                else -> state
            }
        }
    }

    fun changeTableTitle(newTitle: String, tableId: String) {
        viewModelScope.launch {
            tableRepository.updateTableNameNew(newTitle, tableId)
                .onFailure {
                    handleError(it)
                }.onSuccess {
                    _uiState.update { state ->
                        when (state) {
                            is HomeDrawerUiState.Loaded -> state.copy(
                                dialogState = HomeDrawerUiState.DialogState.None,
                            )

                            else -> state
                        }
                    }
                    _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
                }
        }
    }

    fun deleteTable(tableSummary: TableSummary) {
        viewModelScope.launch {
            when (val state = _uiState.value) {
                is HomeDrawerUiState.Loaded -> {
                    val allTables = state.courseBookDrawerItemList.flatMap { it.item.tableList }
                    val sameCourseBookTables =
                        allTables.filter { it.courseBook == tableSummary.courseBook }
                    val indexInSameCourseBook =
                        sameCourseBookTables.indexOfFirst { it.id == tableSummary.id }
                    val indexInAll = allTables.indexOfFirst { it.id == tableSummary.id }

                    tableRepository.deleteTableNew(tableSummary.id)
                        .onFailure {
                            // TODO: 에러 처리
                            handleError(it)
                            // "하나 남은 시간표는 삭제할 수 없습니다" 는 클라로직 대신 서버로직으로 대체함
                            // 에러나면 dialog 숨기고 바텀시트도 닫고..
                        }
                        .onSuccess {
                            // 현재 시간표를 삭제한 경우, 다른 시간표로 전환
                            if (state.selectedTable.id == tableSummary.id) {
                                // 삭제 후 남은 같은 학기 시간표들
                                val remainingSameCourseBookTables =
                                    sameCourseBookTables.filter { it.id != tableSummary.id }

                                val nextTableId = if (remainingSameCourseBookTables.isEmpty()) {
                                    // 같은 학기에 남은 시간표가 없으면 전체에서 선택
                                    val remainingAllTables =
                                        allTables.filter { it.id != tableSummary.id }
                                    if (indexInAll == allTables.size) {
                                        remainingAllTables.last().id
                                    } else {
                                        remainingAllTables[indexInAll].id
                                    }
                                } else {
                                    // 같은 학기에 남은 시간표가 있으면 그 중에서 선택
                                    if (indexInSameCourseBook == sameCourseBookTables.size) {
                                        remainingSameCourseBookTables.last().id
                                    } else {
                                        remainingSameCourseBookTables[indexInSameCourseBook].id
                                    }
                                }
                                tableRepository.fetchTableById(nextTableId)
                            }

                            _uiState.update { s ->
                                when (s) {
                                    is HomeDrawerUiState.Loaded -> s.copy(
                                        dialogState = HomeDrawerUiState.DialogState.None,
                                    )

                                    else -> s
                                }
                            }
                            _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
                        }
                }

                else -> {}
            }
        }
    }

    fun dismissDialog() {
        _uiState.update { state ->
            when (state) {
                is HomeDrawerUiState.Loaded -> state.copy(
                    dialogState = HomeDrawerUiState.DialogState.None,
                )

                else -> state
            }
        }
    }

    private fun handleError(error: DomainError) { // TODO: 네트워크 오류일 때 재시도하기(지금은 앱 껐다 켜야 됨)
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        viewModelScope.launch {
            _uiEvent.emit(HomeDrawerUiEvent.ShowToast(displayMessage))
        }
    }
}

sealed interface HomeDrawerUiEvent {
    data object OpenBottomSheet : HomeDrawerUiEvent
    data object CloseBottomSheet : HomeDrawerUiEvent
    data class ChangeBottomSheet(
        val bottomSheetType: HomeDrawerBottomSheetType,
    ) : HomeDrawerUiEvent

    data object CloseDrawer : HomeDrawerUiEvent

    data object NavigateToThemeDetail : HomeDrawerUiEvent

    data class ShowToast(val displayMessage: String) : HomeDrawerUiEvent
}

sealed interface HomeDrawerUiState {
    data class Loaded(
        val courseBookDrawerItemList: List<Selectable<CoursebookDrawerItem>>,
        val selectedTable: TableSummary,
        val homeDrawerBottomSheetType: HomeDrawerBottomSheetType,
        val dialogState: DialogState,
    ) : HomeDrawerUiState

    data object Loading : HomeDrawerUiState

    sealed interface DialogState {
        data object None : DialogState
        data class ChangeTableName(
            val tableSummary: TableSummary,
        ) : DialogState

        data class DeleteTable(
            val tableSummary: TableSummary,
        ) : DialogState
    }
}

// 이렇게 uiState 용 data class 를 만드는 건 어떨까? 위치는?
data class CoursebookDrawerItem(
    val courseBook: CourseBook,
    val showNewCoursebookDot: Boolean,
    val tableList: List<TableSummary>,
)

sealed class HomeDrawerBottomSheetType {
    data object Empty : HomeDrawerBottomSheetType()
    data class SelectTheme(
        val customThemes: List<CustomTheme>,
        val builtInThemes: List<BuiltInTheme>,
        val selectedPreviewTheme: TableTheme,
    ) : HomeDrawerBottomSheetType()

    data object CreateNewTheme : HomeDrawerBottomSheetType()
    sealed class CreateNewTable : HomeDrawerBottomSheetType() {
        data class SelectCourseBook(
            val initialCourseBook: CourseBook,
            val allCourseBook: List<CourseBook>,
        ) : CreateNewTable()

        data class SpecificCourseBook(
            val courseBook: CourseBook,
        ) : CreateNewTable()
    }

    data class MoreAction(
        val tableSummary: TableSummary,
    ) : HomeDrawerBottomSheetType()
}
