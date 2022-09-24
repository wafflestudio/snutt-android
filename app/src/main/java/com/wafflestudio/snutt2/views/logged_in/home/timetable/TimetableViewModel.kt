package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.lib.isLectureNumberEquals
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val tableRepository: TableRepository
) : ViewModel() {
    val currentTable = currentTableRepository.currentTable.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Defaults.defaultTableDto
    )

    val previewTheme = currentTableRepository.previewTheme.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), initialValue = TimetableColorTheme.SNUTT
    )

    init {
        // TODO: 임시
        viewModelScope.launch {
//            tableRepository.fetchDefaultTable()
        }
    }

    suspend fun addLecture(lecture: LectureDto, is_force: Boolean) {
        currentTableRepository
            .addLecture(lecture.id, is_force)
    }

    suspend fun removeLecture(lecture: LectureDto) {
        currentTable.first().lectureList.findLast { lec ->
            lec.isLectureNumberEquals(lecture)
        }?.id?.let {
            currentTableRepository.removeLecture(it)
        }
    }

    suspend fun updateTheme() {
        tableRepository.updateTableTheme(currentTable.value.id, previewTheme.value!!) // FIXME
    }

    suspend fun setPreviewTheme(previewTheme: TimetableColorTheme?) {
        currentTableRepository.setPreviewTheme(previewTheme)
    }
}
