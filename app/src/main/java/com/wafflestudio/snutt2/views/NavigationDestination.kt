import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class NavigationDestination(
    @Transient val deepLinkPath: String? = null,
) {
    @Serializable data object Onboard : NavigationDestination("onboard")

    @Serializable data object Tutorial : NavigationDestination("tutorial")

    @Serializable data object Home : NavigationDestination("home")

    @Serializable data object SignIn : NavigationDestination("signIn")

    @Serializable data object SignUp : NavigationDestination("signUp")

    @Serializable data object FindId : NavigationDestination("find_id")

    @Serializable data object FindPassword : NavigationDestination("find_password")

    @Serializable data object EmailVerification : NavigationDestination("email_verification")

    @Serializable data object LecturesOfTable : NavigationDestination("lectures_of_table")

    @Serializable data object LectureDetail : NavigationDestination("lecture_detail")

    @Serializable data class TimetableLecture(val tableId: String? = null) : NavigationDestination("timetable-lecture")

    @Serializable data object LectureColorSelector : NavigationDestination("lecture_color_selector")

    @Serializable data object Notification : NavigationDestination("notifications")

    @Serializable data object AppReport : NavigationDestination("app_report")

    @Serializable data object OpenLicenses : NavigationDestination("open_licenses")

    @Serializable data class LicenseDetail(val licenseName: String? = null) : NavigationDestination("license_detail")

    @Serializable data object ServiceInfo : NavigationDestination("service_info")

    @Serializable data object TeamInfo : NavigationDestination("team_info")

    @Serializable data object TimeTableConfig : NavigationDestination("timetable_config")

    @Serializable data object UserConfig : NavigationDestination("user_config")

    @Serializable data object ChangeNickname : NavigationDestination("change_nickname")

    @Serializable data object PersonalInformationPolicy : NavigationDestination("personal_information_policy")

    @Serializable data object ThemeModeSelect : NavigationDestination("theme_mode_select")

    @Serializable data object Bookmark : NavigationDestination("bookmarks")

    @Serializable data object NetworkLog : NavigationDestination("network_log")

    @Serializable data object VacancyNotification : NavigationDestination("vacancy")

    @Serializable data object ThemeMarket : NavigationDestination("theme_market")

    @Serializable data object Friends : NavigationDestination("friends")

    @Serializable data object ThemeConfig : NavigationDestination("theme_config")

    @Serializable data class ThemeDetail(val themeId: String = "", val theme: Int = -1) : NavigationDestination("theme_detail")

    @Serializable data object SocialLink : NavigationDestination("social_link")

    @Serializable data object ImportantNotice : NavigationDestination("important_notice")
}
