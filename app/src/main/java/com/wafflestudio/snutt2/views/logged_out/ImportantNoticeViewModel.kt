package com.wafflestudio.snutt2.views.logged_out

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.important_notice.ImportantNoticeRepository
import com.wafflestudio.snutt2.model.ImportantNotice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ImportantNoticeViewModel @Inject constructor(
    private val importantNoticeRepository: ImportantNoticeRepository
) : ViewModel() {
    private val _importantNotice = MutableStateFlow(ImportantNotice(null, null))
    val importantNotice = _importantNotice.asStateFlow()

    suspend fun getConfigs() {
        _importantNotice.emit(importantNoticeRepository.getConfigs())
    }
}