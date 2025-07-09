package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.lib.courseBookEquals
import com.wafflestudio.snutt2.lib.network.AuthError
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.DomainError
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.onFailure
import com.wafflestudio.snutt2.lib.network.onSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
    private val tableRepository: TableRepository,
    private val courseBookRepository: CourseBookRepository,
    private val savedStateHandle: SavedStateHandle,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    private val _lectureId = MutableStateFlow<String?>(savedStateHandle["lectureId"])
    val lectureId = _lectureId.asStateFlow()

    private val _lectureName = MutableStateFlow<String?>(savedStateHandle["lectureName"])
    val lectureName = _lectureName.asStateFlow()

    private val _writtenLectureIds = MutableStateFlow<List<Int>>(listOf())
    val writtenLecturesIds = _writtenLectureIds.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val todayLectureList: Flow<List<LectureDto>?> = flow {
        val courseBooks = courseBookRepository.getCourseBook()
        if (courseBooks.isNotEmpty()) {
            emit(courseBooks.first())
        }
    }.flatMapLatest { currentCourseBook ->
        combine(
            flowOf(currentCourseBook),
            tableRepository.tableMap,
        ) { courseBook, tableMap ->
            tableMap.values
                .firstOrNull { it.courseBookEquals(courseBook) && it.isPrimary }
        }
    }.filterNotNull()
        .map { it.id }
        .flatMapLatest { id ->
            flow {
                emit(runCatching { tableRepository.searchTableById(id) }.getOrNull())
            }
        }
        .map { tableDto ->
            tableDto?.lectureList.orEmpty().filter { lecture ->
                lecture.class_time_json.any { classTime ->
                    classTime.day == LocalDate.now().dayOfWeek.value - 1 // NOTE: DayOfWeek 는 1이 월요일이고, 우리 서버는 0이 월요일이다
                }
            }
        }

    private val _diaryWriteInit = MutableStateFlow<DiaryWriteUiState>(DiaryWriteUiState.Loading)
    val diaryWriteInit = _diaryWriteInit.asStateFlow()

    private val _diaryWriteUiEvent: MutableSharedFlow<DiaryWriteUiEvent> = MutableSharedFlow(1)
    val diaryWriteUiEvent: SharedFlow<DiaryWriteUiEvent> = _diaryWriteUiEvent

    init {
        viewModelScope.launch {
            _lectureId.collect { savedStateHandle["lectureId"] = it }
        }
        viewModelScope.launch {
            _lectureName.collect { savedStateHandle["lectureName"] = it }
        }
        viewModelScope.launch {
            _diaryWriteInit.value = DiaryWriteUiState.Loading
            diaryRepository.getDiaryWriteInit()
                .onSuccess { data ->
                    _diaryWriteInit.emit(DiaryWriteUiState.Success(data))
                }.onFailure { error ->
                    _diaryWriteInit.emit(DiaryWriteUiState.Error)
                    handleDiaryWriteError(error)
                }
        }
    }

    fun setLectureData(id: String, name: String) {
        _lectureId.value = id
        _lectureName.value = name
    }

    fun saveDiaryWrite(diaryWriteData: DiaryWrite) {
        viewModelScope.launch {
            diaryRepository.saveDiaryWrite(diaryWriteData)
                .onSuccess {
                    _diaryWriteUiEvent.emit(DiaryWriteUiEvent.NavigateToWriteMore)
                }
                .onFailure {
                    _diaryWriteInit.emit(DiaryWriteUiState.Error)
                }
        }
    }

    private suspend fun handleDiaryWriteError(error: DomainError) {
        val displayMessage = displayMessageResolver.getDisplayMessage(error)
        when (error) {
            is AuthError -> {
                _diaryWriteUiEvent.emit(DiaryWriteUiEvent.ShowToast(displayMessage))
                userRepository.performLogout()
                _diaryWriteUiEvent.emit(DiaryWriteUiEvent.NavigateToOnboard)
            }
            else -> {
                _diaryWriteUiEvent.emit(DiaryWriteUiEvent.ShowToast(displayMessage))
            }
        }
    }
}

sealed interface DiaryWriteUiEvent {
    data class ShowToast(val message: String) : DiaryWriteUiEvent
    data object NavigateToWriteMore : DiaryWriteUiEvent
    data object NavigateToOnboard : DiaryWriteUiEvent
}
