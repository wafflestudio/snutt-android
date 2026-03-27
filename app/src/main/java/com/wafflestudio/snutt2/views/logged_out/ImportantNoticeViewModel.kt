package com.wafflestudio.snutt2.views.logged_out

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.RemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportantNoticeViewModel @Inject constructor(
    private val remoteConfig: RemoteConfig,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportantNoticeUiState())
    val uiState: StateFlow<ImportantNoticeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            remoteConfig.noticeConfig.collect { config ->
                _uiState.update {
                    it.copy(
                        title = config.title ?: "",
                        content = config.content ?: "",
                    )
                }
            }
        }
    }
}

data class ImportantNoticeUiState(
    val title: String = "",
    val content: String = "",
)
