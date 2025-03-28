import kotlinx.serialization.Serializable

sealed interface NavigationDestination {
    @Serializable data object Onboard : NavigationDestination

    @Serializable data object Tutorial : NavigationDestination

    @Serializable data object Home : NavigationDestination

    @Serializable data object SignIn : NavigationDestination

    @Serializable data object SignUp : NavigationDestination

    @Serializable data object FindId : NavigationDestination

    @Serializable data object FindPassword : NavigationDestination

    @Serializable data object EmailVerification : NavigationDestination

    @Serializable data object LecturesOfTable : NavigationDestination

    @Serializable data object LectureDetail : NavigationDestination

    @Serializable data class TimetableLecture(val tableId: String? = null) : NavigationDestination

    @Serializable data object LectureColorSelector : NavigationDestination

    @Serializable data object Notification : NavigationDestination

    @Serializable data object AppReport : NavigationDestination

    @Serializable data object OpenLicenses : NavigationDestination

    @Serializable data class LicenseDetail(val licenseName: String) : NavigationDestination

    @Serializable data object ServiceInfo : NavigationDestination

    @Serializable data object TeamInfo : NavigationDestination

    @Serializable data object TimeTableConfig : NavigationDestination

    @Serializable data object UserConfig : NavigationDestination

    @Serializable data object ChangeNickname : NavigationDestination

    @Serializable data object PersonalInformationPolicy : NavigationDestination

    @Serializable data object ThemeModeSelect : NavigationDestination

    @Serializable data object Bookmark : NavigationDestination

    @Serializable data object NetworkLog : NavigationDestination

    @Serializable data object VacancyNotification : NavigationDestination

    @Serializable data object ThemeMarket : NavigationDestination

    @Serializable data object Friends : NavigationDestination

    @Serializable data object ThemeConfig : NavigationDestination

    @Serializable data class ThemeDetail(val themeId: String = "", val theme: Int = -1) : NavigationDestination

    @Serializable data object SocialLink : NavigationDestination

    @Serializable data object ImportantNotice : NavigationDestination
}
