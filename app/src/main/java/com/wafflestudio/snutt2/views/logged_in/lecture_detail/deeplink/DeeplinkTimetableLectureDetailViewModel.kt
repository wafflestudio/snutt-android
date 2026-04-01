package com.wafflestudio.snutt2.views.logged_in.lecture_detail.deeplink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.bookmark.BookmarkRepository
import com.wafflestudio.snutt2.data.lecture_info.LectureInfoRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LectureSyllabusInfo
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.TimetableLectureNotFound
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeeplinkTimetableLectureDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookmarkRepository: BookmarkRepository,
    private val vacancyRepository: VacancyRepository,
    private val lectureInfoRepository: LectureInfoRepository,
    private val tableRepository: TableRepository,
    private val displayMessageResolver: DisplayMessageResolver,
    private val remoteConfig: RemoteConfig,
) : ViewModel() {

    private val lectureId: String = checkNotNull(savedStateHandle["lectureId"])
    private val timetableId: String = checkNotNull(savedStateHandle["timetableId"])

    private lateinit var courseBook: CourseBook

    private val _uiState = MutableStateFlow<DeeplinkTimetableLectureDetailUiState>(
        DeeplinkTimetableLectureDetailUiState.Loading,
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<DeeplinkTimetableLectureDetailUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadLecture()
        observeConfig()
    }

    private fun loadLecture() {
        viewModelScope.launch {
            when (val result = tableRepository.getTableById(timetableId)) {
                is Result.Success -> {
                    val table = result.data
                    courseBook = table.summary.courseBook
                    val lecture = table.lectures.find {
                        it is SyllabusLecture && it.originalLectureId == lectureId
                    }
                    if (lecture != null) {
                        _uiState.update {
                            DeeplinkTimetableLectureDetailUiState.Success(
                                lecture = lecture,
                                showCategoryPre2025 = (courseBook.year * 10 + courseBook.semester) > 20250L,
                            )
                        }
                        loadSecondaryData(lecture, courseBook)
                    } else {
                        _uiEvent.emit(
                            DeeplinkTimetableLectureDetailUiEvent.ShowToastAndNavigateBack(
                                displayMessageResolver.getDisplayMessage(TimetableLectureNotFound),
                            ),
                        )
                    }
                }

                is Result.Fail -> {
                    _uiEvent.emit(
                        DeeplinkTimetableLectureDetailUiEvent.ShowToastAndNavigateBack(
                            displayMessageResolver.getDisplayMessage(result.error),
                        ),
                    )
                }
            }
        }
    }

    private fun loadSecondaryData(lecture: Lecture, courseBook: CourseBook) {
        viewModelScope.launch {
            val buildingsDeferred = async { fetchBuildings(lecture) }
            val isBookmarkedDeferred = async { fetchIsBookmarked(courseBook, lecture) }
            val vacancyRegisteredDeferred = async { fetchVacancyRegistered(lecture) }
            val reviewInfoDeferred = async { fetchReviewInfo(lecture) }
            val buildings = buildingsDeferred.await()
            val isBookmarked = isBookmarkedDeferred.await()
            val vacancyRegistered = vacancyRegisteredDeferred.await()
            val reviewInfo = reviewInfoDeferred.await()
            _uiState.update { state ->
                when (state) {
                    is DeeplinkTimetableLectureDetailUiState.Success ->
                        state.copy(
                            buildings = buildings,
                            isBookmarked = isBookmarked,
                            vacancyRegistered = vacancyRegistered,
                            reviewInfo = reviewInfo,
                        )

                    else -> state
                }
            }
        }
    }

    private fun observeConfig() {
        viewModelScope.launch {
            remoteConfig.disableMapFeature.collect { disableMap ->
                _uiState.update { state ->
                    when (state) {
                        is DeeplinkTimetableLectureDetailUiState.Success ->
                            state.copy(disableMapFeature = disableMap)

                        else -> state
                    }
                }
            }
        }
    }

    private suspend fun fetchBuildings(lecture: Lecture): List<LectureBuildingDto> {
        val places = lecture.lectureSessions.map { it.place }.distinct()
        var buildings: List<LectureBuildingDto> = emptyList()
        lectureInfoRepository.getBuildings(places)
            .onSuccess { buildings = it }
        return buildings
    }

    private suspend fun fetchIsBookmarked(courseBook: CourseBook, lecture: Lecture): Boolean {
        var isBookmarked = false
        bookmarkRepository.isLectureBookmarked(courseBook, lecture)
            .onSuccess { isBookmarked = it }
        return isBookmarked
    }

    private suspend fun fetchVacancyRegistered(lecture: Lecture): Boolean {
        return when (val result = vacancyRepository.isVacancyRegistered(lecture)) {
            is Result.Success -> result.data
            is Result.Fail -> false
        }
    }

    private suspend fun fetchReviewInfo(lecture: Lecture): LectureReviewInfo? {
        if (lecture !is SyllabusLecture) return null
        var reviewInfo: LectureReviewInfo? = null
        lectureInfoRepository.getReviewInfo(lecture.originalLectureId)
            .onSuccess { reviewInfo = it }
        return reviewInfo
    }

    fun toggleBookmark() {
        viewModelScope.launch {
            val state = _uiState.value as? DeeplinkTimetableLectureDetailUiState.Success ?: return@launch
            if (state.isBookmarked) {
                bookmarkRepository.deleteBookmark(courseBook, state.lecture)
                    .onSuccess { _uiState.update { (it as? DeeplinkTimetableLectureDetailUiState.Success)?.copy(isBookmarked = false) ?: it } }
                    .onFailure { handleError(it) }
            } else {
                bookmarkRepository.addBookmark(courseBook, state.lecture)
                    .onSuccess { _uiState.update { (it as? DeeplinkTimetableLectureDetailUiState.Success)?.copy(isBookmarked = true) ?: it } }
                    .onFailure { handleError(it) }
            }
        }
    }

    fun toggleVacancy() {
        viewModelScope.launch {
            val state = _uiState.value as? DeeplinkTimetableLectureDetailUiState.Success ?: return@launch
            if (state.vacancyRegistered) {
                vacancyRepository.removeVacancyLecture(state.lecture)
                    .onSuccess { _uiState.update { (it as? DeeplinkTimetableLectureDetailUiState.Success)?.copy(vacancyRegistered = false) ?: it } }
                    .onFailure { handleError(it) }
            } else {
                vacancyRepository.addVacancyLecture(state.lecture)
                    .onSuccess { _uiState.update { (it as? DeeplinkTimetableLectureDetailUiState.Success)?.copy(vacancyRegistered = true) ?: it } }
                    .onFailure { handleError(it) }
            }
        }
    }

    fun openSyllabus() {
        viewModelScope.launch {
            val lecture = (_uiState.value as? DeeplinkTimetableLectureDetailUiState.Success)?.lecture as? LectureSyllabusInfo ?: return@launch
            lectureInfoRepository.getSyllabusUrl(
                courseBook,
                lecture.courseNumber,
                lecture.lectureNumber,
            ).onSuccess { url ->
                _uiEvent.emit(DeeplinkTimetableLectureDetailUiEvent.OpenUrl(url))
            }.onFailure { handleError(it) }
        }
    }

    fun onFloatingButtonClick() {
        viewModelScope.launch {
            runCatching {
                tableRepository.fetchAndSelectTable(timetableId)
            }
            _uiEvent.emit(DeeplinkTimetableLectureDetailUiEvent.NavigateToHome)
        }
    }

    private fun handleError(error: com.wafflestudio.snutt2.lib.network.DomainError) {
        viewModelScope.launch {
            _uiEvent.emit(
                DeeplinkTimetableLectureDetailUiEvent.ShowToast(
                    displayMessageResolver.getDisplayMessage(error),
                ),
            )
        }
    }
}

sealed interface DeeplinkTimetableLectureDetailUiState {
    data object Loading : DeeplinkTimetableLectureDetailUiState

    data class Success(
        val lecture: Lecture,
        val buildings: List<LectureBuildingDto> = emptyList(),
        val isBookmarked: Boolean = false,
        val vacancyRegistered: Boolean = false,
        val reviewInfo: LectureReviewInfo? = null,
        val showCategoryPre2025: Boolean = true,
        val disableMapFeature: Boolean = false,
    ) : DeeplinkTimetableLectureDetailUiState
}

sealed interface DeeplinkTimetableLectureDetailUiEvent {
    data object NavigateToHome : DeeplinkTimetableLectureDetailUiEvent
    data class ShowToastAndNavigateBack(val message: String) : DeeplinkTimetableLectureDetailUiEvent
    data class ShowToast(val message: String) : DeeplinkTimetableLectureDetailUiEvent
    data class OpenUrl(val url: String) : DeeplinkTimetableLectureDetailUiEvent
}
