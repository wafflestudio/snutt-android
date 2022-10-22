package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.isLectureNumberEquals
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val tableRepository: TableRepository
) : ViewModel() {
    val currentTable: StateFlow<TableDto?> = currentTableRepository.currentTable

    val previewTheme = currentTableRepository.previewTheme.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), initialValue = TimetableColorTheme.SNUTT
    )

    suspend fun addLecture(lecture: LectureDto, is_force: Boolean) {
        currentTableRepository
            .addLecture(lecture.id, is_force)
    }

    suspend fun removeLecture(lecture: LectureDto) {
        currentTable.value?.lectureList?.findLast { lec ->
            lec.isLectureNumberEquals(lecture)
        }?.id?.let {
            currentTableRepository.removeLecture(it)
        }
    }

    suspend fun updateTheme() {
        tableRepository.updateTableTheme(
            currentTable.value?.id!!,
            previewTheme.value!!
        ) // FIXME
    }

    suspend fun setPreviewTheme(previewTheme: TimetableColorTheme?) {
        currentTableRepository.setPreviewTheme(previewTheme)
    }
}
