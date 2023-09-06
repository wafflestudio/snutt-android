package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.network.NetworkLog

fun SNUTTStorage.addNetworkLog(newLog: NetworkLog) {
    networkLog.update(
        networkLog.get().toMutableList().apply {
            add(0, newLog)
        }.let {
            if (it.size > 100) {
                it.subList(0, 100)
            } else {
                it
            }
        },
    )
}
