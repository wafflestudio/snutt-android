import com.wafflestudio.snutt2.navigation.DeepLinkPath
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationDestination {

    @Serializable
    @DeepLinkPath("onboard")
    data object Onboard : NavigationDestination

    @Serializable
    @DeepLinkPath("tutorial")
    data object Tutorial : NavigationDestination

    @Serializable
    @DeepLinkPath("home")
    data object Home : NavigationDestination

    @Serializable
    @DeepLinkPath("signIn")
    data object SignIn : NavigationDestination

    @Serializable
    @DeepLinkPath("signUp")
    data object SignUp : NavigationDestination

    @Serializable
    @DeepLinkPath("find_id")
    data object FindId : NavigationDestination

    @Serializable
    @DeepLinkPath("find_password")
    data object FindPassword : NavigationDestination

    @Serializable
    @DeepLinkPath("email_verification")
    data object EmailVerification : NavigationDestination

    @Serializable
    @DeepLinkPath("lectures_of_table")
    data object LecturesOfTable : NavigationDestination

    @Serializable
    @DeepLinkPath("lecture_detail")
    data object LectureDetail : NavigationDestination

    @Serializable
    @DeepLinkPath("timetable-lecture")
    data class TimetableLecture(val tableId: String? = null) : NavigationDestination

    @Serializable
    @DeepLinkPath("lecture_color_selector")
    data object LectureColorSelector : NavigationDestination

    @Serializable
    @DeepLinkPath("notifications")
    data object Notification : NavigationDestination

    @Serializable
    @DeepLinkPath("app_report")
    data object AppReport : NavigationDestination

    @Serializable
    @DeepLinkPath("open_licenses")
    data object OpenLicenses : NavigationDestination

    @Serializable
    @DeepLinkPath("license_detail")
    data class LicenseDetail(val licenseName: String? = null) : NavigationDestination

    @Serializable
    @DeepLinkPath("service_info")
    data object ServiceInfo : NavigationDestination

    @Serializable
    @DeepLinkPath("team_info")
    data object TeamInfo : NavigationDestination

    @Serializable
    @DeepLinkPath("timetable_config")
    data object TimeTableConfig : NavigationDestination

    @Serializable
    @DeepLinkPath("user_config")
    data object UserConfig : NavigationDestination

    @Serializable
    @DeepLinkPath("change_nickname")
    data object ChangeNickname : NavigationDestination

    @Serializable
    @DeepLinkPath("personal_information_policy")
    data object PersonalInformationPolicy : NavigationDestination

    @Serializable
    @DeepLinkPath("theme_mode_select")
    data object ThemeModeSelect : NavigationDestination

    @Serializable
    @DeepLinkPath("bookmarks")
    data object Bookmark : NavigationDestination

    @Serializable
    @DeepLinkPath("network_log")
    data object NetworkLog : NavigationDestination

    @Serializable
    @DeepLinkPath("test")
    data object Test : NavigationDestination

    @Serializable
    @DeepLinkPath("vacancy")
    data object VacancyNotification : NavigationDestination

    @Serializable
    @DeepLinkPath("theme_market")
    data object ThemeMarket : NavigationDestination

    @Serializable
    @DeepLinkPath("push_preferences")
    data object PushPreferences : NavigationDestination

    @Serializable
    @DeepLinkPath("lecture_diary")
    data object LectureDiary : NavigationDestination

    @Serializable
    @DeepLinkPath("lecture_diary_write")
    data object LectureDiaryWrite : NavigationDestination

    @Serializable
    @DeepLinkPath("friends")
    data object Friends : NavigationDestination

    @Serializable
    @DeepLinkPath("theme_config")
    data object ThemeConfig : NavigationDestination

    @Serializable
    @DeepLinkPath("theme_detail")
    data class ThemeDetail(val themeId: String = "", val theme: Int = -1) : NavigationDestination

    @Serializable
    @DeepLinkPath("social_link")
    data object SocialLink : NavigationDestination

    @Serializable
    @DeepLinkPath("important_notice")
    data object ImportantNotice : NavigationDestination
}
