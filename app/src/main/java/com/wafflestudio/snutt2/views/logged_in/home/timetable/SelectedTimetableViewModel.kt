package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.data.TableRepository
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.data.DataProvider
import com.wafflestudio.snutt2.lib.data.SubjectDataValue
import com.wafflestudio.snutt2.lib.isRegularlyEquals
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.model.TableTrimParam
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@HiltViewModel
class SelectedTimetableViewModel @Inject constructor(
    private val myLectureRepository: MyLectureRepository,
    private val storage: SNUTTStorage,
    private val tableRepository: TableRepository
) : ViewModel() {
    private val _selectedPreviewTheme =
        SubjectDataValue(myLectureRepository.lastViewedTable.get().value?.theme.toOptional())
    val selectedPreviewTheme: DataProvider<Optional<TimetableColorTheme>> = _selectedPreviewTheme

    val lastViewedTable: DataProvider<Optional<TableDto>> = myLectureRepository.lastViewedTable

    val currentTimetable: Observable<TableDto>
        get() = myLectureRepository.currentTable

    val trimParam: Observable<TableTrimParam>
        get() = storage.tableTrimParam.asObservable()


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

    fun setSelectedPreviewTheme(theme: TimetableColorTheme?) {
        _selectedPreviewTheme.update(theme.toOptional())
    }
}
