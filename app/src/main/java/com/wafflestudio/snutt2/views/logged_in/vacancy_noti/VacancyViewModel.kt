package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.vacancy_noti.VacancyRepository
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class VacancyViewModel @Inject constructor(
    private val vacancyRepository: VacancyRepository
) : ViewModel() {
    private val _vacancyLectures =
        MutableStateFlow<List<LectureDto>>(listOf())
    val vacancyLectures: StateFlow<List<LectureDto>> = _vacancyLectures

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    var isEditMode by mutableStateOf(false)

    val selectedLectures = mutableStateListOf<String>()

    val firstVacancyVisit = vacancyRepository.firstVacancyVisit

    val shouldShowVacancyBanner = vacancyRepository.vacancyBannerCloseDate.map { date ->
        if (vacancyRepository.isVacancyBannerEnabled()) {
            if (date.isEmpty()) {
                true
            } else {
                val now = Calendar.getInstance()
                now.time = Date()
                now.set(Calendar.HOUR_OF_DAY, 0)
                now.set(Calendar.MINUTE, 0)
                now.set(Calendar.SECOND, 0)
                now.set(Calendar.MILLISECOND, 0)

                val last = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)

                now.time.after(last)
            }
        } else {
            false
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    @Inject
    lateinit var apiOnError: ApiOnError

    init {
        viewModelScope.launch {
            try {
                getVacancyLectures()
            } catch (e: Exception) {
                apiOnError(e)
            }
        }
    }

    fun refreshVacancyLectures() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            getVacancyLectures()
            _isRefreshing.emit(false)
        }
    }

    suspend fun getVacancyLectures() {
        _vacancyLectures.emit(
            vacancyRepository.getVacancyLectures()
                .sortedByDescending { it.wasFull }
        )
    }

    suspend fun addVacancyLecture(lectureId: String) {
        vacancyRepository.addVacancyLecture(lectureId)
        getVacancyLectures()
    }

    suspend fun removeVacancyLecture(lectureId: String) {
        vacancyRepository.removeVacancyLecture(lectureId)
        getVacancyLectures()
    }

    fun toggleEditMode() {
        isEditMode = !isEditMode
        selectedLectures.clear()
    }

    fun toggleLectureSelected(lectureId: String) {
        if (!selectedLectures.contains(lectureId))
            selectedLectures.add(lectureId)
        else
            selectedLectures.remove(lectureId)
    }

    suspend fun deleteSelectedLectures() {
        selectedLectures.forEach { lectureId ->
            vacancyRepository.removeVacancyLecture(lectureId)
        }
        getVacancyLectures()
    }

    suspend fun setVacancyVisited() {
        if (firstVacancyVisit.value) {
            vacancyRepository.setVacancyVisited()
        }
    }

    suspend fun closeVacancyBanner() {
        vacancyRepository.updateVacancyBannerCloseDate()
    }
}
