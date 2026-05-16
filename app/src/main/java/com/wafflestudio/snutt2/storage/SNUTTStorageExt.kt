package com.wafflestudio.snutt2.storage

import com.wafflestudio.snutt2.storage.model.NetworkLog

fun SNUTTStorage.addNetworkLog(newLog: NetworkLog) {
    networkLog.update { prev ->
        (listOf(newLog) + prev).take(100)
    }
}
