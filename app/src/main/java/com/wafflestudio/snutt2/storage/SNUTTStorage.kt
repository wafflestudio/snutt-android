package com.wafflestudio.snutt2.storage

import com.wafflestudio.snutt2.lib.android.NetworkLog
import com.wafflestudio.snutt2.network.dto.CourseBookDto
import com.wafflestudio.snutt2.network.dto.SemesterStatusDto
import com.wafflestudio.snutt2.network.dto.SimpleTableDto
import com.wafflestudio.snutt2.network.dto.TableDto
import com.wafflestudio.snutt2.network.dto.UserDto
import com.wafflestudio.snutt2.storage.model.TableLectureCustomData
import com.wafflestudio.snutt2.storage.model.TableTrimParamData
import com.wafflestudio.snutt2.storage.model.TagLocalEntity
import com.wafflestudio.snutt2.storage.pref.PrefContext
import com.wafflestudio.snutt2.storage.pref.PrefListValueMetaData
import com.wafflestudio.snutt2.storage.pref.PrefMapValueMetaData
import com.wafflestudio.snutt2.storage.pref.PrefOptionalValueMetaData
import com.wafflestudio.snutt2.storage.pref.PrefValue
import com.wafflestudio.snutt2.storage.pref.PrefValueMetaData
import com.wafflestudio.snutt2.ui.theme.ThemeMode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SNUTTStorage @Inject constructor(
    private val prefContext: PrefContext,
) {

    val prefKeyUserId = PrefValue<Optional<String>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_key_user_id",
            type = String::class.java,
            defaultValue = Optional.empty(),
        ),
    )

    val accessToken = PrefValue<String>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_key_x_access_token",
            type = String::class.java,
            defaultValue = "",
        ),
    )

    val user = PrefValue<Optional<UserDto>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "pref_user",
            type = UserDto::class.java,
            defaultValue = Optional.empty(),
        ),
    )

    val tableMap = PrefValue<Map<String, SimpleTableDto>>(
        prefContext,
        PrefMapValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_tables",
            String::class.java,
            SimpleTableDto::class.java,
            mapOf(),
        ),
    )

    val shownPopupIdsAndTimestamp = PrefValue<Map<String, Long>>(
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

    val lastViewedTable = PrefValue<Optional<TableDto>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "last_tables",
            type = TableDto::class.java,
            defaultValue = Optional.empty(),
        ),
    )

    val tableTrimParam = PrefValue<TableTrimParamData>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "table_trim_param",
            type = TableTrimParamData::class.java,
            defaultValue = TableTrimParamData.Default,
        ),
    )

    val tableLectureCustom = PrefValue<TableLectureCustomData>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "table_lecture_custom",
            type = TableLectureCustomData::class.java,
            defaultValue = TableLectureCustomData.Default,
        ),
    )

    val themeMode = PrefValue<ThemeMode>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "theme_mode",
            type = ThemeMode::class.java,
            defaultValue = ThemeMode.AUTO,
        ),
    )

    val compactMode = PrefValue<Boolean>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "compact_mode",
            type = Boolean::class.java,
            defaultValue = false,
        ),
    )

    val firstBookmarkAlert = PrefValue<Boolean>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "first_bookmark_alert",
            type = Boolean::class.java,
            defaultValue = true,
        ),
    )

    val courseBooks = PrefValue<List<CourseBookDto>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_course_books",
            type = CourseBookDto::class.java,
            defaultValue = listOf(),
        ),
    )

    val networkLog = PrefValue<List<NetworkLog>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_CURRENT_VERSION,
            key = "pref_network_log",
            type = NetworkLog::class.java,
            defaultValue = listOf(),
        ),
    )

    val firstVacancyVisit = PrefValue<Boolean>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "first_vacancy_visit",
            type = Boolean::class.java,
            defaultValue = true,
        ),
    )

    val isVisitedSessionlessLectureList = PrefValue<Boolean>(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "is_visited_sessionless_lecture_list",
            type = Boolean::class.java,
            defaultValue = false,
        ),
    )

    val firstVacancyAdd = PrefValue(
        prefContext,
        PrefValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "first_vacancy_add",
            type = Boolean::class.java,
            defaultValue = true,
        ),
    )

    val recentSearchedDepartments = PrefValue<List<TagLocalEntity>>(
        prefContext,
        PrefListValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "recent_searched_departments",
            type = TagLocalEntity::class.java,
            defaultValue = listOf(),
        ),
    )

    val semesterStatus = PrefValue<Optional<SemesterStatusDto>>(
        prefContext,
        PrefOptionalValueMetaData(
            domain = DOMAIN_SCOPE_LOGIN,
            key = "semester_status",
            type = SemesterStatusDto::class.java,
            defaultValue = Optional.empty(),
        ),
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
