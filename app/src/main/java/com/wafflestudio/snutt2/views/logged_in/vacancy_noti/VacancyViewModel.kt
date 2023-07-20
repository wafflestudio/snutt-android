package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.toDataWithState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class VacancyViewModel @Inject constructor(
    private val vacancyRepository: VacancyRepository
) : ViewModel() {
    private val _vacancyLectures = MutableStateFlow<List<DataWithState<LectureDto, Boolean>>>(listOf())
    val vacancyLectures: StateFlow<List<DataWithState<LectureDto, Boolean>>> = _vacancyLectures

    private val _isRefreshing  = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        getVacancyLectures()
    }

    fun getVacancyLectures() {
        viewModelScope.launch {
            _vacancyLectures.emit(
                vacancyRepository.getVacancyLectures()
                    .map { lecture ->
                        lecture.toDataWithState(lecture.registrationCount < lecture.quota)
                    }
                    .sortedByDescending { it.state }
            )
            _isRefreshing.emit(false)
        }
    }
}
