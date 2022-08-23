package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.data.DataProvider
import com.wafflestudio.snutt2.lib.data.DataValue
import com.wafflestudio.snutt2.lib.data.SubjectDataValue
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.model.LectureItem
import com.wafflestudio.snutt2.views.logged_in.home.timetable.Defaults
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor(
    private val myLectureRepository: MyLectureRepository,
) : ViewModel() {
    private val _selectedLecture = SubjectDataValue<Optional<LectureDto>>()
    val selectedLecture: DataValue<Optional<LectureDto>> = _selectedLecture

    private val _isEditMode = SubjectDataValue(false)
    val isEditMode: DataProvider<Boolean> = _isEditMode

    private val _selectedColor = SubjectDataValue<Optional<Pair<Int, ColorDto?>>>(Optional.empty())
    val selectedColor: DataValue<Optional<Pair<Int, ColorDto?>>> = _selectedColor

    // TODO: 나중에 위에 3개 지우기
    val colorTheme: TimetableColorTheme? = myLectureRepository.lastViewedTable.get().value?.theme
    private var addMode = false
    private val _editMode = MutableStateFlow(false)
    val editMode = _editMode.asStateFlow()
    private var originalLectureDto = Defaults.defaultLectureDto
    private val _selectedLectureFlow = MutableStateFlow(originalLectureDto)
    val selectedLectureFlow = _selectedLectureFlow.asStateFlow()

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

    fun initializeSelectedLectureFlow(lectureDto: LectureDto?) {
        originalLectureDto = lectureDto ?: Defaults.defaultLectureDto // null 문제 (reset에서 비롯됨)
        viewModelScope.launch { _selectedLectureFlow.emit(originalLectureDto) }
    }

    fun abandonEditingSelectedLectureFlow() {
        viewModelScope.launch { _selectedLectureFlow.emit(originalLectureDto) }
    }

    fun editSelectedLectureFlow(newLectureDto: LectureDto) {
        viewModelScope.launch { _selectedLectureFlow.emit(newLectureDto) }
    }

    fun updateLecture2(): Single<PutLectureResults> {
        val param = buildPutLectureParams()
        return myLectureRepository.updateLecture(_selectedLectureFlow.value.id, param)
    }

    fun removeLecture2(): Completable {
        return myLectureRepository.removeLecture(_selectedLectureFlow.value.id)
            .ignoreElement()
    }

    fun resetLecture2(): Single<ResetLectureResults> {
        return myLectureRepository.resetLecture(_selectedLectureFlow.value.id)
    }

    fun createLecture2(): Single<PostCustomLectureResults> {
        val param = buildPostLectureParams()
        return myLectureRepository.createLecture(param)
    }

    // TODO: 여기부터 아래로 쭉 지우기
    val lists: ArrayList<LectureItem> = arrayListOf()

    fun setEditMode(edit: Boolean) {
        _isEditMode.update(edit)
    }

    fun setLecture(lectureDto: LectureDto?) {
        _selectedLecture.update(lectureDto.toOptional())
    }

    fun setSelectedColor(colorIndex: Int, colorDto: ColorDto?) {
        _selectedColor.update(Pair(colorIndex, colorDto).toOptional())
    }

    fun updateLecture(param: PutLectureParams): Single<PutLectureResults> {
        val lectureId = _selectedLecture.get().value?.id
            ?: return Single.error(IllegalStateException("no selected lecture"))
        return myLectureRepository.updateLecture(lectureId, param)
    }

    fun removeLecture(): Completable {
        val lectureId = _selectedLecture.get().value?.id
            ?: return Completable.error(IllegalStateException("no selected lecture"))
        return myLectureRepository.removeLecture(lectureId)
            .ignoreElement()
    }

    fun resetLecture(): Single<ResetLectureResults> {
        val lectureId = _selectedLecture.get().value?.id
            ?: return Single.error(IllegalStateException("no selected lecture"))
        return myLectureRepository.resetLecture(lectureId)
    }
    // TODO: 여기까지 지우기

    fun getCourseBookUrl(): Single<GetCoursebooksOfficialResults> {
        val courseNumber = _selectedLectureFlow.value.course_number ?: return Single.error(
            IllegalStateException("lecture with no course number")
        )
        val lectureNumber = _selectedLectureFlow.value.lecture_number ?: return Single.error(
            IllegalStateException("lecture with no lecture number")
        )
        return myLectureRepository.getLectureCourseBookUrl(courseNumber, lectureNumber)
    }

    fun getReviewContentsUrl(): Single<String> {
        val courseNumber = _selectedLectureFlow.value.course_number ?: return Single.error(
            IllegalStateException("lecture with no course number")
        )
        // TODO: (check) 교수가 수정된 경우는 어떻게 되는가? 서버에서 알아서 찾는가?
        val instructor = _selectedLectureFlow.value.instructor ?: return Single.error(
            IllegalStateException("lecture with no instructor")
        )
        return myLectureRepository.getLectureReviewUrl(courseNumber, instructor)
    }

    private fun buildPutLectureParams(): PutLectureParams {
        return PutLectureParams(
            id = _selectedLectureFlow.value.id,
            course_title = _selectedLectureFlow.value.course_title,
            instructor = _selectedLectureFlow.value.instructor,
            colorIndex = _selectedLectureFlow.value.colorIndex,
            color = _selectedLectureFlow.value.color,
            department = _selectedLectureFlow.value.department,
            academic_year = _selectedLectureFlow.value.academic_year,
            credit = _selectedLectureFlow.value.credit,
            classification = _selectedLectureFlow.value.classification,
            category = _selectedLectureFlow.value.category,
            remark = _selectedLectureFlow.value.remark,
            class_time_json = _selectedLectureFlow.value.class_time_json
        )
    }

    private fun buildPostLectureParams(): PostCustomLectureParams {
        return PostCustomLectureParams(
            course_title = _selectedLectureFlow.value.course_title,
            instructor = _selectedLectureFlow.value.instructor,
            colorIndex = _selectedLectureFlow.value.colorIndex,
            color = _selectedLectureFlow.value.color,
            credit = _selectedLectureFlow.value.credit,
            remark = _selectedLectureFlow.value.remark,
            class_time_json = _selectedLectureFlow.value.class_time_json
        )
    }
}
