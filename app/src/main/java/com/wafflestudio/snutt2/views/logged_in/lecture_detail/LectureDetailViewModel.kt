package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.data.DataProvider
import com.wafflestudio.snutt2.lib.data.DataValue
import com.wafflestudio.snutt2.lib.data.SubjectDataValue
import com.wafflestudio.snutt2.lib.network.dto.GetCoursebooksOfficialResults
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureResults
import com.wafflestudio.snutt2.lib.network.dto.ResetLectureResults
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.model.LectureItem
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.*
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

    val colorTheme: TimetableColorTheme? = myLectureRepository.lastViewedTable.get().value?.theme

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

    fun getCourseBookUrl(): Single<GetCoursebooksOfficialResults> {
        val courseNumber = _selectedLecture.get().value?.course_number ?: return Single.error(
            IllegalStateException("lecture with no course number")
        )
        val lectureNumber = _selectedLecture.get().value?.lecture_number ?: return Single.error(
            IllegalStateException("lecture with no lecture number")
        )
        return myLectureRepository.getLectureCourseBookUrl(courseNumber, lectureNumber)
    }

    fun getReviewContentsUrl(): Single<String> {
        val courseNumber = _selectedLecture.get().value?.course_number ?: return Single.error(
            IllegalStateException("lecture with no course number")
        )
        val instructor = _selectedLecture.get().value?.instructor ?: return Single.error(
            IllegalStateException("lecture with no instructor")
        )
        return myLectureRepository.getLectureReviewUrl(courseNumber, instructor)
    }
}
