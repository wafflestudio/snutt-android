package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.bookmark.BookmarkRepository
import com.wafflestudio.snutt2.data.current_table_lecture.CurrentTableLectureRepository
import com.wafflestudio.snutt2.data.lecture_info.LectureInfoRepository
import com.wafflestudio.snutt2.data.semester_status.SemesterStatusRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.domainmodel.LectureReminderOffset
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.EOF
import com.wafflestudio.snutt2.lib.network.LectureOverlap
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentTableLectureDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val currentTableLectureRepository: CurrentTableLectureRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val lectureInfoRepository: LectureInfoRepository,
    private val tableRepository: TableRepository,
    private val vacancyRepository: VacancyRepository,
    private val semesterStatusRepository: SemesterStatusRepository,
    private val userRepository: UserRepository,
    private val remoteConfig: RemoteConfig,
    private val displayMessageResolver: DisplayMessageResolver,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
) : ViewModel() {

    private val table = checkNotNull(tableRepository.currentTable.value)
    private val lectureId: String = checkNotNull(savedStateHandle["lectureId"])
    private val tableId: String = checkNotNull(savedStateHandle["tableId"])

    private var originalLecture: LocalLecture = checkNotNull(table.lectures.find { it.id == lectureId })
    private val courseBook: CourseBook = table.summary.courseBook

    private val _uiState = MutableStateFlow(
        CurrentTableLectureDetailUiState(
            lecture = originalLecture,
            showCategoryPre2025 = (courseBook.year * 10 + courseBook.semester) > 20250L,
        ),
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<CurrentTableLectureDetailUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val accessToken: StateFlow<String> = userRepository.accessToken

    init {
        loadSecondaryData()
        observeThemeAndConfig()
    }

    private fun loadSecondaryData() {
        viewModelScope.launch {
            val reviewInfoDeferred = async { fetchReviewInfo(originalLecture) }
            val buildingsDeferred = async { fetchBuildings(originalLecture) }
            val reminderDeferred = async { fetchLectureReminder(originalLecture, table.summary) }
            val bookmarkDeferred = async { fetchBookmarkState(courseBook) }
            val vacancyDeferred = async { fetchVacancyState() }

            val reviewInfo = reviewInfoDeferred.await()
            val buildings = buildingsDeferred.await()
            val reminderState = reminderDeferred.await()
            val isBookmarked = bookmarkDeferred.await()
            val vacancyRegistered = vacancyDeferred.await()

            _uiState.update { state ->
                state.copy(
                    reviewInfo = reviewInfo,
                    buildings = buildings,
                    isBookmarked = isBookmarked,
                    vacancyRegistered = vacancyRegistered,
                    showLectureReminderPicker = reminderState.showPicker,
                    lectureWithReminderOption = reminderState.option,
                    enableLectureReminderPicker = reminderState.enablePicker,
                )
            }
        }
    }

    private fun observeThemeAndConfig() {
        viewModelScope.launch {
            combine(
                getCurrentTableThemeUseCase(),
                remoteConfig.disableMapFeature,
            ) { tableTheme, disableMapFeature ->
                _uiState.update { state ->
                    state.copy(
                        tableTheme = tableTheme,
                        disableMapFeature = disableMapFeature,
                    )
                }
            }.collect()
        }
    }

    // region 편집 모드 전환

    fun toggleEditMode() {
        val state = _uiState.value

        if (state.editMode) {
            viewModelScope.launch {
                currentTableLectureRepository.updateLecture(state.lecture, isForced = false)
                    .onSuccess {
                        originalLecture = state.lecture
                        _uiState.update { state.copy(editMode = false) }
                    }
                    .onFailure { error ->
                        when (error) {
                            is LectureOverlap -> _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.LectureTimeOverlap(error.displayMessage)) }
                            else -> handleError(error)
                        }
                    }
            }
        } else {
            _uiState.update { it.copy(editMode = true) }
        }
    }

    fun requestExitEditMode() {
        _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.ExitEditMode) }
    }

    fun confirmExitEditMode() {
        _uiState.update {
            it.copy(
                lecture = originalLecture,
                editMode = false,
                dialogState = CurrentTableLectureDetailUiState.DialogState.None,
            )
        }
        viewModelScope.launch {
            val buildings = fetchBuildings(originalLecture)
            _uiState.update { it.copy(buildings = buildings) }
        }
    }

    // endregion

    // region 필드 편집

    fun editCourseTitle(value: String) = editLecture { lecture ->
        when (lecture) {
            is SyllabusLecture -> lecture.copy(courseTitle = value)
            is CustomLecture -> lecture.copy(courseTitle = value)
        }
    }

    fun editInstructor(value: String) = editLecture { lecture ->
        when (lecture) {
            is SyllabusLecture -> lecture.copy(instructor = value)
            is CustomLecture -> lecture.copy(instructor = value)
        }
    }

    fun editCredit(value: Long) = editLecture { lecture ->
        when (lecture) {
            is SyllabusLecture -> lecture.copy(credit = value)
            is CustomLecture -> lecture.copy(credit = value)
        }
    }

    fun editDepartment(value: String) = editLecture { lecture ->
        when (lecture) {
            is SyllabusLecture -> lecture.copy(department = value)
            is CustomLecture -> lecture
        }
    }

    fun editAcademicYear(value: String) = editLecture { lecture ->
        when (lecture) {
            is SyllabusLecture -> lecture.copy(academicYear = value)
            is CustomLecture -> lecture
        }
    }

    fun editClassification(value: String) = editLecture { lecture ->
        when (lecture) {
            is SyllabusLecture -> lecture.copy(classification = value)
            is CustomLecture -> lecture
        }
    }

    fun editCategory(value: String) = editLecture { lecture ->
        when (lecture) {
            is SyllabusLecture -> lecture.copy(category = value)
            is CustomLecture -> lecture
        }
    }

    fun editCategoryPre2025(value: String) = editLecture { lecture ->
        when (lecture) {
            is SyllabusLecture -> lecture.copy(categoryPre2025 = value)
            is CustomLecture -> lecture
        }
    }

    fun editRemark(value: String) = editLecture { lecture ->
        when (lecture) {
            is SyllabusLecture -> lecture.copy(remark = value)
            is CustomLecture -> lecture.copy(remark = value)
        }
    }

    fun editColor(color: LectureColor) = editLecture { lecture ->
        when (lecture) {
            is SyllabusLecture -> lecture.copy(color = color)
            is CustomLecture -> lecture.copy(color = color)
        }
    }

    fun editSessionTime(index: Int, session: LectureSession) = editLecture { lecture ->
        val newSessions = lecture.lectureSessions.toMutableList().also { it[index] = session }
        when (lecture) {
            is SyllabusLecture -> lecture.copy(lectureSessions = newSessions)
            is CustomLecture -> lecture.copy(lectureSessions = newSessions)
        }
    }

    fun editSessionLocation(index: Int, location: String) = editLecture { lecture ->
        val newSessions = lecture.lectureSessions.toMutableList().also {
            it[index] = it[index].copy(place = location)
        }
        when (lecture) {
            is SyllabusLecture -> lecture.copy(lectureSessions = newSessions)
            is CustomLecture -> lecture.copy(lectureSessions = newSessions)
        }
    }

    fun addSession() = editLecture { lecture ->
        val lastSession = lecture.lectureSessions.lastOrNull()
        val newSession = lastSession?.copy(id = null) ?: LectureSession.Default
        val newSessions = lecture.lectureSessions + newSession
        when (lecture) {
            is SyllabusLecture -> lecture.copy(lectureSessions = newSessions)
            is CustomLecture -> lecture.copy(lectureSessions = newSessions)
        }
    }

    private fun editLecture(transform: (LocalLecture) -> LocalLecture) {
        _uiState.update { it.copy(lecture = transform(it.lecture)) }
    }

    // endregion

    // region 다이얼로그

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.None) }
    }

    fun requestDeleteSessionDialog(index: Int) {
        _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.DeleteSession(index)) }
    }

    fun confirmDeleteSession() {
        val dialog = _uiState.value.dialogState as? CurrentTableLectureDetailUiState.DialogState.DeleteSession ?: return

        editLecture { lecture ->
            val newSessions = lecture.lectureSessions.toMutableList().also { it.removeAt(dialog.index) }
            when (lecture) {
                is SyllabusLecture -> lecture.copy(lectureSessions = newSessions)
                is CustomLecture -> lecture.copy(lectureSessions = newSessions)
            }
        }
        _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.None) }
    }

    fun requestDeleteLecture() {
        _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.DeleteLecture) }
    }

    fun confirmDeleteLecture() {
        viewModelScope.launch {
            currentTableLectureRepository.removeLecture(originalLecture.id)
                .onSuccess {
                    _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.None) }
                    _uiEvent.emit(CurrentTableLectureDetailUiEvent.LectureDeleted)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.None) }
                    handleError(error)
                }
        }
    }

    fun requestResetLecture() {
        _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.ResetLecture) }
    }

    fun confirmResetLecture() {
        viewModelScope.launch {
            currentTableLectureRepository.resetLecture(originalLecture.id)
                .onSuccess { resetLecture ->
                    originalLecture = resetLecture
                    val buildings = fetchBuildings(resetLecture)
                    _uiState.update {
                        it.copy(
                            lecture = resetLecture,
                            buildings = buildings,
                            editMode = false,
                            dialogState = CurrentTableLectureDetailUiState.DialogState.None,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.None) }
                    handleError(error)
                }
        }
    }

    // endregion

    // region 강의 저장 (시간 겹침 처리 포함)

    fun confirmForceUpdateLecture() {
        val state = _uiState.value

        viewModelScope.launch {
            currentTableLectureRepository.updateLecture(state.lecture, isForced = true)
                .onSuccess {
                    originalLecture = state.lecture
                    _uiState.update {
                        it.copy(
                            editMode = false,
                            dialogState = CurrentTableLectureDetailUiState.DialogState.None,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(dialogState = CurrentTableLectureDetailUiState.DialogState.None) }
                    handleError(error)
                }
        }
    }

    // endregion

    // region 북마크 / 빈자리 알림

    fun toggleBookmark() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.isBookmarked) {
                bookmarkRepository.deleteBookmark(courseBook, originalLecture)
                    .onSuccess { _uiState.update { it.copy(isBookmarked = false) } }
                    .onFailure { handleError(it) }
            } else {
                bookmarkRepository.addBookmark(courseBook, originalLecture)
                    .onSuccess { _uiState.update { it.copy(isBookmarked = true) } }
                    .onFailure { handleError(it) }
            }
        }
    }

    fun toggleVacancy() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.vacancyRegistered) {
                vacancyRepository.removeVacancyLecture(originalLecture)
                    .onSuccess { _uiState.update { it.copy(vacancyRegistered = false) } }
                    .onFailure { handleError(it) }
            } else {
                vacancyRepository.addVacancyLecture(originalLecture)
                    .onSuccess { _uiState.update { it.copy(vacancyRegistered = true) } }
                    .onFailure { handleError(it) }
            }
        }
    }

    private suspend fun fetchBookmarkState(courseBook: CourseBook): Boolean =
        when (val result = bookmarkRepository.isLectureBookmarked(courseBook, originalLecture)) {
            is Result.Success -> result.data
            is Result.Fail -> false
        }

    private suspend fun fetchVacancyState(): Boolean =
        when (val result = vacancyRepository.isVacancyRegistered(originalLecture)) {
            is Result.Success -> result.data
            is Result.Fail -> false
        }

    /**
     * 북마크/빈자리 알림 API에 사용할 강의 ID를 반환한다.
     * SyllabusLecture면 originalLectureId (수강편람 ID), 그 외에는 인스턴스 ID.
     */
    fun getLoggingLectureId(): String {
        return when (val lecture = originalLecture) {
            is SyllabusLecture -> lecture.originalLectureId
            else -> lecture.id
        }
    }

    // endregion

    // region 강의계획서

    fun openSyllabus() {
        viewModelScope.launch {
            val syllabusLecture = _uiState.value.lecture as? SyllabusLecture ?: return@launch
            lectureInfoRepository.getSyllabusUrl(
                courseBook,
                syllabusLecture.courseNumber,
                syllabusLecture.lectureNumber,
            ).onSuccess { url ->
                _uiEvent.emit(CurrentTableLectureDetailUiEvent.OpenUrl(url))
            }.onFailure { error ->
                handleError(error)
            }
        }
    }

    // endregion

    // region 리마인더

    fun changeLectureReminderOption(option: LectureWithReminderOption) {
        _uiState.update { it.copy(lectureWithReminderOption = option) }
        viewModelScope.launch { putLectureReminder(option) }
    }

    private data class ReminderState(
        val showPicker: Boolean = false,
        val enablePicker: Boolean = false,
        val option: LectureWithReminderOption = LectureWithReminderOption.Default,
    )

    private suspend fun fetchLectureReminder(lecture: Lecture, baseTable: TableSummary): ReminderState {
        if (!shouldFetchReminder(lecture, baseTable)) return ReminderState()

        var state = ReminderState()
        tableRepository.getTimetableLectureReminder(baseTable.id, lecture.id)
            .onSuccess { data ->
                state = ReminderState(
                    showPicker = true,
                    enablePicker = baseTable.isPrimary,
                    option = data,
                )
            }
            .onFailure { error ->
                when (error) {
                    is AuthError -> handleError(error)
                    is EOF -> {
                        state = ReminderState(
                            showPicker = true,
                            enablePicker = baseTable.isPrimary,
                            option = LectureWithReminderOption.Default.copy(lectureId = lecture.id),
                        )
                    }

                    else -> handleError(error)
                }
            }
        return state
    }

    private fun shouldFetchReminder(lecture: Lecture, baseTable: TableSummary): Boolean {
        if (lecture.lectureSessions.isEmpty()) return false
        val semesterStatus = semesterStatusRepository.semesterStatus.value ?: return false
        return semesterStatus.isActiveSemester(baseTable.courseBook)
    }

    private suspend fun putLectureReminder(option: LectureWithReminderOption) {
        tableRepository.updateTimetableLectureReminder(
            timetableId = tableId,
            lectureId = option.lectureId,
            offset = option.lectureReminderOffset,
        ).onSuccess {
            _uiEvent.emit(CurrentTableLectureDetailUiEvent.ReminderUpdateSuccess(option.lectureReminderOffset))
        }.onFailure { error ->
            handleReminderError(error, false)
        }
    }

    // endregion

    // region 데이터 fetch

    private suspend fun fetchReviewInfo(lecture: Lecture): LectureReviewInfo? {
        if (lecture !is SyllabusLecture) return null
        var reviewInfo: LectureReviewInfo? = null
        lectureInfoRepository.getReviewInfo(lecture.originalLectureId)
            .onSuccess { reviewInfo = it }
        return reviewInfo
    }

    fun refreshReviewInfo() {
        viewModelScope.launch {
            val reviewInfo = fetchReviewInfo(originalLecture)
            _uiState.update { it.copy(reviewInfo = reviewInfo) }
        }
    }

    fun openTimePicker(index: Int) {
        val session = _uiState.value.lecture.lectureSessions.getOrNull(index) ?: return
        _uiState.update { it.copy(sheetType = CurrentTableLectureDetailUiState.SheetType.TimePicker(index, session)) }
        viewModelScope.launch { _uiEvent.emit(CurrentTableLectureDetailUiEvent.OpenBottomSheet) }
    }

    fun openReview() {
        _uiState.update { it.copy(sheetType = CurrentTableLectureDetailUiState.SheetType.Review) }
        viewModelScope.launch { _uiEvent.emit(CurrentTableLectureDetailUiEvent.OpenBottomSheet) }
    }

    fun closeSheet() {
        if (_uiState.value.sheetType is CurrentTableLectureDetailUiState.SheetType.Review) {
            refreshReviewInfo()
        }
        viewModelScope.launch {
            _uiEvent.emit(CurrentTableLectureDetailUiEvent.CloseBottomSheet)
        }
    }

    private suspend fun fetchBuildings(lecture: Lecture): List<LectureBuildingDto> {
        val places = lecture.lectureSessions.map { it.place }.distinct()
        var buildings: List<LectureBuildingDto> = emptyList()
        lectureInfoRepository.getBuildings(places)
            .onSuccess { buildings = it }
        return buildings
    }

    // endregion

    // region 에러 처리

    private suspend fun handleError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(CurrentTableLectureDetailUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
                _uiEvent.emit(CurrentTableLectureDetailUiEvent.LoggedOut)
            }

            else -> {
                _uiEvent.emit(CurrentTableLectureDetailUiEvent.ShowToast(displayMessage))
            }
        }
    }

    private suspend fun handleReminderError(error: DomainError, isTablePrimary: Boolean) {
        when (error) {
            is AuthError -> handleError(error)

            is EOF -> {
                val defaultOption = LectureWithReminderOption.Default.copy(lectureId = originalLecture.id)
                _uiState.update {
                    it.copy(
                        showLectureReminderPicker = true,
                        enableLectureReminderPicker = isTablePrimary,
                        lectureWithReminderOption = defaultOption,
                    )
                }
            }

            else -> handleError(error)
        }
    }

    // endregion
}

// region UiState / UiEvent

data class CurrentTableLectureDetailUiState(
    val lecture: LocalLecture,
    val editMode: Boolean = false,
    val tableTheme: TableTheme = BuiltInTheme.SNUTT,
    val reviewInfo: LectureReviewInfo? = null,
    val buildings: List<LectureBuildingDto> = emptyList(),
    val isBookmarked: Boolean = false,
    val vacancyRegistered: Boolean = false,
    val showCategoryPre2025: Boolean = false,
    val disableMapFeature: Boolean = false,
    val showLectureReminderPicker: Boolean = false,
    val lectureWithReminderOption: LectureWithReminderOption = LectureWithReminderOption.Default,
    val enableLectureReminderPicker: Boolean = false,
    val dialogState: DialogState = DialogState.None,
    val sheetType: SheetType = SheetType.None,
) {
    sealed interface DialogState {
        data object None : DialogState
        data object ExitEditMode : DialogState
        data class DeleteSession(val index: Int) : DialogState
        data object DeleteLecture : DialogState
        data object ResetLecture : DialogState
        data class LectureTimeOverlap(val message: String) : DialogState
    }

    sealed interface SheetType {
        data object None : SheetType
        data class TimePicker(val index: Int, val session: LectureSession) : SheetType
        data object Review : SheetType
    }
}

sealed interface CurrentTableLectureDetailUiEvent {
    data class ShowToast(val message: String) : CurrentTableLectureDetailUiEvent
    data class ReminderUpdateSuccess(val offset: LectureReminderOffset) : CurrentTableLectureDetailUiEvent
    data class OpenUrl(val url: String) : CurrentTableLectureDetailUiEvent
    data object OpenBottomSheet : CurrentTableLectureDetailUiEvent
    data object CloseBottomSheet : CurrentTableLectureDetailUiEvent
    data object LectureDeleted : CurrentTableLectureDetailUiEvent
    data object LoggedOut : CurrentTableLectureDetailUiEvent
}

// endregion
