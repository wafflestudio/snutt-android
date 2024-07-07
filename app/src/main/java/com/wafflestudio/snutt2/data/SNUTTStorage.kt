package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.core.qualifiers.App
import com.wafflestudio.snutt2.core.qualifiers.CoreDatabase
import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.NetworkLog
import com.wafflestudio.snutt2.lib.network.dto.core.*
import com.wafflestudio.snutt2.lib.preferences.context.*
import com.wafflestudio.snutt2.model.TableTrimParam
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.ui.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton

@App
interface SNUTTStorage {
    val prefKeyUserId: PrefValue<Optional<String>>

    val accessToken: PrefValue<String>

    val user: PrefValue<Optional<UserDto>>

    val tableMap: PrefValue<Map<String, SimpleTableDto>>

    val shownPopupIdsAndTimestamp: PrefValue<Map<String, Long>>

    val lastViewedTable: PrefValue<Optional<TableDto>>

    val tableTrimParam: PrefValue<TableTrimParam>

    val themeMode: PrefValue<ThemeMode>

    val compactMode: PrefValue<Boolean>

    val firstBookmarkAlert: PrefValue<Boolean>

    val courseBooks: PrefValue<List<CourseBookDto>>

    val tags: PrefValue<List<TagDto>>

    val networkLog: PrefValue<List<NetworkLog>>

    val firstVacancyVisit: PrefValue<Boolean>

    fun clearLoginScope()

    fun addNetworkLog(newLog: NetworkLog)
}

@Singleton
@App
class SNUTTStorageImpl @Inject constructor(
    @App private val prefContext: PrefContext,
):SNUTTStorage {

    override val prefKeyUserId = PrefValue<Optional<String>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_key_user_id",
            type = String::class.java,
            defaultValue = Optional.empty(),
        ),
    )

    override val accessToken = PrefValue<String>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_key_x_access_token",
            type = String::class.java,
            defaultValue = "",
        ),
    )

    override val user = PrefValue<Optional<UserDto>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_user",
            type = UserDto::class.java,
            defaultValue = Optional.empty(),
        ),
    )

    override val tableMap = PrefValue<Map<String, SimpleTableDto>>(
        prefContext,
        PrefMapValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_tables",
            String::class.java,
            SimpleTableDto::class.java,
            mapOf(),
        ),
    )

    override val shownPopupIdsAndTimestamp = PrefValue<Map<String, Long>>(
        prefContext,
        PrefMapValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "popup_keys",
            String::class.java,
            // Primitive Type 을 사용하지 못해 wrapping 된 타입을 넘겨준다.
            Long::class.javaObjectType,
            mapOf(),
        ),
    )

    override val lastViewedTable = PrefValue<Optional<TableDto>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "last_tables",
            type = TableDto::class.java,
            defaultValue = Optional.empty(),
        ),
    )

    override val tableTrimParam = PrefValue<TableTrimParam>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "table_trim_param",
            type = TableTrimParam::class.java,
            defaultValue = TableTrimParam.Default,
        ),
    )

    override val themeMode = PrefValue<ThemeMode>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "theme_mode",
            type = ThemeMode::class.java,
            defaultValue = ThemeMode.AUTO,
        ),
    )

    override val compactMode = PrefValue<Boolean>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "compact_mode",
            type = Boolean::class.java,
            defaultValue = false,
        ),
    )

    override val firstBookmarkAlert = PrefValue<Boolean>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "first_bookmark_alert",
            type = Boolean::class.java,
            defaultValue = true,
        ),
    )

    override val courseBooks = PrefValue<List<CourseBookDto>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_course_books",
            type = CourseBookDto::class.java,
            defaultValue = listOf(),
        ),
    )

    override val tags = PrefValue<List<TagDto>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_tags",
            type = TagDto::class.java,
            defaultValue = listOf(),
        ),
    )

    override val networkLog = PrefValue<List<NetworkLog>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_network_log",
            type = NetworkLog::class.java,
            defaultValue = listOf(),
        ),
    )

    override val firstVacancyVisit = PrefValue<Boolean>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "first_vacancy_visit",
            type = Boolean::class.java,
            defaultValue = true,
        ),
    )

    override fun clearLoginScope() {
        prefContext.clear(DOMAIN_SCOPE_LOGIN)
        prefContext.clear(DOMAIN_SCOPE_CURRENT_VERSION)
    }

    override fun addNetworkLog(newLog: NetworkLog) {
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

    companion object {
        const val DOMAIN_SCOPE_PERMANENT = "domain_scope_permanent"

        // 레거시 대응을 위해 어쩔 수 없음..
        const val DOMAIN_SCOPE_LOGIN = "com.wafflestudio.snutt2.live_preferences"
        const val DOMAIN_SCOPE_CURRENT_VERSION = "domain_scope_current_version"
    }
}
