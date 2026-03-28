package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.current_table_lecture.CurrentTableLectureRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.LectureOverlap
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomLectureViewModel @Inject constructor(
    private val currentTableLectureRepository: CurrentTableLectureRepository,
    private val userRepository: UserRepository,
    private val remoteConfig: RemoteConfig,
    private val displayMessageResolver: DisplayMessageResolver,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AddCustomLectureUiState(lecture = CustomLecture.Empty),
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<AddCustomLectureUiEvent>(replay = 0)
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        observeThemeAndConfig()
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

    // region 강의 저장

    fun saveLecture() {
        viewModelScope.launch {
            currentTableLectureRepository.createCustomLecture(_uiState.value.lecture)
                .onSuccess {
                    _uiEvent.emit(AddCustomLectureUiEvent.LectureCreated)
                }
                .onFailure { error ->
                    when (error) {
                        is LectureOverlap -> _uiState.update {
                            it.copy(dialogState = AddCustomLectureUiState.DialogState.LectureTimeOverlap(error.displayMessage))
                        }

                        else -> handleError(error)
                    }
                }
        }
    }

    fun confirmForceCreateLecture() {
        viewModelScope.launch {
            currentTableLectureRepository.createCustomLecture(_uiState.value.lecture, isForced = true)
                .onSuccess {
                    _uiState.update { it.copy(dialogState = AddCustomLectureUiState.DialogState.None) }
                    _uiEvent.emit(AddCustomLectureUiEvent.LectureCreated)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(dialogState = AddCustomLectureUiState.DialogState.None) }
                    handleError(error)
                }
        }
    }

    // endregion

    // region 필드 편집

    fun editCourseTitle(value: String) = editLecture { it.copy(courseTitle = value) }
    fun editInstructor(value: String) = editLecture { it.copy(instructor = value) }
    fun editCredit(value: Long) = editLecture { it.copy(credit = value) }
    fun editRemark(value: String) = editLecture { it.copy(remark = value) }
    fun editColor(color: LectureColor) = editLecture { it.copy(color = color) }

    fun editSessionTime(index: Int, session: LectureSession) = editLecture { lecture ->
        lecture.copy(lectureSessions = lecture.lectureSessions.toMutableList().also { it[index] = session })
    }

    fun editSessionLocation(index: Int, location: String) = editLecture { lecture ->
        lecture.copy(
            lectureSessions = lecture.lectureSessions.toMutableList().also {
                it[index] = it[index].copy(place = location)
            },
        )
    }

    fun addSession() = editLecture { lecture ->
        val lastSession = lecture.lectureSessions.lastOrNull()
        val newSession = lastSession?.copy(id = null) ?: LectureSession.Default
        lecture.copy(lectureSessions = lecture.lectureSessions + newSession)
    }

    private fun editLecture(transform: (CustomLecture) -> CustomLecture) {
        _uiState.update { it.copy(lecture = transform(it.lecture)) }
    }

    // endregion

    // region 다이얼로그

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = AddCustomLectureUiState.DialogState.None) }
    }

    fun requestDeleteSessionDialog(index: Int) {
        _uiState.update { it.copy(dialogState = AddCustomLectureUiState.DialogState.DeleteSession(index)) }
    }

    fun confirmDeleteSession() {
        val dialog = _uiState.value.dialogState as? AddCustomLectureUiState.DialogState.DeleteSession ?: return
        editLecture { lecture ->
            lecture.copy(lectureSessions = lecture.lectureSessions.toMutableList().also { it.removeAt(dialog.index) })
        }
        _uiState.update { it.copy(dialogState = AddCustomLectureUiState.DialogState.None) }
    }

    // endregion

    // region 시간 선택 바텀시트

    fun openTimePicker(index: Int) {
        val session = _uiState.value.lecture.lectureSessions.getOrNull(index) ?: return
        _uiState.update { it.copy(sheetType = AddCustomLectureUiState.SheetType.TimePicker(index, session)) }
        viewModelScope.launch { _uiEvent.emit(AddCustomLectureUiEvent.OpenBottomSheet) }
    }

    fun closeSheet() {
        viewModelScope.launch { _uiEvent.emit(AddCustomLectureUiEvent.CloseBottomSheet) }
        _uiState.update { it.copy(sheetType = AddCustomLectureUiState.SheetType.None) }
    }

    // endregion

    // region 에러 처리

    private suspend fun handleError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(AddCustomLectureUiEvent.ShowToast(displayMessage))
                userRepository.postForceLogout()
                _uiEvent.emit(AddCustomLectureUiEvent.LoggedOut)
            }

            else -> {
                _uiEvent.emit(AddCustomLectureUiEvent.ShowToast(displayMessage))
            }
        }
    }

    // endregion
}

// region UiState / UiEvent

data class AddCustomLectureUiState(
    val lecture: CustomLecture,
    val tableTheme: TableTheme = BuiltInTheme.SNUTT,
    val disableMapFeature: Boolean = false,
    val dialogState: DialogState = DialogState.None,
    val sheetType: SheetType = SheetType.None,
) {
    sealed interface DialogState {
        data object None : DialogState
        data class DeleteSession(val index: Int) : DialogState
        data class LectureTimeOverlap(val message: String) : DialogState
    }

    sealed interface SheetType {
        data object None : SheetType
        data class TimePicker(val index: Int, val session: LectureSession) : SheetType
    }
}

sealed interface AddCustomLectureUiEvent {
    data class ShowToast(val message: String) : AddCustomLectureUiEvent
    data object OpenBottomSheet : AddCustomLectureUiEvent
    data object CloseBottomSheet : AddCustomLectureUiEvent
    data object LectureCreated : AddCustomLectureUiEvent
    data object LoggedOut : AddCustomLectureUiEvent
}

// endregion
