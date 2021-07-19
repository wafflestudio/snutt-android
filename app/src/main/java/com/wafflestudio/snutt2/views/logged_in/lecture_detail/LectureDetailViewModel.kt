package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.lib.network.dto.GetCoursebooksOfficialResults
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureResults
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.manager.LectureManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.lang.IllegalStateException
import javax.inject.Inject

@HiltViewModel
class LectureDetailViewModel @Inject constructor(
    private val myLectureRepository: MyLectureRepository,
) : ViewModel() {
    private val _selectedLecture = BehaviorSubject.create<LectureDto>()
    val selectedLecture: Observable<LectureDto> = _selectedLecture.hide()

    private val _isEditMode = BehaviorSubject.createDefault(false)
    val isEditMode: Observable<Boolean> = _isEditMode.hide()

    fun setEditMode(edit: Boolean) {
        _isEditMode.onNext(edit)
    }

    fun setLecture(lectureDto: LectureDto) {
        _selectedLecture.onNext(lectureDto)
    }

    fun updateLecture(param: PutLectureParams): Single<PutLectureResults> {
        return myLectureRepository.updateLecture(_selectedLecture.value.id, param)
    }

    fun removeLecture(): Completable {
        return myLectureRepository.removeLecture(_selectedLecture.value.id)
            .ignoreElement()
    }

    fun resetLecture(): Completable {
        return myLectureRepository.resetLecture(_selectedLecture.value.id)
            .ignoreElement()
    }

    fun getCourseBookUrl(): Single<GetCoursebooksOfficialResults> {
        val courseNumber = _selectedLecture.value.course_number ?: return Single.error(
            IllegalStateException("lecture with no course number")
        )
        val lectureNumber = _selectedLecture.value.lecture_number ?: return Single.error(
            IllegalStateException("lecture with no lecture number")
        )
        return myLectureRepository.getLectureCourseBookUrl(courseNumber, lectureNumber)
    }


}
