package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.lib.isLectureNumberEquals
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val tableRepository: TableRepository,
    private val themeRepository: ThemeRepository,
) : ViewModel() {
    val currentTable: StateFlow<TableDto?> = currentTableRepository.currentTable

    private val _previewTheme = MutableStateFlow<TableTheme?>(null)
    val previewTheme: StateFlow<TableTheme?> get() = _previewTheme

    val tableTheme: StateFlow<TableTheme> get() = currentTableRepository.currentTable.map { table ->
        table?.themeId?.let {
            themeRepository.getTheme(it)
        } ?: table?.theme?.let {
            BuiltInTheme.fromCode(it)
        } ?: BuiltInTheme.SNUTT
    }.stateIn(viewModelScope, SharingStarted.Eagerly, BuiltInTheme.SNUTT)

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
                if (theme is BuiltInTheme) {
                    tableRepository.updateTableTheme(id, theme.code)
                } else {
                    tableRepository.updateTableTheme(id, (theme as CustomTheme).id)
                }
            }
        }
    }

    fun setPreviewTheme(previewTheme: TableTheme?) {
        _previewTheme.value = previewTheme
    }
}
