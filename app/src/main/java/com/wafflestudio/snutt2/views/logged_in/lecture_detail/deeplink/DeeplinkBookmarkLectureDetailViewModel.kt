package com.wafflestudio.snutt2.views.logged_in.lecture_detail.deeplink

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import com.wafflestudio.snutt2.data.bookmark.BookmarkRepository
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LectureSyllabusInfo
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.BookmarkLectureNotFound
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeeplinkBookmarkLectureDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookmarkRepository: BookmarkRepository,
    private val vacancyRepository: VacancyRepository,
    private val lectureSearchRepository: LectureSearchRepository,
    private val userRepository: UserRepository,
    private val displayMessageResolver: DisplayMessageResolver,
    private val remoteConfig: RemoteConfig,
) : ViewModel() {

    private val lectureId: String = checkNotNull(savedStateHandle["lectureId"])
    private val year: Long = checkNotNull(savedStateHandle["year"])
    private val semester: Long = checkNotNull(savedStateHandle["semester"])

    private val courseBook = CourseBook(year = year, semester = semester)

    private val _uiState = MutableStateFlow<DeeplinkBookmarkLectureDetailUiState>(
        DeeplinkBookmarkLectureDetailUiState.Loading,
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<DeeplinkBookmarkLectureDetailUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val accessToken: StateFlow<String?> = userRepository.accessToken

    init {
        loadLecture()
        observeConfig()
    }

    private fun loadLecture() {
        viewModelScope.launch {
            when (val result = bookmarkRepository.fetchBookmarks(courseBook)) {
                is Result.Success -> {
                    val lecture = result.data.find { it.id == lectureId }
                    if (lecture != null) {
                        _uiState.update {
                            DeeplinkBookmarkLectureDetailUiState.Success(
                                lecture = lecture,
                                reviewInfo = lecture.reviewInfo,
                                showCategoryPre2025 = (year * 10 + semester) > 20250L,
                            )
                        }
                        loadSecondaryData(lecture)
                    } else {
                        _uiEvent.emit(
                            DeeplinkBookmarkLectureDetailUiEvent.ShowToastAndNavigateBack(
                                displayMessageResolver.getDisplayMessage(BookmarkLectureNotFound),
                            ),
                        )
                    }
                }

                is Result.Fail -> {
                    _uiEvent.emit(
                        DeeplinkBookmarkLectureDetailUiEvent.ShowToastAndNavigateBack(
                            displayMessageResolver.getDisplayMessage(result.error),
                        ),
                    )
                }
            }
        }
    }

    private fun loadSecondaryData(lecture: Lecture) {
        viewModelScope.launch {
            val buildingsDeferred = async { fetchBuildings(lecture) }
            val isBookmarkedDeferred = async { fetchIsBookmarked(lecture) }
            val vacancyRegisteredDeferred = async { fetchVacancyRegistered(lecture) }
            val buildings = buildingsDeferred.await()
            val isBookmarked = isBookmarkedDeferred.await()
            val vacancyRegistered = vacancyRegisteredDeferred.await()
            _uiState.update { state ->
                when (state) {
                    is DeeplinkBookmarkLectureDetailUiState.Success ->
                        state.copy(
                            buildings = buildings,
                            isBookmarked = isBookmarked,
                            vacancyRegistered = vacancyRegistered,
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
                        is DeeplinkBookmarkLectureDetailUiState.Success ->
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
        lectureSearchRepository.getBuildings(places)
            .onSuccess { buildings = it }
        return buildings
    }

    private suspend fun fetchIsBookmarked(lecture: Lecture): Boolean {
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

    fun toggleBookmark() {
        viewModelScope.launch {
            val state = _uiState.value as? DeeplinkBookmarkLectureDetailUiState.Success ?: return@launch
            if (state.isBookmarked) {
                bookmarkRepository.deleteBookmark(courseBook, state.lecture)
                    .onSuccess { _uiState.update { (it as? DeeplinkBookmarkLectureDetailUiState.Success)?.copy(isBookmarked = false) ?: it } }
                    .onFailure { handleError(it) }
            } else {
                bookmarkRepository.addBookmark(courseBook, state.lecture)
                    .onSuccess { _uiState.update { (it as? DeeplinkBookmarkLectureDetailUiState.Success)?.copy(isBookmarked = true) ?: it } }
                    .onFailure { handleError(it) }
            }
        }
    }

    fun toggleVacancy() {
        viewModelScope.launch {
            val state = _uiState.value as? DeeplinkBookmarkLectureDetailUiState.Success ?: return@launch
            if (state.vacancyRegistered) {
                vacancyRepository.removeVacancyLecture(state.lecture)
                    .onSuccess { _uiState.update { (it as? DeeplinkBookmarkLectureDetailUiState.Success)?.copy(vacancyRegistered = false) ?: it } }
                    .onFailure { handleError(it) }
            } else {
                vacancyRepository.addVacancyLecture(state.lecture)
                    .onSuccess { _uiState.update { (it as? DeeplinkBookmarkLectureDetailUiState.Success)?.copy(vacancyRegistered = true) ?: it } }
                    .onFailure { handleError(it) }
            }
        }
    }

    fun openSyllabus() {
        viewModelScope.launch {
            val lecture = (_uiState.value as? DeeplinkBookmarkLectureDetailUiState.Success)?.lecture as? LectureSyllabusInfo ?: return@launch
            lectureSearchRepository.getSyllabusUrl(
                CourseBook(year = year, semester = semester),
                lecture.courseNumber,
                lecture.lectureNumber,
            ).onSuccess { url ->
                _uiEvent.emit(DeeplinkBookmarkLectureDetailUiEvent.OpenUrl(url))
            }.onFailure { handleError(it) }
        }
    }

    fun openReview() {
        viewModelScope.launch {
            _uiEvent.emit(DeeplinkBookmarkLectureDetailUiEvent.OpenReviewSheet)
        }
    }

    fun closeReview() {
        viewModelScope.launch {
            _uiEvent.emit(DeeplinkBookmarkLectureDetailUiEvent.CloseReviewSheet)
        }
    }

    private fun handleError(error: com.wafflestudio.snutt2.lib.network.DomainError) {
        viewModelScope.launch {
            _uiEvent.emit(
                DeeplinkBookmarkLectureDetailUiEvent.ShowToast(
                    displayMessageResolver.getDisplayMessage(error),
                ),
            )
        }
    }
}

sealed interface DeeplinkBookmarkLectureDetailUiState {
    data object Loading : DeeplinkBookmarkLectureDetailUiState

    data class Success(
        val lecture: SearchedLecture,
        val buildings: List<LectureBuildingDto> = emptyList(),
        val isBookmarked: Boolean = false,
        val vacancyRegistered: Boolean = false,
        val reviewInfo: LectureReviewInfo? = null,
        val showCategoryPre2025: Boolean = true,
        val disableMapFeature: Boolean = false,
    ) : DeeplinkBookmarkLectureDetailUiState
}

sealed interface DeeplinkBookmarkLectureDetailUiEvent {
    data class ShowToastAndNavigateBack(val message: String) : DeeplinkBookmarkLectureDetailUiEvent
    data class ShowToast(val message: String) : DeeplinkBookmarkLectureDetailUiEvent
    data class OpenUrl(val url: String) : DeeplinkBookmarkLectureDetailUiEvent
    data object OpenReviewSheet : DeeplinkBookmarkLectureDetailUiEvent
    data object CloseReviewSheet : DeeplinkBookmarkLectureDetailUiEvent
}
