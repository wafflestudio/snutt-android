package com.wafflestudio.snutt2.core.database.preference

import com.wafflestudio.snutt2.core.database.model.CourseBook
import com.wafflestudio.snutt2.core.database.model.NetworkLog
import com.wafflestudio.snutt2.core.database.model.SimpleTable
import com.wafflestudio.snutt2.core.database.model.Table
import com.wafflestudio.snutt2.core.database.model.TableTrimParam
import com.wafflestudio.snutt2.core.database.model.Tag
import com.wafflestudio.snutt2.core.database.model.ThemeMode
import com.wafflestudio.snutt2.core.database.model.User
import com.wafflestudio.snutt2.core.database.preference.context.PrefContext
import com.wafflestudio.snutt2.core.database.preference.context.PrefListValueMetaData
import com.wafflestudio.snutt2.core.database.preference.context.PrefMapValueMetaData
import com.wafflestudio.snutt2.core.database.preference.context.PrefOptionalValueMetaData
import com.wafflestudio.snutt2.core.database.preference.context.PrefValue
import com.wafflestudio.snutt2.core.database.preference.context.PrefValueMetaData
import com.wafflestudio.snutt2.core.database.util.Optional
import javax.inject.Inject
import javax.inject.Singleton

interface SNUTTStorageTemp {
    val prefKeyUserId: PrefValue<Optional<String>>

    val accessToken: PrefValue<String>

    val user: PrefValue<Optional<User>>

    val tableMap: PrefValue<Map<String, SimpleTable>>

    val shownPopupIdsAndTimestamp: PrefValue<Map<String, Long>>

    val lastViewedTable: PrefValue<Optional<Table>>

    val tableTrimParam: PrefValue<TableTrimParam>

    val themeMode: PrefValue<ThemeMode>

    val compactMode: PrefValue<Boolean>

    val firstBookmarkAlert: PrefValue<Boolean>

    val courseBooks: PrefValue<List<CourseBook>>

    val tags: PrefValue<List<Tag>>

    val networkLog: PrefValue<List<NetworkLog>>

    val firstVacancyVisit: PrefValue<Boolean>

    fun clearLoginScope()

    fun addNetworkLog(newLog: NetworkLog)
}

@Singleton
class SNUTTStorageTempImpl @Inject constructor(
    private val prefContext: PrefContext,
) : SNUTTStorageTemp {

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

    override val user = PrefValue<Optional<User>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_user",
            type = User::class.java,
            defaultValue = Optional.empty(),
        ),
    )

    override val tableMap = PrefValue<Map<String, SimpleTable>>(
        prefContext,
        PrefMapValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_tables",
            String::class.java,
            SimpleTable::class.java,
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

    override val lastViewedTable = PrefValue<Optional<Table>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "last_tables",
            type = Table::class.java,
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

    override val courseBooks = PrefValue<List<CourseBook>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_course_books",
            type = CourseBook::class.java,
            defaultValue = listOf(),
        ),
    )

    override val tags = PrefValue<List<Tag>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_tags",
            type = Tag::class.java,
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
