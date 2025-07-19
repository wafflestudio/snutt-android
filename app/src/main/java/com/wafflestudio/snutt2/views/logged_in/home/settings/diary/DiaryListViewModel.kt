package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.course_books.CourseBookRepository
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.domainmodel.DiaryList
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.DisplayMessageResolver
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryListViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository,
    private val courseBookRepository: CourseBookRepository,
    private val displayMessageResolver: DisplayMessageResolver,
) : ViewModel() {

    var courseBookDtoList: List<CourseBookDto>? = null

    var _selectedCourseBookIdx = MutableStateFlow(0)
    val selectedCourseBookIdx: StateFlow<Int> = _selectedCourseBookIdx.asStateFlow()

    fun clickCourseBook(idx: Int) {
        _selectedCourseBookIdx.value = idx
    }

    private val _diaryListUiState = MutableStateFlow<DiaryListUiState>(DiaryListUiState.Loading)
    val diaryListUiState = _diaryListUiState.asStateFlow()

    init {
        viewModelScope.launch {
            courseBookDtoList = courseBookRepository.getCourseBook()
            _diaryListUiState.value = DiaryListUiState.Success(DiaryList(courseBookDtoList!![_selectedCourseBookIdx.value], DiaryPreviewData.diaryList))
        }
    }
}
