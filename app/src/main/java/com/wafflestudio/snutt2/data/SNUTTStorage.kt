package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.lib.preferences.context.*
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.model.TagDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SNUTTStorage @Inject constructor(
    private val prefContext: PrefContext
) {

    val tableMap = PrefValue<Map<String, SimpleTableDto>>(
        prefContext,
        PrefMapValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_tables",
            String::class.java,
            SimpleTableDto::class.java,
            mapOf()
        )
    )

    val lastViewedTable = PrefValue<Optional<TableDto>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "last_tables",
            type = TableDto::class.java,
            defaultValue = Optional.empty()
        )
    )

    val tableTrimParam = PrefValue<TableTrimParam>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "table_trim_param",
            type = TableTrimParam::class.java,
            defaultValue = TableTrimParam.Default.copy(forceFitLectures = true)
        )
    )

    val accessToken = PrefValue<String>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_key_x_access_token",
            type = String::class.java,
            defaultValue = ""
        )
    )

    val user = PrefValue<Optional<UserDto>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_user",
            type = UserDto::class.java,
            defaultValue = Optional.empty()
        )
    )

    val prefKeyUserId = PrefValue<Optional<String>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_key_user_id",
            type = String::class.java,
            defaultValue = Optional.empty()
        )
    )

    val courseBooks = PrefValue<List<CourseBookDto>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_course_books",
            type = CourseBookDto::class.java,
            defaultValue = listOf()
        )
    )

    val tags = PrefValue<List<TagDto>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_tags",
            type = TagDto::class.java,
            defaultValue = listOf()
        )
    )

    fun clearAll() {
        prefContext.clear(DOMAIN_SCOPE_LOGIN)
    }

    companion object {
        // 레거시 대응을 위해 어쩔 수 없음..
        const val DOMAIN_SCOPE_LOGIN = "com.wafflestudio.snutt2.live.preferences"

        const val DOMAIN_SCOPE_CURRENT_VERSION = "domain_scope_current_version"

        const val DOMAIN_SCOPE_PERMANENT = "domain_scope_permanent"
    }
}
