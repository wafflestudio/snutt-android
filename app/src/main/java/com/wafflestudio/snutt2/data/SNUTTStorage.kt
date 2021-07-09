package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.preferences.storage.PrefStorage
import com.wafflestudio.snutt2.lib.preferences.storage.PrefValue
import com.wafflestudio.snutt2.model.TableTrimParam
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SNUTTStorage @Inject constructor(prefStorage: PrefStorage) {

    val lastViewedTable: PrefValue<Optional<TableDto>> = PrefValue.defineNullableStorageValue(
        "last_table",
        null,
        prefStorage,
        TableDto::class
    )

    val tableTrimParam: PrefValue<TableTrimParam> = PrefValue.defineNonNullStorageValue(
        "table_trim_param",
        TableTrimParam.Default,
        prefStorage,
        TableTrimParam::class
    )
}
