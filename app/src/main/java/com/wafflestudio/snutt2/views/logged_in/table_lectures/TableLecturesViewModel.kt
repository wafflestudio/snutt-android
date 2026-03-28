package com.wafflestudio.snutt2.views.logged_in.table_lectures

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TableLecturesViewModel @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TableLecturesUiState(
            lectures = currentTableRepository.currentTable.value?.lectures ?: emptyList(),
        ),
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TableLecturesUiEvent>(0)
    val uiEvent = _uiEvent.asSharedFlow()

    fun onNavigateLectureDetail(lecture: LocalLecture) {
        viewModelScope.launch {
            val tableId = currentTableRepository.currentTable.value?.summary?.id
            _uiEvent.emit(TableLecturesUiEvent.NavigateToLectureDetail(lecture.id, tableId))
        }
    }
}

sealed interface TableLecturesUiEvent {
    data class NavigateToLectureDetail(val lectureId: String, val tableId: String?) : TableLecturesUiEvent
}

data class TableLecturesUiState(
    val lectures: List<LocalLecture>,
)
