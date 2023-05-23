package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.NetworkLog
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.lib.preferences.context.PrefContext
import com.wafflestudio.snutt2.lib.preferences.context.PrefListValueMetaData
import com.wafflestudio.snutt2.lib.preferences.context.PrefMapValueMetaData
import com.wafflestudio.snutt2.lib.preferences.context.PrefOptionalValueMetaData
import com.wafflestudio.snutt2.lib.preferences.context.PrefValue
import com.wafflestudio.snutt2.lib.preferences.context.PrefValueMetaData
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.ui.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SNUTTStorage @Inject constructor(
    private val prefContext: PrefContext
) {

    val prefKeyUserId = PrefValue<Optional<String>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_key_user_id",
            type = String::class.java,
            defaultValue = Optional.empty()
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

    val tableMap = PrefValue<Map<String, SimpleTableDto>>(
        prefContext,
        PrefMapValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_tables",
            String::class.java,
            SimpleTableDto::class.java,
            mapOf()
        )
    )

    val shownPopupIdsAndTimestamp = PrefValue<Map<String, Long>>(
        prefContext,
        PrefMapValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "popup_keys",
            String::class.java,
            // Primitive Type 을 사용하지 못해 wrapping 된 타입을 넘겨준다.
            Long::class.javaObjectType,
            mapOf()
        )
    )

    val lastViewedTable = PrefValue<Optional<TableDto>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "last_tables",
            type = TableDto::class.java,
            defaultValue = Optional.empty()
        )
    )

    val tableTrimParam = PrefValue<TableTrimParam>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "table_trim_param",
            type = TableTrimParam::class.java,
            defaultValue = TableTrimParam.Default
        )
    )

    val themeMode = PrefValue<ThemeMode>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "theme_mode",
            type = ThemeMode::class.java,
            defaultValue = ThemeMode.AUTO
        )
    )

    val compactMode = PrefValue<Boolean>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "compact_mode",
            type = Boolean::class.java,
            defaultValue = false
        )
    )

    val firstBookmarkAlert = PrefValue<Boolean>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "first_bookmark_alert",
            type = Boolean::class.java,
            defaultValue = true
        )
    )

    val courseBooks = PrefValue<List<CourseBookDto>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_course_books",
            type = CourseBookDto::class.java,
            defaultValue = listOf()
        )
    )

    val tags = PrefValue<List<TagDto>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_tags",
            type = TagDto::class.java,
            defaultValue = listOf()
        )
    )

    val networkLog = PrefValue<List<NetworkLog>> (
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_network_log",
            type = NetworkLog::class.java,
            defaultValue = listOf()
        )
    )

    fun clearLoginScope() {
        prefContext.clear(DOMAIN_SCOPE_LOGIN)
        prefContext.clear(DOMAIN_SCOPE_CURRENT_VERSION)
    }

    companion object {
        const val DOMAIN_SCOPE_PERMANENT = "domain_scope_permanent"

        // 레거시 대응을 위해 어쩔 수 없음..
        const val DOMAIN_SCOPE_LOGIN = "com.wafflestudio.snutt2.live_preferences"
        const val DOMAIN_SCOPE_CURRENT_VERSION = "domain_scope_current_version"
    }
}
