package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.lib.isLectureNumberEquals
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val tableRepository: TableRepository,
) : ViewModel() {
    val currentTable: StateFlow<TableDto?> = currentTableRepository.currentTable

    private val _previewTheme = MutableStateFlow<ThemeDto?>(null)
    val previewTheme: StateFlow<ThemeDto?> get() = _previewTheme

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
        currentTable.value?.id?.let { id ->
            _previewTheme.value?.let { theme ->
                tableRepository.updateTableTheme(id, theme)
            }
        }
    }

    fun setPreviewTheme(previewTheme: ThemeDto?) {
        _previewTheme.value = previewTheme
    }
}
