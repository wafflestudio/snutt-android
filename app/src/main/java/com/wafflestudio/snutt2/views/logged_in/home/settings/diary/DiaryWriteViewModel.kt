package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.domainmodel.DiaryWrite
import com.wafflestudio.snutt2.domainmodel.DiaryWriteInit
import com.wafflestudio.snutt2.domainmodel.preview.DiaryPreviewData
import com.wafflestudio.snutt2.lib.network.ApiOnError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryWriteViewModel @Inject constructor(
    private val apiOnError: ApiOnError,
) : ViewModel() {

    private val _diaryWriteInit = MutableStateFlow<DiaryWriteUiState>(DiaryWriteUiState.Loading)
    val diaryWriteInit = _diaryWriteInit.asStateFlow()

    init {
        viewModelScope.launch {
            _diaryWriteInit.value = DiaryWriteUiState.Loading
            _diaryWriteInit.value = DiaryWriteUiState.Success(DiaryPreviewData.diaryWriteInit)
        }
    }
}
