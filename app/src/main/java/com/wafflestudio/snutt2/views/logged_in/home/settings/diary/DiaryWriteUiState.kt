package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import com.wafflestudio.snutt2.domainmodel.DiaryWrite

sealed interface DiaryWriteUiState {
    data class Success(val diaryList: DiaryWrite) : DiaryWriteUiState
    data object Error : DiaryWriteUiState
    data object Loading : DiaryWriteUiState
    data object Empty : DiaryWriteUiState
}
