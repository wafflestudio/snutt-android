package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModelNew @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
) : ViewModel() {
    val currentTable = currentTableRepository.currentTable.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Defaults.defaultTableDto
    )

    private var addMode = false
    private val _editMode = MutableStateFlow(false)
    val editMode = _editMode.asStateFlow()

    private var fixedLectureDetail = Defaults.defaultLectureDto
    private val _editingLectureDetail = MutableStateFlow(fixedLectureDetail)
    val editingLectureDetail = _editingLectureDetail.asStateFlow()

    fun isAddMode(): Boolean {
        return addMode
    }

    fun setAddMode(value: Boolean) {
        addMode = value
    }

    fun setEditMode() {
        viewModelScope.launch { _editMode.emit(true) }
    }

    fun unsetEditMode() {
        viewModelScope.launch { _editMode.emit(false) }
    }

    fun initializeEditingLectureDetail(lecture: LectureDto?) {
        fixedLectureDetail = lecture ?: Defaults.defaultLectureDto // null 문제 (reset에서 비롯됨)
        viewModelScope.launch { _editingLectureDetail.emit(fixedLectureDetail) }
    }

    fun abandonEditingLectureDetail() {
        viewModelScope.launch { _editingLectureDetail.emit(fixedLectureDetail) }
    }

    fun editEditingLectureDetail(newLectureDto: LectureDto) {
        viewModelScope.launch { _editingLectureDetail.emit(newLectureDto) }
    }

    suspend fun updateLecture2() {
        val param = buildPutLectureParams()
        currentTableRepository.updateLecture(_editingLectureDetail.value.id, param)
    }

    suspend fun removeLecture2() {
        currentTableRepository.removeLecture(_editingLectureDetail.value.id)
    }

    suspend fun resetLecture2(): LectureDto {
        currentTableRepository.resetLecture(_editingLectureDetail.value.id)
        return currentTable.value.lectureList.find { it.id == editingLectureDetail.value.id }!! // TODO: 왜 resetLecture 의 api 응답이 TableDto 인지..
    }

    suspend fun createLecture2() {
        val param = buildPostLectureParams()
        currentTableRepository.createCustomLecture(param)
    }

    suspend fun getCourseBookUrl(): String {
        val courseNumber = _editingLectureDetail.value.course_number
            ?: (throw IllegalStateException("lecture with no course number")) // FIXME
        val lectureNumber = _editingLectureDetail.value.lecture_number
            ?: (throw IllegalStateException("lecture with no course number")) // FIXME
        return currentTableRepository.getLectureSyllabusUrl(courseNumber, lectureNumber)
    }

    suspend fun getReviewContentsUrl() {
        // TODO
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
            class_time_json = _editingLectureDetail.value.class_time_json
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
            class_time_json = _editingLectureDetail.value.class_time_json
        )
    }
}