package com.wafflestudio.snutt2.data

import com.google.common.collect.Table
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.preferences.storage.PrefStorage
import com.wafflestudio.snutt2.lib.preferences.storage.PrefValue
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.model.TagDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SNUTTStorage @Inject constructor(private val prefStorage: PrefStorage) {

    val tableMap: PrefValue<Map<String, TableDto>> = PrefValue.defineMapStorageValue(
        "pref_tables",
        mapOf(),
        prefStorage,
        String::class,
        TableDto::class
    )

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

    val accessToken = PrefValue.defineNonNullStorageValue(
        "pref_key_x_access_token",
        "",
        prefStorage,
        String::class
    )

    val prefKeyUserId = PrefValue.defineNullableStorageValue(
        "pref_key_user_id",
        null,
        prefStorage,
        String::class
    )

    val tables: PrefValue<List<TableDto>> = PrefValue.defineListStorageValue(
        "pref_tables",
        listOf(),
        prefStorage,
        TableDto::class
    )

    val courseBooks: PrefValue<List<CourseBookDto>> = PrefValue.defineListStorageValue(
        "pref_course_books",
        listOf(),
        prefStorage,
        CourseBookDto::class
    )

    val tags: PrefValue<List<TagDto>> = PrefValue.defineListStorageValue(
        "pref_tags",
        listOf(),
        prefStorage,
        TagDto::class
    )

    fun clearAll() {
        prefStorage.clear()
    }
}
