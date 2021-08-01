package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.*
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
    private val tableRepository: TableRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _selectedPreviewTheme =
        SubjectDataValue(myLectureRepository.lastViewedTable.get().value?.theme.toOptional())
    val selectedPreviewTheme: DataProvider<Optional<TimetableColorTheme>> = _selectedPreviewTheme

    val lastViewedTable: DataProvider<Optional<TableDto>> = myLectureRepository.lastViewedTable

    val trimParam: DataProvider<TableTrimParam>
        get() = settingsRepository.tableTrimParam


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
