package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.NetworkLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val snuttStorage: SNUTTStorage
) : ViewModel() {

    val networkLog: StateFlow<List<NetworkLog>> = snuttStorage.networkLog.asStateFlow()

    fun clearNetworkLog() {
        snuttStorage.networkLog.clear()
    }
}
