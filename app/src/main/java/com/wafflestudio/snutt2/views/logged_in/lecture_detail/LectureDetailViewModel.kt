package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.LectureReminderOffset
import com.wafflestudio.snutt2.domainmodel.LectureWithReminderOption
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.EOF
import com.wafflestudio.snutt2.lib.network.PastSemester
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureReviewDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.TableTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ModeType {
    object Normal : ModeType()
    data class Editing(val adding: Boolean = false) : ModeType()
    object Viewing : ModeType()
}

@OptIn(FlowPreview::class)
@HiltViewModel
class LectureDetailViewModel @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val lectureSearchRepository: LectureSearchRepository,
    private val tableRepository: TableRepository,
    private val userRepository: UserRepository,
    private val apiOnError: ApiOnError,
    private val displayMessageResolver: DisplayMessageResolver,
    getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
) : ViewModel() {
    val currentTable: StateFlow<TableDto?> = currentTableRepository.currentTable

    private val _table = MutableStateFlow<TableDto?>(null)
    val table = _table.asStateFlow()
    val currentTableTheme: StateFlow<TableTheme> = getCurrentTableThemeUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, BuiltInTheme.SNUTT)

    private val _modeType = MutableStateFlow<ModeType>(ModeType.Normal)
    val modeType = _modeType.asStateFlow()

    private var fixedLectureDetail = LectureDto.Default

    private val _editingLectureDetail = MutableStateFlow(fixedLectureDetail)
    val editingLectureDetail = _editingLectureDetail.asStateFlow()
    val editingLectureReview = _editingLectureDetail.map { lecture ->
        /**
         * 로컬 저장소에는 리뷰 정보를 저장하지 않으므로, 시간표탭에서 강의상세로 진입하면 editingLectureDetail.value.review가 null이다
         * 따라서 getLectureReview()로 리뷰 정보만을 따로 불러온다
         */
        lecture.review?.reviewCount?.let { lecture.review }
            ?: runCatching {
                getLectureReview()
            }.onFailure(apiOnError).getOrNull()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, editingLectureDetail.value.review)

    val editingLectureBuildings = editingLectureDetail.map { lecture ->
        val places = lecture.class_time_json.map { it.place }.distinct().joinToString(",")
        runCatching {
            lectureSearchRepository.getBuildings(places)
        }.getOrElse { emptyList() }
    }

    private val _showLectureReminderPicker = MutableStateFlow(false)
    val showLectureReminderPicker = _showLectureReminderPicker.asStateFlow()

    private val _enableLectureReminderPicker = MutableStateFlow(false)
    val enableLectureReminderPicker = _enableLectureReminderPicker.asStateFlow()

    private val _lectureWithReminderOption = MutableStateFlow(LectureWithReminderOption.Default)
    val lectureWithReminderOption = _lectureWithReminderOption.asStateFlow()

    private val _lectureDetailUiEvent: MutableSharedFlow<LectureDetailUiEvent> = MutableSharedFlow(replay = 0)
    val lectureDetailUiEvent = _lectureDetailUiEvent.asSharedFlow()

    // 여기부터 dispose(), 그리고 관련 코드는 리팩토링을 한다면 필요가 없다. 지금은 LectureDetailPage가 dispose 될 때 LectureDetailViewModel은 여전히 살아있기 때문에 필요한 코드.
    private var lectureReminderJob: Job? = null

    private fun init() {
        lectureReminderJob = lectureWithReminderOption
            .drop(1) // 이 또한 리팩토링을 한다면 필요가 없다.
            .debounce(200L)
            .distinctUntilChanged()
            .onEach { lectureWithReminderOption ->
                putTimetableLectureReminder(lectureWithReminderOption)
            }
            .launchIn(viewModelScope)
    }

    fun dispose() {
        lectureReminderJob?.cancel()
        lectureReminderJob = null
        resetUiEvent()
    }

    private fun resetUiEvent() {
        viewModelScope.launch {
            _lectureDetailUiEvent.emit(LectureDetailUiEvent.ShowToast(""))
        }
    }

    fun setEditMode(adding: Boolean = false) {
        viewModelScope.launch { _modeType.emit(ModeType.Editing(adding)) }
    }

    fun initializeEditingLectureDetail(lecture: LectureDto?, modeType: ModeType, table: TableDto? = null) {
        fixedLectureDetail = lecture ?: LectureDto.Default // null 문제 (reset에서 비롯됨)
        viewModelScope.launch {
            lectureReminderJob?.cancel()
            _table.emit(table)
            _modeType.emit(modeType)
            _editingLectureDetail.emit(fixedLectureDetail)
            if (modeType !is ModeType.Editing) { // Editing으로 여는 것은 강의를 추가할 때 뿐이고, 이때는 아직 추가되지 않은 강의이므로 lecture reminder를 얻을 수 없다.
                getTimetableLectureReminder(fixedLectureDetail)
            }
            init()
        }
    }

    fun abandonEditingLectureDetail() {
        initializeEditingLectureDetail(fixedLectureDetail, ModeType.Normal, _table.value)
    }

    fun editLectureDetail(editedLecture: LectureDto) {
        viewModelScope.launch { _editingLectureDetail.emit(editedLecture) }
    }

    suspend fun updateLecture(is_forced: Boolean = false) {
        val param = buildPutLectureParams()
        param.isForced = is_forced
        currentTableRepository.updateLecture(_editingLectureDetail.value.id, param)
        initializeEditingLectureDetail(_editingLectureDetail.value, ModeType.Normal, _table.value)
    }

    suspend fun removeLecture() {
        currentTableRepository.removeLecture(_editingLectureDetail.value.id)
    }

    suspend fun resetLecture() {
        val originLecture = currentTableRepository.resetLecture(_editingLectureDetail.value.id)
        initializeEditingLectureDetail(originLecture, ModeType.Normal)
    }

    suspend fun createLecture(is_forced: Boolean = false) {
        val param = buildPostLectureParams()
        param.isForced = is_forced
        currentTableRepository.createCustomLecture(param)
    }

    suspend fun getCourseBookUrl(): String {
        val courseNumber = _editingLectureDetail.value.course_number
            ?: (throw IllegalStateException("lecture with no course number")) // FIXME
        val lectureNumber = _editingLectureDetail.value.lecture_number
            ?: (throw IllegalStateException("lecture with no course number")) // FIXME
        return currentTableRepository.getLectureSyllabusUrl(courseNumber, lectureNumber)
    }

    suspend fun getLectureReview(id: String? = null): LectureReviewDto? {
        /**
         * 알림함에서 강의 상세로 진입할 때 호출하는 경우에만 id가 null이 아니다.
         * 이때는 ModeType.Viewing임에도 lecture_id를 ev 서버에 보내야 하기 때문에, DeeplinkExecutor에서 id를 제공한다.
         */
        val originalLectureId = id
            ?: if (modeType.value == ModeType.Viewing) {
                editingLectureDetail.value.id
            } else {
                editingLectureDetail.value.lecture_id
            }
        return originalLectureId?.let { lectureId ->
            currentTableRepository.getLectureReviewSummary(lectureId)
        }
    }
    suspend fun getTimetableLectureReminder(lecture: LectureDto = fixedLectureDetail) {
        _showLectureReminderPicker.emit(false)
        _lectureWithReminderOption.emit(LectureWithReminderOption.Default)
        _enableLectureReminderPicker.emit(false)
        val table = _table.value
        if (table != null && lecture.class_time_json.isNotEmpty() && lecture.lecture_id != null) {
            tableRepository.getTimetableLectureReminder(currentTable.value?.id ?: "", lecture.id)
                .onSuccess { data ->
                    _showLectureReminderPicker.emit(true)
                    _enableLectureReminderPicker.emit(table.isPrimary)
                    _lectureWithReminderOption.emit(data)
                }
                .onFailure { error ->
                    handleLectureDetailError(error, table.isPrimary)
                }
        }
    }

    fun changeLectureReminderOption(option: LectureWithReminderOption) {
        viewModelScope.launch {
            _lectureWithReminderOption.emit(option)
        }
    }

    private suspend fun putTimetableLectureReminder(lectureWithReminderOption: LectureWithReminderOption) {
        tableRepository.updateTimetableLectureReminder(
            timetableId = currentTable.value?.id ?: "",
            lectureId = lectureWithReminderOption.lectureId,
            option = lectureWithReminderOption,
        ).onSuccess {
            _lectureDetailUiEvent.emit(
                LectureDetailUiEvent.ShowSnackBarByEvent(
                    when (lectureWithReminderOption.lectureReminderOffset) {
                        LectureReminderOffset.NONE -> LectureDetailEvent.LECTURE_REMINDER_UPDATE_SUCCESS_NONE
                        LectureReminderOffset.TEN_MINUTES_BEFORE -> LectureDetailEvent.LECTURE_REMINDER_UPDATE_SUCCESS_TEN_MINUTES_BEFORE
                        LectureReminderOffset.AT_START_TIME -> LectureDetailEvent.LECTURE_REMINDER_UPDATE_SUCCESS_AT_START_TIME
                        LectureReminderOffset.TEN_MINUTES_AFTER -> LectureDetailEvent.LECTURE_REMINDER_UPDATE_SUCCESS_TEN_MINUTES_AFTER
                    },
                ),
            )
        }.onFailure { error ->
            handleLectureDetailError(error, false)
        }
    }

    private fun buildPutLectureParams(): PutLectureParams {
        return PutLectureParams(
            id = _editingLectureDetail.value.id,
            course_title = _editingLectureDetail.value.course_title,
            instructor = _editingLectureDetail.value.instructor,
            colorIndex = _editingLectureDetail.value.colorIndex,
            color = _editingLectureDetail.value.color,
            department = _editingLectureDetail.value.department,
            academic_year = _editingLectureDetail.value.academic_year,
            credit = _editingLectureDetail.value.credit,
            classification = _editingLectureDetail.value.classification,
            category = _editingLectureDetail.value.category,
            categoryPre2025 = _editingLectureDetail.value.categoryPre2025,
            remark = _editingLectureDetail.value.remark,
            class_time_json = _editingLectureDetail.value.class_time_json,
        )
    }

    private fun buildPostLectureParams(): PostCustomLectureParams {
        return PostCustomLectureParams(
            course_title = _editingLectureDetail.value.course_title,
            instructor = _editingLectureDetail.value.instructor,
            colorIndex = _editingLectureDetail.value.colorIndex,
            color = _editingLectureDetail.value.color,
            credit = _editingLectureDetail.value.credit,
            remark = _editingLectureDetail.value.remark,
            class_time_json = _editingLectureDetail.value.class_time_json,
        )
    }

    private suspend fun handleLectureDetailError(error: DomainError, isTablePrimary: Boolean) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _lectureDetailUiEvent.emit(LectureDetailUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
                _lectureDetailUiEvent.emit(LectureDetailUiEvent.LoggedOut)
            }
            is EOF -> {
                _showLectureReminderPicker.emit(true)
                _enableLectureReminderPicker.emit(isTablePrimary)
                _lectureWithReminderOption.emit(LectureWithReminderOption.Default.copy(lectureId = fixedLectureDetail.id))
            }
            is PastSemester -> {
            }
            else -> {
                _lectureDetailUiEvent.emit(LectureDetailUiEvent.ShowToast(displayMessage))
            }
        }
    }
}

sealed interface LectureDetailUiEvent {
    data class ShowToast(val message: String) : LectureDetailUiEvent
    data class ShowSnackBarByEvent(val event: LectureDetailEvent) : LectureDetailUiEvent
    data object LoggedOut : LectureDetailUiEvent
}

enum class LectureDetailEvent {
    LECTURE_REMINDER_UPDATE_SUCCESS_NONE,
    LECTURE_REMINDER_UPDATE_SUCCESS_TEN_MINUTES_BEFORE,
    LECTURE_REMINDER_UPDATE_SUCCESS_AT_START_TIME,
    LECTURE_REMINDER_UPDATE_SUCCESS_TEN_MINUTES_AFTER,
}
