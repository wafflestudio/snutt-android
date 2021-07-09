package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.toOptional
import javax.inject.Singleton

// FIXME: For hilt testing
@Singleton
class TableRepository(
    private val snuttRestApi: SNUTTRestApi,
    private val storage: SNUTTStorage,
    private val apiOnError: ApiOnError
) {
    fun refreshTable(tableId: String) {
        snuttRestApi.getTableById("token", tableId)
            .doOnSuccess {
                storage.lastViewedTable.setValue(it.toOptional())
            }
    }
}
