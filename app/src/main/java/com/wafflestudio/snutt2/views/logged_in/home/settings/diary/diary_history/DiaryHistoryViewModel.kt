package com.wafflestudio.snutt2.views.logged_in.home.settings.diary.diary_history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.dto.core.toCourseBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryHistoryViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
    private val courseBookRepository: CourseBookRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    fun clickCourseBook(idx: Int) {
        if (_diaryHistoryUiState.value is DiaryHistoryUiState.Success) {
            _diaryHistoryUiState.value =
                (_diaryHistoryUiState.value as DiaryHistoryUiState.Success).copy(
                    selectedCourseBookId = idx
                )
        }
    }

    private val _diaryHistoryUiState =
        MutableStateFlow<DiaryHistoryUiState>(DiaryHistoryUiState.Loading)
    val diaryListUiState = _diaryHistoryUiState.asStateFlow()

    init {
        viewModelScope.launch {
            val courseBookList = courseBookRepository.getCourseBook()
                .map { courseBookDto -> courseBookDto.toCourseBook() }
            _diaryHistoryUiState.value =
                DiaryHistoryUiState.Success(courseBookList, 0, DiaryPreviewData.diaryList)
        }
    }
}
