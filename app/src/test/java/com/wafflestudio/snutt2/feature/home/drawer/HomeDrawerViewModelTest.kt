package com.wafflestudio.snutt2.feature.home.drawer

import app.cash.turbine.test
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domain.NotSelectedTimetable
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.fake.FakeCourseBookRepository
import com.wafflestudio.snutt2.fake.FakeDisplayMessageResolver
import com.wafflestudio.snutt2.fake.FakeTableRepository
import com.wafflestudio.snutt2.fake.FakeThemeRepository
import com.wafflestudio.snutt2.fixture.TestFixtures.courseBook2024_2
import com.wafflestudio.snutt2.fixture.TestFixtures.courseBook2025_1
import com.wafflestudio.snutt2.fixture.TestFixtures.customTheme
import com.wafflestudio.snutt2.fixture.TestFixtures.table
import com.wafflestudio.snutt2.fixture.TestFixtures.tableSummary
import com.wafflestudio.snutt2.lib.toDataWithState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeDrawerViewModelTest {

    private lateinit var fakeCourseBookRepository: FakeCourseBookRepository
    private lateinit var fakeTableRepository: FakeTableRepository
    private lateinit var fakeThemeRepository: FakeThemeRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeCourseBookRepository = FakeCourseBookRepository()
        fakeTableRepository = FakeTableRepository()
        fakeThemeRepository = FakeThemeRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HomeDrawerViewModel(
        courseBookRepository = fakeCourseBookRepository,
        tableRepository = fakeTableRepository,
        themeRepository = fakeThemeRepository,
        getCurrentTableThemeUseCase = GetCurrentTableThemeUseCase(
            themeRepository = fakeThemeRepository,
            tableRepository = fakeTableRepository,
        ),
        displayMessageResolver = fakeDisplayMessageResolver,
    )

    // region init — combine으로 courseBookDrawerItemList 구성

    @Test
    fun `init 시 courseBooks와 tableSummaryList로 서랍 목록이 구성된다`() = runTest {
        val summary1 = tableSummary(id = "t1", courseBook = courseBook2025_1, title = "시간표1")
        val summary2 = tableSummary(id = "t2", courseBook = courseBook2024_2, title = "시간표2")
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1, courseBook2024_2)
        fakeTableRepository.tableSummaryList.value = listOf(summary1, summary2)
        fakeTableRepository.currentTable.value = table(summary = summary1)

        val viewModel = createViewModel()

        assertEquals(
            HomeDrawerUiState(
                courseBookDrawerItemList = listOf(
                    CoursebookDrawerItem(
                        courseBook = courseBook2025_1,
                        showNewCoursebookDot = false,
                        tableList = listOf(summary1),
                    ).toDataWithState(true), // current semester → expanded
                    CoursebookDrawerItem(
                        courseBook = courseBook2024_2,
                        showNewCoursebookDot = false,
                        tableList = listOf(summary2),
                    ).toDataWithState(false),
                ),
                selectedTable = summary1,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `최신 학기에 시간표가 없어도 서랍에 표시되고 newCoursebookDot이 true이다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2024_2)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1, courseBook2024_2)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(summary = summary)

        val viewModel = createViewModel()

        assertEquals(
            HomeDrawerUiState(
                courseBookDrawerItemList = listOf(
                    CoursebookDrawerItem(
                        courseBook = courseBook2025_1,
                        showNewCoursebookDot = true,
                        tableList = emptyList(),
                    ).toDataWithState(false),
                    CoursebookDrawerItem(
                        courseBook = courseBook2024_2,
                        showNewCoursebookDot = false,
                        tableList = listOf(summary),
                    ).toDataWithState(true), // current semester → expanded
                ),
                selectedTable = summary,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `courseBooks가 비어있으면 서랍 목록이 갱신되지 않는다`() = runTest {
        fakeCourseBookRepository.courseBooks.value = emptyList()
        fakeTableRepository.tableSummaryList.value = listOf(
            tableSummary(id = "t1", courseBook = courseBook2025_1),
        )
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t1", courseBook = courseBook2025_1),
        )

        val viewModel = createViewModel()

        assertEquals(HomeDrawerUiState(), viewModel.uiState.value)
    }

    @Test
    fun `tableSummaryList가 비어있으면 서랍 목록이 갱신되지 않는다`() = runTest {
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = emptyList()
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t1", courseBook = courseBook2025_1),
        )

        val viewModel = createViewModel()

        assertEquals(HomeDrawerUiState(), viewModel.uiState.value)
    }

    // endregion

    // region Source 반응 — currentTable 변경 시 expanded 이동

    @Test
    fun `currentTable이 다른 학기로 변경되면 해당 학기가 expanded 되고 이전 학기도 유지된다`() = runTest {
        val summary2025 = tableSummary(id = "t1", courseBook = courseBook2025_1)
        val summary2024 = tableSummary(id = "t2", courseBook = courseBook2024_2)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1, courseBook2024_2)
        fakeTableRepository.tableSummaryList.value = listOf(summary2025, summary2024)
        fakeTableRepository.currentTable.value = table(summary = summary2025)
        val viewModel = createViewModel()

        fakeTableRepository.currentTable.value = table(summary = summary2024)

        // NOTE: isCurrentSemester || wasExpanded 로직으로 인해,
        // 한번 expanded된 학기는 current가 아니게 되어도 접히지 않는다.
        // 수동 toggle만이 접을 수 있다.
        assertEquals(
            HomeDrawerUiState(
                courseBookDrawerItemList = listOf(
                    CoursebookDrawerItem(
                        courseBook = courseBook2025_1,
                        showNewCoursebookDot = false,
                        tableList = listOf(summary2025),
                    ).toDataWithState(true), // 이전에 current로 expanded → wasExpanded로 유지
                    CoursebookDrawerItem(
                        courseBook = courseBook2024_2,
                        showNewCoursebookDot = false,
                        tableList = listOf(summary2024),
                    ).toDataWithState(true), // 새로 current → expanded
                ),
                selectedTable = summary2024,
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — tableSummaryList 변경 시 그룹핑 반영

    @Test
    fun `tableSummaryList에 시간표가 추가되면 해당 학기의 tableList에 반영된다`() = runTest {
        val summary1 = tableSummary(id = "t1", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary1)
        fakeTableRepository.currentTable.value = table(summary = summary1)
        val viewModel = createViewModel()

        val summary2 = tableSummary(id = "t2", courseBook = courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary1, summary2)

        assertEquals(
            HomeDrawerUiState(
                courseBookDrawerItemList = listOf(
                    CoursebookDrawerItem(
                        courseBook = courseBook2025_1,
                        showNewCoursebookDot = false,
                        tableList = listOf(summary1, summary2),
                    ).toDataWithState(true),
                ),
                selectedTable = summary1,
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region Source 반응 — expanded 보존

    @Test
    fun `수동으로 펼친 학기는 source 변화 후에도 expanded가 유지된다`() = runTest {
        val summary2025 = tableSummary(id = "t1", courseBook = courseBook2025_1)
        val summary2024 = tableSummary(id = "t2", courseBook = courseBook2024_2)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1, courseBook2024_2)
        fakeTableRepository.tableSummaryList.value = listOf(summary2025, summary2024)
        fakeTableRepository.currentTable.value = table(summary = summary2025)
        val viewModel = createViewModel()
        // 2024-2를 수동으로 펼침 (index 1)
        viewModel.toggleCourseBookDrawerItem(1)

        // tableSummaryList 변경 → combine 재실행
        val summary2025Second = tableSummary(id = "t3", courseBook = courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary2025, summary2024, summary2025Second)

        assertEquals(
            HomeDrawerUiState(
                courseBookDrawerItemList = listOf(
                    CoursebookDrawerItem(
                        courseBook = courseBook2025_1,
                        showNewCoursebookDot = false,
                        tableList = listOf(summary2025, summary2025Second),
                    ).toDataWithState(true), // current → expanded
                    CoursebookDrawerItem(
                        courseBook = courseBook2024_2,
                        showNewCoursebookDot = false,
                        tableList = listOf(summary2024),
                    ).toDataWithState(true), // 수동으로 펼쳤으므로 유지
                ),
                selectedTable = summary2025,
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onClickDrawerIcon

    @Test
    fun `onClickDrawerIcon 호출 시 OpenDrawer 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickDrawerIcon()
            assertEquals(HomeDrawerUiEvent.OpenDrawer, awaitItem())
        }
    }

    // endregion

    // region toggleCourseBookDrawerItem

    @Test
    fun `toggleCourseBookDrawerItem 호출 시 해당 항목의 expanded가 토글된다`() = runTest {
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1, courseBook2024_2)
        fakeTableRepository.tableSummaryList.value = listOf(
            tableSummary(id = "t1", courseBook = courseBook2024_2),
        )
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t1", courseBook = courseBook2024_2),
        )
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        // index 0 = courseBook2025_1 (최신 순 정렬)
        viewModel.toggleCourseBookDrawerItem(0)

        assertEquals(
            before.copy(
                courseBookDrawerItemList = before.courseBookDrawerItemList.mapIndexed { i, item ->
                    if (i == 0) item.copy(state = !item.state) else item
                },
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region selectTable

    @Test
    fun `selectTable 호출 시 repository의 fetchAndSelectTable을 호출한다`() = runTest {
        val viewModel = createViewModel()
        viewModel.selectTable("table-id")
        assertEquals("table-id", fakeTableRepository.fetchAndSelectTableCalledWith)
    }

    @Test
    fun `selectTable 성공 시 CloseDrawer 이벤트가 발생한다`() = runTest {
        fakeTableRepository.fetchAndSelectTableResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.selectTable("table-id")
            assertEquals(HomeDrawerUiEvent.CloseDrawer, awaitItem())
        }
    }

    @Test
    fun `selectTable 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.fetchAndSelectTableResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.selectTable("table-id")
            assertEquals(HomeDrawerUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region openCreateNewTableSheet / createNewTable

    @Test
    fun `openCreateNewTableSheet 호출 시 바텀시트 상태가 SelectCourseBook이 된다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1, courseBook2024_2)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(summary = summary)
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        viewModel.openCreateNewTableSheet()

        assertEquals(
            before.copy(
                homeDrawerBottomSheetType = HomeDrawerBottomSheetType.CreateNewTable.SelectCourseBook(
                    initialCourseBook = courseBook2025_1,
                    allCourseBook = listOf(courseBook2025_1, courseBook2024_2),
                ),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `openCreateNewTableSheet 호출 시 selectedTable이 없으면 allCourseBooks의 첫 번째가 초기 학기가 된다`() = runTest {
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1, courseBook2024_2)
        fakeTableRepository.tableSummaryList.value = emptyList()
        fakeTableRepository.currentTable.value = null
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        viewModel.openCreateNewTableSheet()

        assertEquals(
            before.copy(
                homeDrawerBottomSheetType = HomeDrawerBottomSheetType.CreateNewTable.SelectCourseBook(
                    initialCourseBook = courseBook2025_1,
                    allCourseBook = listOf(courseBook2025_1, courseBook2024_2),
                ),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `openCreateNewTableSheet 호출 시 OpenBottomSheet 이벤트가 발생한다`() = runTest {
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = emptyList()
        fakeTableRepository.currentTable.value = null
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.openCreateNewTableSheet()
            assertEquals(HomeDrawerUiEvent.OpenBottomSheet, awaitItem())
        }
    }

    @Test
    fun `openCreateNewTableOfSpecificCourseBookSheet 호출 시 바텀시트 상태가 SpecificCourseBook이 된다`() = runTest {
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        viewModel.openCreateNewTableOfSpecificCourseBookSheet(courseBook2025_1)

        assertEquals(
            before.copy(
                homeDrawerBottomSheetType = HomeDrawerBottomSheetType.CreateNewTable.SpecificCourseBook(
                    courseBook = courseBook2025_1,
                ),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `openCreateNewTableOfSpecificCourseBookSheet 호출 시 OpenBottomSheet 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.openCreateNewTableOfSpecificCourseBookSheet(courseBook2025_1)
            assertEquals(HomeDrawerUiEvent.OpenBottomSheet, awaitItem())
        }
    }

    @Test
    fun `createNewTable 호출 시 repository의 createAndSelectTable을 호출한다`() = runTest {
        val viewModel = createViewModel()
        viewModel.openCreateNewTableOfSpecificCourseBookSheet(courseBook2025_1)
        viewModel.onCreateNewTableTitleChange("새 시간표")
        viewModel.createNewTable()
        assertEquals(courseBook2025_1 to "새 시간표", fakeTableRepository.createAndSelectTableCalledWith)
    }

    @Test
    fun `createNewTable 성공 시 CloseBottomSheet와 CloseDrawer 이벤트가 순서대로 발생한다`() = runTest {
        fakeTableRepository.createAndSelectTableResult = Result.Success(Unit)
        val viewModel = createViewModel()
        viewModel.openCreateNewTableOfSpecificCourseBookSheet(courseBook2025_1)
        viewModel.onCreateNewTableTitleChange("새 시간표")

        viewModel.uiEvent.test {
            viewModel.createNewTable()
            assertEquals(HomeDrawerUiEvent.CloseBottomSheet, awaitItem())
            assertEquals(HomeDrawerUiEvent.CloseDrawer, awaitItem())
        }
    }

    @Test
    fun `createNewTable 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.createAndSelectTableResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()
        viewModel.openCreateNewTableOfSpecificCourseBookSheet(courseBook2025_1)
        viewModel.onCreateNewTableTitleChange("새 시간표")

        viewModel.uiEvent.test {
            viewModel.createNewTable()
            assertEquals(HomeDrawerUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region openMoreActionBottomSheet

    @Test
    fun `openMoreActionBottomSheet 호출 시 바텀시트 상태가 MoreAction이 된다`() = runTest {
        val summary = tableSummary(id = "t1")
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        viewModel.openMoreActionBottomSheet(summary)

        assertEquals(
            before.copy(homeDrawerBottomSheetType = HomeDrawerBottomSheetType.MoreAction(summary)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `openMoreActionBottomSheet 호출 시 OpenBottomSheet 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.openMoreActionBottomSheet(tableSummary(id = "t1"))
            assertEquals(HomeDrawerUiEvent.OpenBottomSheet, awaitItem())
        }
    }

    // endregion

    // region openChangeTableNameDialog / changeTableTitle

    @Test
    fun `openChangeTableNameDialog 호출 시 ChangeTableName 다이얼로그가 열린다`() = runTest {
        val viewModel = createViewModel()
        val before = viewModel.uiState.value
        val summary = tableSummary(id = "t1")

        viewModel.openChangeTableNameDialog(summary)

        assertEquals(
            before.copy(dialogState = HomeDrawerUiState.DialogState.ChangeTableName(summary)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `changeTableTitle 호출 시 repository의 updateTableName을 호출한다`() = runTest {
        val summary = tableSummary(id = "t1")
        val viewModel = createViewModel()
        viewModel.openChangeTableNameDialog(summary)
        viewModel.onChangeTableNameTitleChange("변경된 이름")

        viewModel.changeTableTitle()

        assertEquals(summary to "변경된 이름", fakeTableRepository.updateTableNameCalledWith)
    }

    @Test
    fun `changeTableTitle 성공 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        fakeTableRepository.updateTableNameResult = Result.Success(Unit)
        val viewModel = createViewModel()
        viewModel.openChangeTableNameDialog(tableSummary(id = "t1"))
        viewModel.onChangeTableNameTitleChange("변경된 이름")

        viewModel.uiEvent.test {
            viewModel.changeTableTitle()
            assertEquals(HomeDrawerUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    @Test
    fun `changeTableTitle 성공 시 다이얼로그가 닫힌다`() = runTest {
        fakeTableRepository.updateTableNameResult = Result.Success(Unit)
        val viewModel = createViewModel()
        val summary = tableSummary(id = "t1")
        viewModel.openChangeTableNameDialog(summary)
        viewModel.onChangeTableNameTitleChange("변경된 이름")
        val before = viewModel.uiState.value

        viewModel.changeTableTitle()

        assertEquals(
            before.copy(dialogState = HomeDrawerUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `changeTableTitle 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.updateTableNameResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()
        viewModel.openChangeTableNameDialog(tableSummary(id = "t1"))
        viewModel.onChangeTableNameTitleChange("변경된 이름")

        viewModel.uiEvent.test {
            viewModel.changeTableTitle()
            assertEquals(HomeDrawerUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region openDeleteTableDialog / deleteTable

    @Test
    fun `openDeleteTableDialog 호출 시 DeleteTable 다이얼로그가 열린다`() = runTest {
        val viewModel = createViewModel()
        val before = viewModel.uiState.value
        val summary = tableSummary(id = "t1")

        viewModel.openDeleteTableDialog(summary)

        assertEquals(
            before.copy(dialogState = HomeDrawerUiState.DialogState.DeleteTable(summary)),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `deleteTable 호출 시 repository의 deleteTable을 호출한다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t2", courseBook = courseBook2025_1),
        )
        val viewModel = createViewModel()

        viewModel.deleteTable(summary)

        assertEquals(summary, fakeTableRepository.deleteTableCalledWith)
    }

    @Test
    fun `deleteTable 성공 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t2", courseBook = courseBook2025_1),
        )
        fakeTableRepository.deleteTableResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.deleteTable(summary)
            assertEquals(HomeDrawerUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    @Test
    fun `deleteTable 성공 시 다이얼로그가 닫힌다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t2", courseBook = courseBook2025_1),
        )
        fakeTableRepository.deleteTableResult = Result.Success(Unit)
        val viewModel = createViewModel()
        viewModel.openDeleteTableDialog(summary)
        val before = viewModel.uiState.value

        viewModel.deleteTable(summary)

        assertEquals(
            before.copy(dialogState = HomeDrawerUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `deleteTable 성공 시 현재 시간표를 삭제하면 같은 학기의 다른 시간표로 전환한다`() = runTest {
        val current = tableSummary(id = "t1", courseBook = courseBook2025_1)
        val other = tableSummary(id = "t2", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(current, other)
        fakeTableRepository.currentTable.value = table(summary = current)
        fakeTableRepository.deleteTableResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.deleteTable(current)

        assertEquals("t2", fakeTableRepository.fetchAndSelectTableCalledWith)
    }

    @Test
    fun `deleteTable 성공 시 현재 시간표를 삭제하고 같은 학기에 남은 시간표가 없으면 다른 학기 시간표로 전환한다`() = runTest {
        val current = tableSummary(id = "t1", courseBook = courseBook2025_1)
        val otherSemester = tableSummary(id = "t2", courseBook = courseBook2024_2)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1, courseBook2024_2)
        fakeTableRepository.tableSummaryList.value = listOf(current, otherSemester)
        fakeTableRepository.currentTable.value = table(summary = current)
        fakeTableRepository.deleteTableResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.deleteTable(current)

        assertEquals("t2", fakeTableRepository.fetchAndSelectTableCalledWith)
    }

    @Test
    fun `deleteTable 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = tableSummary(id = "t2", courseBook = courseBook2025_1),
        )
        fakeTableRepository.deleteTableResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.deleteTable(summary)
            assertEquals(HomeDrawerUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region shareTable

    @Test
    fun `shareTable 호출 시 repository의 getTableById를 호출한다`() = runTest {
        val summary = tableSummary(id = "t1")
        val viewModel = createViewModel()

        viewModel.shareTable(summary)

        assertEquals("t1", fakeTableRepository.getTableByIdCalledWith)
    }

    @Test
    fun `shareTable 성공 시 ShareTable 이벤트가 발생한다`() = runTest {
        val tableObj = table(summary = tableSummary(id = "t1"))
        fakeTableRepository.getTableByIdResult = Result.Success(tableObj)
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.shareTable(tableSummary(id = "t1"))
            assertEquals(HomeDrawerUiEvent.ShareTable(tableObj), awaitItem())
        }
    }

    @Test
    fun `shareTable 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.getTableByIdResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.shareTable(tableSummary(id = "t1"))
            assertEquals(HomeDrawerUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region dismissDialog

    @Test
    fun `dismissDialog 호출 시 다이얼로그가 닫힌다`() = runTest {
        val viewModel = createViewModel()
        viewModel.openChangeTableNameDialog(tableSummary(id = "t1"))
        val before = viewModel.uiState.value

        viewModel.dismissDialog()

        assertEquals(
            before.copy(dialogState = HomeDrawerUiState.DialogState.None),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region onDrawerOpened

    @Test
    fun `onDrawerOpened 호출 시 fetchTableList가 호출된다`() = runTest {
        val viewModel = createViewModel()

        viewModel.onDrawerOpened()

        assertEquals(true, fakeTableRepository.fetchTableListCalled)
    }

    @Test
    fun `onDrawerOpened 호출 시 fetchCourseBooks가 호출된다`() = runTest {
        val viewModel = createViewModel()

        viewModel.onDrawerOpened()

        assertEquals(true, fakeCourseBookRepository.fetchCourseBooksCalled)
    }

    // endregion

    // region copyTable

    @Test
    fun `copyTable 호출 시 repository의 copyTable을 호출한다`() = runTest {
        val summary = tableSummary(id = "t1")
        val viewModel = createViewModel()

        viewModel.copyTable(summary)

        assertEquals(summary, fakeTableRepository.copyTableCalledWith)
    }

    @Test
    fun `copyTable 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.copyTableResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.copyTable(tableSummary(id = "t1"))
            assertEquals(HomeDrawerUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region navigateToThemeDetail

    @Test
    fun `navigateToThemeDetail 호출 시 NavigateToThemeDetail 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.navigateToThemeDetail()
            assertEquals(HomeDrawerUiEvent.NavigateToThemeDetail, awaitItem())
        }
    }

    // endregion

    // region closeSheet

    @Test
    fun `closeSheet 호출 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.closeSheet()
            assertEquals(HomeDrawerUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    // endregion

    // region onSheetDismissed

    @Test
    fun `onSheetDismissed 호출 시 바텀시트가 Empty가 된다`() = runTest {
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = emptyList()
        fakeTableRepository.currentTable.value = null
        val viewModel = createViewModel()
        viewModel.openCreateNewTableSheet()
        val before = viewModel.uiState.value

        viewModel.onSheetDismissed()

        assertEquals(
            before.copy(homeDrawerBottomSheetType = HomeDrawerBottomSheetType.Empty),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `onSheetDismissed 호출 시 sheetTransitionTarget이 있으면 해당 시트로 전환된다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        val builtInThemes = listOf(BuiltInTheme.SNUTT, BuiltInTheme.MODERN)
        val myCustomTheme = customTheme(id = "ct-1", name = "커스텀")
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = summary,
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.customThemes.value = listOf(myCustomTheme)
        fakeThemeRepository.builtInThemes.value = builtInThemes
        val viewModel = createViewModel()

        // onClickSetThemeSheet에서 sheetTransitionTarget이 설정됨
        viewModel.onClickSetThemeSheet(summary)
        val beforeDismiss = viewModel.uiState.value

        // onSheetDismissed에서 pending을 적용
        viewModel.onSheetDismissed()

        assertEquals(
            beforeDismiss.copy(
                homeDrawerBottomSheetType = HomeDrawerBottomSheetType.SelectTheme(
                    customThemes = listOf(myCustomTheme),
                    builtInThemes = builtInThemes,
                    selectedPreviewTheme = BuiltInTheme.SNUTT,
                ),
                sheetTransitionTarget = null,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `onSheetDismissed 호출 시 sheetTransitionTarget이 있으면 OpenBottomSheet 이벤트가 발생한다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = summary,
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.customThemes.value = emptyList()
        fakeThemeRepository.builtInThemes.value = listOf(BuiltInTheme.SNUTT)
        val viewModel = createViewModel()

        // onClickSetThemeSheet에서 sheetTransitionTarget이 설정됨 → CloseDrawer, CloseBottomSheet 이벤트 소비
        viewModel.onClickSetThemeSheet(summary)

        viewModel.uiEvent.test {
            viewModel.onSheetDismissed()
            assertEquals(HomeDrawerUiEvent.OpenBottomSheet, awaitItem())
        }
    }

    // endregion

    // region setPrimaryTable

    @Test
    fun `setPrimaryTable 호출 시 repository의 setPrimaryTable을 호출한다`() = runTest {
        val summary = tableSummary(id = "t1")
        val viewModel = createViewModel()

        viewModel.setPrimaryTable(summary)

        assertEquals(summary, fakeTableRepository.setPrimaryTableCalledWith)
    }

    @Test
    fun `setPrimaryTable 성공 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        fakeTableRepository.setPrimaryTableResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.setPrimaryTable(tableSummary(id = "t1"))
            assertEquals(HomeDrawerUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    @Test
    fun `setPrimaryTable 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.setPrimaryTableResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.setPrimaryTable(tableSummary(id = "t1"))
            assertEquals(HomeDrawerUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region unsetPrimaryTable

    @Test
    fun `unsetPrimaryTable 호출 시 repository의 unsetPrimaryTable을 호출한다`() = runTest {
        val summary = tableSummary(id = "t1")
        val viewModel = createViewModel()

        viewModel.unsetPrimaryTable(summary)

        assertEquals(summary, fakeTableRepository.unsetPrimaryTableCalledWith)
    }

    @Test
    fun `unsetPrimaryTable 성공 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        fakeTableRepository.unsetPrimaryTableResult = Result.Success(Unit)
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.unsetPrimaryTable(tableSummary(id = "t1"))
            assertEquals(HomeDrawerUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    @Test
    fun `unsetPrimaryTable 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
        fakeTableRepository.unsetPrimaryTableResult =
            Result.Fail(Unknown(displayTitle = "", displayMessage = "에러"))
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.unsetPrimaryTable(tableSummary(id = "t1"))
            assertEquals(HomeDrawerUiEvent.ShowToast("에러"), awaitItem())
        }
    }

    // endregion

    // region onClickSetThemeSheet

    @Test
    fun `onClickSetThemeSheet 호출 시 선택되지 않은 시간표이면 ShowToast 이벤트가 발생한다`() = runTest {
        val currentSummary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        val otherSummary = tableSummary(id = "t2", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(currentSummary, otherSummary)
        fakeTableRepository.currentTable.value = table(summary = currentSummary)
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickSetThemeSheet(otherSummary)
            assertEquals(
                HomeDrawerUiEvent.ShowToast(NotSelectedTimetable.displayMessage),
                awaitItem(),
            )
        }
    }

    @Test
    fun `onClickSetThemeSheet 호출 시 선택된 시간표이면 CloseDrawer 이벤트가 발생한다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = summary,
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.customThemes.value = emptyList()
        fakeThemeRepository.builtInThemes.value = listOf(BuiltInTheme.SNUTT)
        val viewModel = createViewModel()

        viewModel.uiEvent.test {
            viewModel.onClickSetThemeSheet(summary)
            assertEquals(HomeDrawerUiEvent.CloseDrawer, awaitItem())
            assertEquals(HomeDrawerUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    @Test
    fun `onClickSetThemeSheet 호출 시 선택된 시간표이면 sheetTransitionTarget에 SelectTheme가 설정된다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        val myCustomTheme = customTheme(id = "ct-1", name = "커스텀")
        val builtInThemes = listOf(BuiltInTheme.SNUTT, BuiltInTheme.MODERN)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = summary,
            themeRef = ThemeReference.BuiltIn(1),
        )
        fakeThemeRepository.customThemes.value = listOf(myCustomTheme)
        fakeThemeRepository.builtInThemes.value = builtInThemes
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        viewModel.onClickSetThemeSheet(summary)

        assertEquals(
            before.copy(
                sheetTransitionTarget = HomeDrawerBottomSheetType.SelectTheme(
                    customThemes = listOf(myCustomTheme),
                    builtInThemes = builtInThemes,
                    selectedPreviewTheme = BuiltInTheme.MODERN,
                ),
            ),
            viewModel.uiState.value,
        )
    }

    // endregion

    // region setPreviewTheme

    @Test
    fun `setPreviewTheme 호출 시 SelectTheme 시트의 selectedPreviewTheme이 변경된다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        val builtInThemes = listOf(BuiltInTheme.SNUTT, BuiltInTheme.MODERN)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = summary,
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.customThemes.value = emptyList()
        fakeThemeRepository.builtInThemes.value = builtInThemes
        val viewModel = createViewModel()
        // SelectTheme 시트를 연다 (onClickSetThemeSheet → onSheetDismissed로 전환)
        viewModel.onClickSetThemeSheet(summary)
        viewModel.onSheetDismissed()
        val before = viewModel.uiState.value

        viewModel.setPreviewTheme(BuiltInTheme.MODERN)

        assertEquals(
            before.copy(
                homeDrawerBottomSheetType = HomeDrawerBottomSheetType.SelectTheme(
                    customThemes = emptyList(),
                    builtInThemes = builtInThemes,
                    selectedPreviewTheme = BuiltInTheme.MODERN,
                ),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `setPreviewTheme 호출 시 시트가 SelectTheme이 아니면 상태가 변경되지 않는다`() = runTest {
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        viewModel.setPreviewTheme(BuiltInTheme.MODERN)

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion

    // region applyTheme

    @Test
    fun `applyTheme 호출 시 BuiltInTheme이면 updateTableTheme을 code로 호출한다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = summary,
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.customThemes.value = emptyList()
        fakeThemeRepository.builtInThemes.value = listOf(BuiltInTheme.SNUTT, BuiltInTheme.MODERN)
        val viewModel = createViewModel()
        // SelectTheme 시트를 연다
        viewModel.onClickSetThemeSheet(summary)
        viewModel.onSheetDismissed()
        // MODERN 테마로 변경
        viewModel.setPreviewTheme(BuiltInTheme.MODERN)

        viewModel.applyTheme()

        assertEquals("t1" to 1, fakeTableRepository.updateTableThemeBuiltInCalledWith)
    }

    @Test
    fun `applyTheme 호출 시 CustomTheme이면 updateTableTheme을 themeId로 호출한다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        val myCustomTheme = customTheme(id = "ct-1", name = "커스텀")
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = summary,
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.customThemes.value = listOf(myCustomTheme)
        fakeThemeRepository.builtInThemes.value = listOf(BuiltInTheme.SNUTT)
        fakeThemeRepository.getThemeResult = myCustomTheme
        val viewModel = createViewModel()
        // SelectTheme 시트를 연다
        viewModel.onClickSetThemeSheet(summary)
        viewModel.onSheetDismissed()
        // CustomTheme으로 변경
        viewModel.setPreviewTheme(myCustomTheme)

        viewModel.applyTheme()

        assertEquals("t1" to "ct-1", fakeTableRepository.updateTableThemeCustomCalledWith)
    }

    @Test
    fun `applyTheme 호출 시 CloseBottomSheet 이벤트가 발생한다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = summary,
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.customThemes.value = emptyList()
        fakeThemeRepository.builtInThemes.value = listOf(BuiltInTheme.SNUTT, BuiltInTheme.MODERN)
        val viewModel = createViewModel()
        // SelectTheme 시트를 연다
        viewModel.onClickSetThemeSheet(summary)
        viewModel.onSheetDismissed()

        viewModel.uiEvent.test {
            viewModel.applyTheme()
            assertEquals(HomeDrawerUiEvent.CloseBottomSheet, awaitItem())
        }
    }

    // endregion

    // region source: customThemes/builtInThemes — SelectTheme 시트 자동 반영

    @Test
    fun `SelectTheme 시트가 열려있을 때 customThemes가 변경되면 시트에 반영된다`() = runTest {
        val summary = tableSummary(id = "t1", courseBook = courseBook2025_1)
        val builtInThemes = listOf(BuiltInTheme.SNUTT)
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = listOf(summary)
        fakeTableRepository.currentTable.value = table(
            summary = summary,
            themeRef = ThemeReference.BuiltIn(0),
        )
        fakeThemeRepository.customThemes.value = emptyList()
        fakeThemeRepository.builtInThemes.value = builtInThemes
        val viewModel = createViewModel()
        viewModel.onClickSetThemeSheet(summary)
        viewModel.onSheetDismissed()
        val before = viewModel.uiState.value

        val newCustomTheme = customTheme(id = "ct-new", name = "새 테마")
        fakeThemeRepository.customThemes.value = listOf(newCustomTheme)

        assertEquals(
            before.copy(
                homeDrawerBottomSheetType = HomeDrawerBottomSheetType.SelectTheme(
                    customThemes = listOf(newCustomTheme),
                    builtInThemes = builtInThemes,
                    selectedPreviewTheme = BuiltInTheme.SNUTT,
                ),
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `SelectTheme 시트가 열려있지 않으면 customThemes 변경이 시트에 반영되지 않는다`() = runTest {
        fakeCourseBookRepository.courseBooks.value = listOf(courseBook2025_1)
        fakeTableRepository.tableSummaryList.value = emptyList()
        fakeTableRepository.currentTable.value = null
        fakeThemeRepository.customThemes.value = emptyList()
        fakeThemeRepository.builtInThemes.value = listOf(BuiltInTheme.SNUTT)
        val viewModel = createViewModel()
        val before = viewModel.uiState.value

        fakeThemeRepository.customThemes.value = listOf(customTheme(id = "ct-1", name = "새 테마"))

        assertEquals(before, viewModel.uiState.value)
    }

    // endregion
}
