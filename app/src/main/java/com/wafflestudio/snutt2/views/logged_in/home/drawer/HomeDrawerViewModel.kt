package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.Table
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeDrawerViewModel @Inject constructor(
    private val courseBookRepository: CourseBookRepository,
    private val tableRepository: TableRepository,
    private val themeRepository: ThemeRepository,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<HomeDrawerUiEvent>()
    private val _uiState = MutableStateFlow(HomeDrawerUiState())

    val uiEvent = _uiEvent.asSharedFlow()
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                courseBookRepository.courseBooks,
                tableRepository.tableSummaryList,
                tableRepository.currentTable,
            ) { courseBooks, tableSummaryList, currentTable ->
                _uiState.update { state ->
                    if (courseBooks.isEmpty() || tableSummaryList.isEmpty()) return@combine

                    // 유저의 전체 시간표 목록을 학기 별로 그룹핑 한 뒤 학기 순으로 정렬
                    val tableSummariesOfEachCourseBook = tableSummaryList.groupBy {
                        it.courseBook
                    }.toMutableMap()

                    // 가장 최신 학기는 생성된 시간표가 없어도 서랍에서 보여주기
                    val mostRecentCourseBook = courseBooks.first()
                    if (!tableSummariesOfEachCourseBook.containsKey(mostRecentCourseBook)) {
                        tableSummariesOfEachCourseBook[mostRecentCourseBook] = emptyList()
                    }

                    val previousExpandedState =
                        state.courseBookDrawerItemList.associate { (item, expanded) ->
                            item.courseBook to expanded
                        }

                    val courseBookDrawerItemList = tableSummariesOfEachCourseBook
                        .toList()
                        .sortedBy { (coursebook, _) -> coursebook }
                        .map { (courseBook, tableSummaries) ->
                            val isCurrentSemester = currentTable?.summary?.courseBook == courseBook
                            val wasExpanded = previousExpandedState[courseBook] ?: false

                            CoursebookDrawerItem(
                                courseBook = courseBook,
                                showNewCoursebookDot = (courseBook == mostRecentCourseBook) && tableSummaries.isEmpty(),
                                tableList = tableSummaries,
                            ).toDataWithState(isCurrentSemester || wasExpanded)
                        }

                    state.copy(
                        courseBookDrawerItemList = courseBookDrawerItemList,
                        selectedTable = currentTable?.summary,
                    )
                }
            }.collect()
        }

        // SelectTheme 시트가 열려있는 동안 테마 목록 변경을 자동 반영
        viewModelScope.launch {
            combine(
                themeRepository.customThemes,
                themeRepository.builtInThemes,
            ) { customThemes, builtInThemes ->
                Pair(customThemes, builtInThemes)
            }.collect { (customThemes, builtInThemes) ->
                _uiState.update { state ->
                    val sheet = state.homeDrawerBottomSheetType
                    if (sheet !is HomeDrawerBottomSheetType.SelectTheme) return@update state
                    state.copy(
                        homeDrawerBottomSheetType = sheet.copy(
                            customThemes = customThemes,
                            builtInThemes = builtInThemes,
                        ),
                    )
                }
            }
        }
    }

    fun onClickDrawerIcon() {
        viewModelScope.launch { _uiEvent.emit(HomeDrawerUiEvent.OpenDrawer) }
    }

    fun onDrawerOpened() {
        viewModelScope.launch { tableRepository.fetchTableList() }
        viewModelScope.launch { courseBookRepository.fetchCourseBooks() }
    }

    fun toggleCourseBookDrawerItem(index: Int) {
        _uiState.update {
            it.copy(courseBookDrawerItemList = it.courseBookDrawerItemList.toggleIndex(index))
        }
    }

    fun openCreateNewTableSheet() {
        val allCourseBooks = courseBookRepository.courseBooks.value
        _uiState.update {
            it.copy(
                homeDrawerBottomSheetType = HomeDrawerBottomSheetType.CreateNewTable.SelectCourseBook(
                    initialCourseBook = it.selectedTable?.courseBook ?: allCourseBooks.first(),
                    allCourseBook = allCourseBooks,
                ),
            )
        }
        viewModelScope.launch {
            _uiEvent.emit(HomeDrawerUiEvent.OpenBottomSheet)
        }
    }

    fun openCreateNewTableOfSpecificCourseBookSheet(
        courseBook: CourseBook,
    ) {
        _uiState.update {
            it.copy(
                homeDrawerBottomSheetType = HomeDrawerBottomSheetType.CreateNewTable.SpecificCourseBook(
                    courseBook = courseBook,
                ),
            )
        }
        viewModelScope.launch {
            _uiEvent.emit(HomeDrawerUiEvent.OpenBottomSheet)
        }
    }

    fun selectTable(tableId: String) {
        viewModelScope.launch {
            tableRepository.fetchAndSelectTable(tableId)
                .onSuccess { _uiEvent.emit(HomeDrawerUiEvent.CloseDrawer) }
                .onFailure { handleError(it) }
        }
    }

    fun copyTable(tableId: String) {
        viewModelScope.launch {
            tableRepository.copyTable(tableId).onFailure {
                handleError(it)
            }
        }
    }

    fun openMoreActionBottomSheet(tableSummary: TableSummary) {
        _uiState.update {
            it.copy(homeDrawerBottomSheetType = HomeDrawerBottomSheetType.MoreAction(tableSummary))
        }
        viewModelScope.launch {
            _uiEvent.emit(HomeDrawerUiEvent.OpenBottomSheet)
        }
    }

    fun createNewTable(courseBook: CourseBook, title: String) {
        viewModelScope.launch {
            tableRepository.createAndSelectTable(
                courseBook = courseBook,
                title = title,
            ).onFailure {
                handleError(it)
            }.onSuccess {
                _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
                _uiEvent.emit(HomeDrawerUiEvent.CloseDrawer)
            }
        }
    }

    fun openChangeTableNameDialog(tableSummary: TableSummary) {
        _uiState.update {
            it.copy(dialogState = HomeDrawerUiState.DialogState.ChangeTableName(tableSummary))
        }
    }

    fun setPrimaryTable(tableSummary: TableSummary) {
        viewModelScope.launch {
            tableRepository.setPrimaryTable(tableSummary.id)
                .onFailure {
                    handleError(it)
                }
                .onSuccess {
                    _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
                }
        }
    }

    fun unsetPrimaryTable(tableSummary: TableSummary) {
        viewModelScope.launch {
            tableRepository.unsetPrimaryTable(tableSummary.id)
                .onFailure {
                    handleError(it)
                }
                .onSuccess {
                    _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
                }
        }
    }

    fun onClickSetThemeSheet(tableSummary: TableSummary) {
        viewModelScope.launch {
            val currentTable = tableRepository.currentTable.value
            if (currentTable?.summary?.id == tableSummary.id) {
                _uiEvent.emit(HomeDrawerUiEvent.CloseDrawer)
                changeToSelectThemeSheet()
            } else {
                handleError(NotSelectedTimetable)
            }
        }
    }

    private suspend fun buildSelectThemeSheetType(): HomeDrawerBottomSheetType.SelectTheme {
        val customThemes = themeRepository.customThemes.value
        val builtInThemes = themeRepository.builtInThemes.value
        // FIXME: 에러 처리하기. silent 하게 해도 될까?
        val selectedTheme = getCurrentTableThemeUseCase().first()
        return HomeDrawerBottomSheetType.SelectTheme(
            customThemes = customThemes,
            builtInThemes = builtInThemes,
            selectedPreviewTheme = selectedTheme,
        )
    }

    private suspend fun changeToSelectThemeSheet() {
        val selectThemeSheet = buildSelectThemeSheetType()
        _uiState.update { it.copy(sheetTransitionTarget = selectThemeSheet) }
        _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
    }

    fun setPreviewTheme(theme: TableTheme) {
        _uiState.update { state ->
            val sheet = state.homeDrawerBottomSheetType
            if (sheet !is HomeDrawerBottomSheetType.SelectTheme) return@update state
            state.copy(
                homeDrawerBottomSheetType = sheet.copy(selectedPreviewTheme = theme),
            )
        }
    }

    fun applyTheme() {
        viewModelScope.launch {
            val currentTable = tableRepository.currentTable.value
            val previewTheme = (_uiState.value.homeDrawerBottomSheetType as? HomeDrawerBottomSheetType.SelectTheme)
                ?.selectedPreviewTheme
            if (currentTable != null && previewTheme != null) {
                when (previewTheme) {
                    is BuiltInTheme -> tableRepository.updateTableTheme(
                        currentTable.summary.id,
                        previewTheme.code,
                    )

                    is CustomTheme -> tableRepository.updateTableTheme(
                        currentTable.summary.id,
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

    fun openDeleteTableDialog(tableSummary: TableSummary) {
        _uiState.update {
            it.copy(dialogState = HomeDrawerUiState.DialogState.DeleteTable(tableSummary))
        }
    }

    fun shareTable(tableSummary: TableSummary) {
        viewModelScope.launch {
            tableRepository.getTableById(tableSummary.id).onSuccess { table ->
                _uiEvent.emit(HomeDrawerUiEvent.ShareTable(table))
            }.onFailure {
                handleError(it)
            }
        }
    }

    fun changeTableTitle(newTitle: String, tableId: String) {
        viewModelScope.launch {
            tableRepository.updateTableName(newTitle, tableId)
                .onFailure {
                    handleError(it)
                }.onSuccess {
                    _uiState.update {
                        it.copy(dialogState = HomeDrawerUiState.DialogState.None)
                    }
                    _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
                }
        }
    }

    fun deleteTable(tableSummary: TableSummary) {
        viewModelScope.launch {
            val state = _uiState.value
            val allTables = state.courseBookDrawerItemList.flatMap { it.item.tableList }
            val sameCourseBookTables =
                allTables.filter { it.courseBook == tableSummary.courseBook }
            val indexInSameCourseBook =
                sameCourseBookTables.indexOfFirst { it.id == tableSummary.id }
            val indexInAll = allTables.indexOfFirst { it.id == tableSummary.id }

            tableRepository.deleteTable(tableSummary.id)
                .onFailure {
                    handleError(it)
                }
                .onSuccess {
                    // 현재 시간표를 삭제한 경우, 다른 시간표로 전환
                    if (state.selectedTable?.id == tableSummary.id) {
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
                        tableRepository.fetchAndSelectTable(nextTableId)
                    }

                    _uiState.update {
                        it.copy(dialogState = HomeDrawerUiState.DialogState.None)
                    }
                    _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
                }
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = HomeDrawerUiState.DialogState.None) }
    }

    fun closeSheet() {
        viewModelScope.launch {
            _uiEvent.emit(HomeDrawerUiEvent.CloseBottomSheet)
        }
    }

    fun onSheetDismissed() {
        val hadPending = _uiState.value.sheetTransitionTarget != null

        _uiState.update { state ->
            val pending = state.sheetTransitionTarget
            if (pending != null) {
                state.copy(
                    homeDrawerBottomSheetType = pending,
                    sheetTransitionTarget = null,
                )
            } else {
                state.copy(
                    homeDrawerBottomSheetType = HomeDrawerBottomSheetType.Empty,
                )
            }
        }

        if (hadPending) {
            viewModelScope.launch { _uiEvent.emit(HomeDrawerUiEvent.OpenBottomSheet) }
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
    data object OpenDrawer : HomeDrawerUiEvent
    data object CloseDrawer : HomeDrawerUiEvent

    data object OpenBottomSheet : HomeDrawerUiEvent
    data object CloseBottomSheet : HomeDrawerUiEvent

    data object NavigateToThemeDetail : HomeDrawerUiEvent

    data class ShareTable(val table: Table) : HomeDrawerUiEvent

    data class ShowToast(val displayMessage: String) : HomeDrawerUiEvent
}

data class HomeDrawerUiState(
    val courseBookDrawerItemList: List<Selectable<CoursebookDrawerItem>> = emptyList(),
    val selectedTable: TableSummary? = null,
    val homeDrawerBottomSheetType: HomeDrawerBottomSheetType = HomeDrawerBottomSheetType.Empty,
    val sheetTransitionTarget: HomeDrawerBottomSheetType? = null,
    val dialogState: DialogState = DialogState.None,
) {
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
