package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.dto.GetCoursebooksOfficialResults
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureResults
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.toOptional
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor(
    private val myLectureRepository: MyLectureRepository,
) : ViewModel() {
    private val _selectedLecture = BehaviorSubject.create<Optional<LectureDto>>()
    val selectedLecture: Observable<Optional<LectureDto>> = _selectedLecture.hide()

    private val _isEditMode = BehaviorSubject.createDefault(false)
    val isEditMode: Observable<Boolean> = _isEditMode.hide()

    fun setEditMode(edit: Boolean) {
        _isEditMode.onNext(edit)
    }

    fun setLecture(lectureDto: LectureDto?) {
        _selectedLecture.onNext(lectureDto.toOptional())
    }

    fun updateLecture(param: PutLectureParams): Single<PutLectureResults> {
        val lectureId = _selectedLecture.value.get()?.id
            ?: return Single.error(IllegalStateException("no selected lecture"))
        return myLectureRepository.updateLecture(lectureId, param)
    }

    fun removeLecture(): Completable {
        val lectureId = _selectedLecture.value.get()?.id
            ?: return Completable.error(IllegalStateException("no selected lecture"))
        return myLectureRepository.removeLecture(lectureId)
            .ignoreElement()
    }

    fun resetLecture(): Completable {
        val lectureId = _selectedLecture.value.get()?.id
            ?: return Completable.error(IllegalStateException("no selected lecture"))
        return myLectureRepository.resetLecture(lectureId)
            .ignoreElement()
    }

    fun getCourseBookUrl(): Single<GetCoursebooksOfficialResults> {
        val courseNumber = _selectedLecture.value.get()?.course_number ?: return Single.error(
            IllegalStateException("lecture with no course number")
        )
        val lectureNumber = _selectedLecture.value.get()?.lecture_number ?: return Single.error(
            IllegalStateException("lecture with no lecture number")
        )
        return myLectureRepository.getLectureCourseBookUrl(courseNumber, lectureNumber)
    }

    fun getSelectedLecture(): Optional<LectureDto> {
        return _selectedLecture.value
    }
}
