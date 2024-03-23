package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.lecture_search.LectureSearchRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.TableTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ModeType {
    object Normal : ModeType()
    data class Editing(val adding: Boolean = false) : ModeType()
    object Viewing : ModeType()
}

@HiltViewModel
class LectureDetailViewModel @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val lectureSearchRepository: LectureSearchRepository,
    private val themeRepository: ThemeRepository,
) : ViewModel() {
    val currentTable: StateFlow<TableDto?> = currentTableRepository.currentTable

    val currentTableTheme: StateFlow<TableTheme> = themeRepository.currentTableTheme

    private val _modeType = MutableStateFlow<ModeType>(ModeType.Normal)
    val modeType = _modeType.asStateFlow()

    private var fixedLectureDetail = LectureDto.Default
    private val _editingLectureDetail = MutableStateFlow(fixedLectureDetail)
    val editingLectureDetail = _editingLectureDetail.asStateFlow()

    fun setEditMode(adding: Boolean = false) {
        viewModelScope.launch { _modeType.emit(ModeType.Editing(adding)) }
    }

    fun initializeEditingLectureDetail(lecture: LectureDto?, modeType: ModeType) {
        fixedLectureDetail = lecture ?: LectureDto.Default // null 문제 (reset에서 비롯됨)
        viewModelScope.launch {
            _modeType.emit(modeType)
            _editingLectureDetail.emit(fixedLectureDetail)
        }
    }

    fun abandonEditingLectureDetail() {
        initializeEditingLectureDetail(fixedLectureDetail, ModeType.Normal)
    }

    fun editLectureDetail(editedLecture: LectureDto) {
        viewModelScope.launch { _editingLectureDetail.emit(editedLecture) }
    }

    suspend fun updateLecture(is_forced: Boolean = false) {
        val param = buildPutLectureParams()
        param.isForced = is_forced
        currentTableRepository.updateLecture(_editingLectureDetail.value.id, param)
        initializeEditingLectureDetail(_editingLectureDetail.value, ModeType.Normal)
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

    suspend fun getReviewContentsUrl(): String? {
        return lectureSearchRepository.getLectureReviewUrl(
            courseNumber = editingLectureDetail.value.course_number ?: return null,
            instructor = editingLectureDetail.value.instructor,
        )
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
}
