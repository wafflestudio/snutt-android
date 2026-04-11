package com.wafflestudio.snutt2.feature.settings

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.lib.android.NetworkLog
import com.wafflestudio.snutt2.storage.SNUTTStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val snuttStorage: SNUTTStorage,
) : ViewModel() {

    val networkLog: StateFlow<List<NetworkLog>> = snuttStorage.networkLog.asStateFlow()

    fun clearNetworkLog() {
        snuttStorage.networkLog.clear()
    }
}
