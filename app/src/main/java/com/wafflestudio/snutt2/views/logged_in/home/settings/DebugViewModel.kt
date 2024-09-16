package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.core.database.preference.SNUTTStorageTemp
import com.wafflestudio.snutt2.core.database.util.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.wafflestudio.snutt2.core.database.model.NetworkLog as CoreDatabaseNetworkLog
import com.wafflestudio.snutt2.lib.network.NetworkLog as TempDomainNetworkLog

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val snuttStorage: SNUTTStorageTemp,
    externalScope: CoroutineScope, // TODO : 임시로 필요
) : ViewModel() {

    val networkLog: StateFlow<List<TempDomainNetworkLog>> = snuttStorage.networkLog.asStateFlow()
        .map(
            externalScope,
            mapper = { list ->
                list.map { networkLog ->
                    networkLog.toTempNetworkModel()
                }
            },
        )

    fun clearNetworkLog() {
        snuttStorage.networkLog.clear()
    }
}

fun CoreDatabaseNetworkLog.toTempNetworkModel(): TempDomainNetworkLog {
    return TempDomainNetworkLog(
        requestMethod = requestMethod,
        requestUrl = requestUrl,
        requestHeader = requestHeader,
        requestBody = responseBody,
        responseCode = responseCode,
        responseBody = responseBody,
    )
}
