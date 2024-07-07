package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.core.database.model.NetworkLog as NetworkLogDatabase
import com.wafflestudio.snutt2.core.database.preference.SNUTTStorageTemp
import com.wafflestudio.snutt2.core.database.util.map
import com.wafflestudio.snutt2.core.qualifiers.CoreDatabase
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.NetworkLog
import com.wafflestudio.snutt2.lib.network.toExternalModel
import com.wafflestudio.snutt2.model.toExternalModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    @CoreDatabase private val snuttStorage: SNUTTStorageTemp,
    externalScope: CoroutineScope, // TODO : 임시로 필요
) : ViewModel() {

    val networkLog: StateFlow<List<NetworkLog>> = snuttStorage.networkLog.asStateFlow()
        .map(externalScope) {it: List<NetworkLogDatabase> -> it.map { log ->
            log.toExternalModel()}
        }

    fun clearNetworkLog() {
        snuttStorage.networkLog.clear()
    }
}
