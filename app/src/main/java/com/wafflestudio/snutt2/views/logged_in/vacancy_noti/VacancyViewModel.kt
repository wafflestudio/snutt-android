package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.toDataWithState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VacancyViewModel @Inject constructor(
    private val vacancyRepository: VacancyRepository
) : ViewModel() {
    private val _selectedLecture = MutableStateFlow<LectureDto?>(null)
    val selectedLecture = _selectedLecture.asStateFlow()
    private val _querySignal = MutableSharedFlow<Unit>(replay = 0)

    val queryResults = combine(
        _querySignal.flatMapLatest {
            vacancyRepository.getVacancyLectureStream()
//                .cachedIn(viewModelScope)
        },
        _selectedLecture
    ) { pagingData, selectedLecture ->
        pagingData.map { lecture ->
            lecture.toDataWithState(lecture == selectedLecture)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
//        PagingData.empty()
        listOf()
    )

    init {
        viewModelScope.launch {
            _querySignal.emit(Unit)
        }
    }

    suspend fun toggleLectureSelection(lecture: LectureDto) {
        if (lecture == _selectedLecture.value) _selectedLecture.emit(null)
        else _selectedLecture.emit(lecture)
    }
}
