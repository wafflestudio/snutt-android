package com.wafflestudio.snutt2.views.logged_in.home.bookmark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.domain.GetCurrentTableThemeUseCase
import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.LectureOverlap
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import com.wafflestudio.snutt2.views.logged_in.home.search.LectureState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map
import kotlin.collections.toSet

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    val currentTableRepository: CurrentTableRepository,
    val userRepository: UserRepository,
    val vacancyRepository: VacancyRepository,
    private val getCurrentTableThemeUseCase: GetCurrentTableThemeUseCase,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {
    private val bookmarkLectures = MutableStateFlow<List<SearchedLecture>>(emptyList())
    private val currentTable = currentTableRepository.currentTableRefactored
    private val tableTheme = getCurrentTableThemeUseCase()
    private val trimParam = userRepository.tableTrimParam
    private val tableLectureCustomOption = userRepository.tableLectureCustomOption
    private val vacancyLectures = MutableStateFlow<List<SearchedLecture>>(emptyList())
    private val selectedLecture = MutableStateFlow<SearchedLecture?>(null)

    private val _uiEvent = MutableSharedFlow<BookmarkUiEvent>()
    val uiEvent: SharedFlow<BookmarkUiEvent> = _uiEvent.asSharedFlow()

    init {
        fetchBookmarkList()
        viewModelScope.launch {
            currentTableRepository.updateCurrentTable()
        }
    }

    private val bookmarkList = kotlinx.coroutines.flow.combine(
        bookmarkLectures,
        currentTable,
        vacancyLectures,
        selectedLecture,
    ) { bookmarkLectures, currentTable, vacancyLectures, selectedLecture ->
        val currentTableIds =
            currentTable?.lectures
                ?.map {
                    when (it) {
                        is SyllabusLecture -> it.originalLectureId
                        is CustomLecture -> it.id
                    }
                }
                ?.toSet()
                ?: emptySet()

        val vacancyIds =
            vacancyLectures
                .map { it.id }
                .toSet()

        bookmarkLectures.map { lecture ->
            DataWithState(
                item = lecture,
                state = LectureState(
                    selected = lecture == selectedLecture,
                    contained = lecture.id in currentTableIds,
                    isBookmarked = true,
                    isVacancyRegistered = lecture.id in vacancyIds,
                ),
            )
        }
    }

    private val dialogState = MutableStateFlow<BookmarkUiState.DialogState>(BookmarkUiState.DialogState.None)
    private val bottomSheetState = MutableStateFlow<BookmarkUiState.BottomSheetState>(BookmarkUiState.BottomSheetState.None)

    val uiState = combine(
        bookmarkList,
        trimParam,
        tableLectureCustomOption,
        currentTable,
        combine (
            tableTheme,
            dialogState,
            bottomSheetState,
            ::Triple,
        )
    ) { bookmarkList, trimParam, tableLectureCustomOption, currentTable, (tableTheme, dialogState, bottomSheetState) ->
        BookmarkUiState.Success(
            currentTable = currentTable,
            tableTheme = tableTheme,
            bookmarkList = bookmarkList,
            dialogState = dialogState,
            bottomSheetState = bottomSheetState,
        )
    }

    fun onClickLectureDetail(lecture: SearchedLecture) {
        viewModelScope.launch {
            bottomSheetState.emit(BookmarkUiState.BottomSheetState.LectureDetail(lecture))
        }
    }

    fun onClickReview(lecture: SearchedLecture) {
        viewModelScope.launch {
            bottomSheetState.emit(BookmarkUiState.BottomSheetState.Review(lecture))
        }
    }

    fun onClickBookmark(lecture: SearchedLecture, isBookmarked: Boolean) {
        viewModelScope.launch {
            if (isBookmarked) {
                dialogState.emit(BookmarkUiState.DialogState.DeleteBookmark(lecture))
            }
        }
    }

    fun onConfirmDeleteBookmark(lecture: SearchedLecture) {
        viewModelScope.launch {
            currentTableRepository.deleteBookmark(lecture)
                .onSuccess {
                    fetchBookmarkList()
                    dialogState.emit(BookmarkUiState.DialogState.None)
                }
                .onFailure {
                    handleError(it)
                }
        }
    }

    fun onConfirmDeleteVacancyNotification(lecture: SearchedLecture) {
        viewModelScope.launch {
            vacancyRepository.removeVacancyLectureNew(lecture.id)
                .onSuccess {
                    fetchBookmarkList()
                    dialogState.emit(BookmarkUiState.DialogState.None)
                }
                .onFailure {
                    handleError(it)
                }
        }
    }

    fun onDismissDialog() {
        viewModelScope.launch {
            dialogState.emit(BookmarkUiState.DialogState.None)
        }
    }

    fun onDismissBottomSheet() {
        viewModelScope.launch {
            bottomSheetState.emit(BookmarkUiState.BottomSheetState.None)
        }
    }

    fun onClickVacancy(lecture: SearchedLecture, isVacancyRegistered: Boolean) {
        viewModelScope.launch {
            if (isVacancyRegistered) {
                dialogState.emit(BookmarkUiState.DialogState.DeleteVacancyNotification(lecture))
            } else {
                vacancyRepository.addVacancyLectureNew(lecture.id)
                    .onSuccess {
                        vacancyLectures.update { vacancyLectures ->
                            vacancyLectures + lecture
                        }
                    }
                    .onFailure {
                        handleError(it)
                    }
            }
        }
    }

    fun onToggleLectureSelection(lecture: SearchedLecture) {
        viewModelScope.launch {
            selectedLecture.update { selectedLecture ->
                if (selectedLecture == lecture) {
                    null
                } else {
                    lecture
                }
            }
        }
    }

    fun onConfirmForceAddLecture(lecture: SearchedLecture) {
        viewModelScope.launch {
            currentTableRepository.addLectureNew(lecture.id, true)
                .onSuccess {
                    selectedLecture.update { null }
                    dialogState.emit(BookmarkUiState.DialogState.None)
                }
                .onFailure {
                    handleError(it)
                }
        }
    }

    fun onToggleLectureContained(lecture: SearchedLecture, contained: Boolean) {
        viewModelScope.launch {
            if (contained) {
                val localLectureId = currentTable.value?.lectures?.find {
                    when (it) {
                        is SyllabusLecture -> it.originalLectureId == lecture.id
                        is CustomLecture -> it.id == lecture.id
                    }
                }?.id
                localLectureId?.let {
                    currentTableRepository.removeLectureNew(it)
                        .onSuccess {
                            currentTableRepository.updateCurrentTable()
                            selectedLecture.update { null }
                        }
                        .onFailure { error ->
                            handleError(error)
                        }
                }
            } else {
                currentTableRepository.addLectureNew(lecture.id, false)
                    .onSuccess {
                        selectedLecture.update { null }
                    }
                    .onFailure { error ->
                        if (error is LectureOverlap) {
                            val displayMessage = displayMessageResolver.getDisplayMessage(error)
                            dialogState.emit(BookmarkUiState.DialogState.LectureTimeOverlap(lecture, displayMessage))
                        } else {
                            handleError(error)
                        }
                    }
            }
        }
    }

    private fun fetchBookmarkList() {
        viewModelScope.launch {
            currentTableRepository.getBookmarksNew()
                .onSuccess {
                    bookmarkLectures.value = it
                }
                .onFailure {
                    handleError(it)
                }
            vacancyRepository.getVacancyLecturesNew()
                .onSuccess {
                    vacancyLectures.value = it
                }
                .onFailure {
                    handleError(it)
                }
        }
    }

    private suspend fun handleError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _uiEvent.emit(BookmarkUiEvent.ShowToast(displayMessage))
                userRepository.performLogout()
                _uiEvent.emit(BookmarkUiEvent.NavigateToOnboard)
            }
            else -> {
                _uiEvent.emit(BookmarkUiEvent.ShowToast(displayMessage))
            }
        }
    }
}

sealed interface BookmarkUiEvent {
    data class ShowToast(val message: String) : BookmarkUiEvent
    data object NavigateToOnboard : BookmarkUiEvent
}

sealed interface BookmarkUiState {
    data class Success(
        val currentTable: Table?,
        val tableTheme: TableTheme,
        val bookmarkList: List<DataWithState<SearchedLecture, LectureState>>,
        val dialogState: DialogState = DialogState.None,
        val bottomSheetState: BottomSheetState = BottomSheetState.None,
    ) : BookmarkUiState

    sealed interface DialogState {
        data object None : DialogState
        data class DeleteBookmark(
            val lecture: SearchedLecture,
        ) : DialogState

        data class DeleteVacancyNotification(
            val lecture: SearchedLecture,
        ) : DialogState

        data class LectureTimeOverlap(
            val lecture: SearchedLecture,
            val displayMessage: String,
        ) : DialogState
    }

    sealed interface BottomSheetState {
        data object None : BottomSheetState
        data class LectureDetail(
            val lecture: SearchedLecture,
        ) : BottomSheetState

        data class Review(
            val lecture: SearchedLecture,
        ) : BottomSheetState
    }

    data object Loading : BookmarkUiState
    data object Error : BookmarkUiState
    data object Empty : BookmarkUiState
}
