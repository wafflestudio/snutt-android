package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.lecture_diary.DiaryRepository
import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.lib.network.ApiOnError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val apiOnError: ApiOnError,
) : ViewModel() {

    private val _diaryWriteInit = MutableStateFlow<DiaryWriteUiState>(DiaryWriteUiState.Loading)
    val diaryWriteInit = _diaryWriteInit.asStateFlow()

    private val _navigationFlag = MutableStateFlow(false)
    val navigationFlag: StateFlow<Boolean> = _navigationFlag

    init {
        viewModelScope.launch {
            _diaryWriteInit.value = DiaryWriteUiState.Loading
            diaryRepository.getDiaryWriteInit().catch {
                _diaryWriteInit.value = DiaryWriteUiState.Error
            }.collect { data ->
                _diaryWriteInit.value = DiaryWriteUiState.Success(data)
            }
        }
    }

    fun saveDiaryWrite(diaryWriteData: DiaryWrite) {
        viewModelScope.launch {
            runCatching {
                diaryRepository.saveDiaryWrite(diaryWriteData)
            }.onFailure { error ->
                apiOnError.invoke(error)
            }.onSuccess {
                _navigationFlag.value = true
            }
        }
    }

    fun clearNavigationFlag() {
        _navigationFlag.value = false
    }
}
