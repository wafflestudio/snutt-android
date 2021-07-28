package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.data.TableRepository
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.isRegularlyEquals
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.model.TableTrimParam
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val myLectureRepository: MyLectureRepository,
    private val storage: SNUTTStorage,
    private val tableRepository: TableRepository
) : ViewModel() {

    val currentTimetable: Observable<TableDto>
        get() = myLectureRepository.currentTable

    val trimParam: Observable<TableTrimParam>
        get() = storage.tableTrimParam.asObservable()

    private val selectedPreviewTheme =
        BehaviorSubject.createDefault<Optional<TimetableColorTheme>>(Optional.empty())
    val previewTheme = selectedPreviewTheme.hide()

    fun toggleLecture(lecture: LectureDto): Completable {
        return myLectureRepository.currentTable
            .firstOrError()
            .flatMap {
                val target = it.lectureList.findLast { lec -> lec.isRegularlyEquals(lecture) }
                if (target != null) myLectureRepository.removeLecture(lectureId = target.id)
                else myLectureRepository.addLecture(lectureId = lecture.id)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .ignoreElement()
    }

    fun updateTheme(id: String, theme: TimetableColorTheme): Completable {
        return tableRepository.updateTableTheme(id, theme)
            .observeOn(AndroidSchedulers.mainThread())
            .ignoreElement()
    }

    fun setPreviewTheme(theme: TimetableColorTheme?) {
        selectedPreviewTheme.onNext(theme.toOptional())
    }
}
