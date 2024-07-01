package com.wafflestudio.snutt2.core.data

import com.wafflestudio.snutt2.core.network.model.*
import com.wafflestudio.snutt2.core.data.model.*

fun BuildingsResponseT.toNetworkModel() = BuildingsResponse(
    content = this.content.map { it.toNetworkModel() },
    totalCount = this.totalCount
)

fun CampusT.toNetworkModel(): Campus {
    return when (this){
        CampusT.GWANAK -> Campus.GWANAK
        CampusT.YEONGEON -> Campus.YEONGEON
        CampusT.PYEONGCHANG -> Campus.PYEONGCHANG
    }
}

fun ClassTimeDtoT.toNetworkModel() = ClassTimeDto(
    day = this.day,
    place = this.place,
    id = this.id,
    startMinute = this.startMinute,
    endMinute = this.endMinute,
)

fun ColorDtoT.toNetworkModel() = ColorDto(
    fgRaw = this.fgRaw,
    bgRaw = this.bgRaw,
)

fun CourseBookDtoT.toNetworkModel() = CourseBookDto(
    semester = this.semester,
    year = this.year,
)

fun DeleteFirebaseTokenResultsT.toNetworkModel() = DeleteFirebaseTokenResults(
    message = this.message,
)

fun DeleteTableResultsT.toNetworkModel() = this.map { it.toNetworkModel() }

fun DeleteUserAccountResultsT.toNetworkModel() = DeleteUserAccountResults(
    message = this.message,
)

fun DeleteUserFacebookResultsT.toNetworkModel() = DeleteUserFacebookResults(
    token = this.token,
)

fun ErrorDTOT.toNetworkModel() = ErrorDTO(
    code = this.code,
    message = this.message,
    displayMessage = this.displayMessage,
    ext = this.ext,
)

fun GeoCoordinateT.toNetworkModel() = GeoCoordinate(
    latitude = this.latitude,
    longitude = this.longitude,
)

fun GetBookmarkListResultsT.toNetworkModel() = GetBookmarkListResults(
    year = this.year,
    semester = this.semester,
    lectures = this.lectures.map { it.toNetworkModel() },
)

fun GetCoursebooksOfficialResultsT.toNetworkModel() = GetCoursebooksOfficialResults(
    url = this.url,
)

fun GetLecturesIdResultsT.toNetworkModel() = GetLecturesIdResults(
    id = this.id,
)

fun GetNotificationCountResultsT.toNetworkModel() = GetNotificationCountResults(
    count = this.count,
)

fun GetNotificationResultsT.toNetworkModel() = this.map { it.toNetworkModel() }

fun GetPopupResultsT.toNetworkModel() = GetPopupResults(
    popups = this.popups.map { it.toNetworkModel() },
)

fun GetPopupResultsT.PopupT.toNetworkModel() = GetPopupResults.Popup(
    key = this.key,
    uri = this.uri,
    popupHideDays = popupHideDays,
)

fun GetTagListResultsT.toNetworkModel() = GetTagListResults(
    classification = this.classification,
    department = this.department,
    academicYear = this.academicYear,
    credit = this.credit,
    instructor = this.instructor,
    category = this.category,
)

fun GetThemesResultsT.toNetworkModel() = this.map { it.toNetworkModel() }

fun GetUserFacebookResultsT.toNetworkModel() = GetUserFacebookResults(
    name = this.name,
    attached = this.attached,
)

fun LectureBuildingDtoT.toNetworkModel() = LectureBuildingDto(
    id = this.id,
    buildingNumber = this.buildingNumber,
    buildingNameKor = this.buildingNameKor,
    buildingNameEng = this.buildingNameEng,
    locationInDMS = this.locationInDMS.toNetworkModel(),
    locationInDecimal = this.locationInDecimal.toNetworkModel(),
    campus = this.campus.toNetworkModel(),
)

fun LectureDtoT.toNetworkModel() = LectureDto(
    id = this.id,
    lecture_id = this.lecture_id,
    classification = this.classification,
    department = this.department,
    academic_year = this.academic_year,
    course_number = this.course_number,
    lecture_number = this.lecture_number,
    course_title = this.course_title,
    credit = this.credit,
    class_time_json = this.class_time_json.map { it.toNetworkModel() },
    instructor = this.instructor,
    quota = this.quota,
    freshmanQuota = this.freshmanQuota,
    remark = this.remark,
    category = this.category,
    colorIndex = this.colorIndex,
    color = this.color.toNetworkModel(),
    registrationCount = this.registrationCount,
    wasFull = this.wasFull,
)

fun NicknameDtoT.toNetworkModel() = NicknameDto(
    nickname = this.nickname,
    tag = this.tag,
)

fun NotificationDtoT.toNetworkModel() = NotificationDto(
    id = this.id,
    title = this.title,
    message = this.message,
    createdAt = this.createdAt,
    type = this.type,
    detail = this.detail?.toNetworkModel(),
    deeplink = this.deeplink,
)

fun NotificationDtoT.DetailT.toNetworkModel() = NotificationDto.Detail(
    courseTitle = this.courseTitle,
    lectureNumber = this.lectureNumber,
    courseNumber = this.courseNumber,
)

fun PatchThemeParamsT.toNetworkModel() = PatchThemeParams(
    name = this.name,
    colors = this.colors.map { it.toNetworkModel() },
)

fun PatchUserInfoParamsT.toNetworkModel() = PatchUserInfoParams(
    nickname = this.nickname,
)

fun PostBookmarkParamsT.toNetworkModel() = PostBookmarkParams(
    id = this.id,
)

fun PostCheckEmailByIdParamsT.toNetworkModel() = PostCheckEmailByIdParams(
    id = this.id,
)

fun PostCheckEmailByIdResultsT.toNetworkModel() = PostCheckEmailByIdResults(
    email = this.email,
)

fun PostCustomLectureParamsT.toNetworkModel() = PostCustomLectureParams(
    id = this.id,
    classification = this.classification,
    department = this.department,
    academic_year = this.academic_year,
    course_number = this.course_number,
    lecture_number = this.lecture_number,
    course_title = this.course_title,
    credit = credit,
    class_time = this.class_time,
    class_time_json = this.class_time_json?.map { it.toNetworkModel() },
    location = this.location,
    instructor = this.instructor,
    quota = this.quota,
    enrollment = this.enrollment,
    remark = this.remark,
    category = this.category,
    colorIndex = this.colorIndex,
    color = this.color?.toNetworkModel(),
    isForced = this.isForced,
)

fun PostFeedbackParamsT.toNetworkModel() = PostFeedbackParams(
    email = this.email,
    message = this.message,
)

fun PostFeedbackResultsT.toNetworkModel() = PostFeedbackResults(
    message = this.message,
)

fun PostFindIdParamsT.toNetworkModel() = PostFindIdParams(
    email = this.email,
)

fun PostFindIdResultsT.toNetworkModel() = PostFindIdResults(
    message = this.message,
)

fun PostForceLogoutParamsT.toNetworkModel() = PostForceLogoutParams(
    userId = this.userId,
    registrationId = this.registrationId,
)

fun PostForceLogoutResultsT.toNetworkModel() = PostForceLogoutResults(
    message = this.message,
)

fun PostLectureParamsT.toNetworkModel() = PostLectureParams(
    id = this.id,
)

fun PostLoginFacebookParamsT.toNetworkModel() = PostLoginFacebookParams(
    facebookId = this.facebookId,
    facebookToken = this.facebookToken,
)

fun PostLoginFacebookResultsT.toNetworkModel() = PostLoginFacebookResults(
    token = this.token,
    userId = this.userId,
)

fun PostResetPasswordParamsT.toNetworkModel() = PostResetPasswordParams(
    id = this.id,
    password = this.password,
)

fun PostSearchQueryParamsT.toNetworkModel() = PostSearchQueryParams(
    year = this.year,
    semester = this.semester,
    title = this.title,
    classification = this.classification,
    credit = this.credit,
    courseNumber = this.courseNumber,
    academic_year = this.academic_year,
    department = this.department,
    category = this.category,
    etc = this.etc,
    times = this.times?.map { it.toNetworkModel() },
    timesToExclude = this.timesToExclude?.map { it.toNetworkModel() },
    offset = this.offset,
    limit = this.limit,
)

fun PostSendCodeToEmailParamsT.toNetworkModel() = PostSendCodeToEmailParams(
    email = this.email,
)

fun PostSendPwResetCodeParamsT.toNetworkModel() = PostSendPwResetCodeParams(
    email = this.email,
)

fun PostSignInParamsT.toNetworkModel() = PostSignInParams(
    id = this.id,
    password = this.password,
)

fun PostSignInResultsT.toNetworkModel() = PostSignInResults(
    token = this.token,
    userId = this.userId,
)

fun PostSignUpParamsT.toNetworkModel() = PostSignUpParams(
    id = this.id,
    password = this.password,
    email = this.email,
)

fun PostSignUpResultsT.toNetworkModel() = PostSignUpResults(
    message = this.message,
    token = this.token,
    userId = this.userId,
)

fun PostTableParamsT.toNetworkModel() = PostTableParams(
    year = this.year,
    semester = this.semester,
    title = this.title,
)

fun PostThemeParamsT.toNetworkModel() = PostThemeParams(
    name = this.name,
    colors = this.colors.map { it.toNetworkModel() },
)

fun PostUserFacebookParamsT.toNetworkModel() = PostUserFacebookParams(
    facebookId = this.facebookId,
    facebookToken = this.facebookToken,
)

fun PostUserFacebookResultsT.toNetworkModel() = PostUserFacebookResults(
    token = this.token,
)

fun PostUserPasswordParamsT.toNetworkModel() = PostUserPasswordParams(
    id = this.id,
    password = this.password,
)

fun PostUserPasswordResultsT.toNetworkModel() = PostUserPasswordResults(
    token = this.token,
)

fun PostVerifyEmailCodeParamsT.toNetworkModel() = PostVerifyEmailCodeParams(
    code = this.code,
)

fun PostVerifyPwResetCodeParamsT.toNetworkModel() =
    PostVerifyPwResetCodeParamsT(
        id = this.id,
        code = this.code,
    )

fun PutLectureParamsT.toNetworkModel() = PutLectureParams(
    id = this.id,
    classification = this.classification,
    department = this.department,
    academic_year = this.academic_year,
    course_number = this.course_number,
    lecture_number = this.lecture_number,
    course_title = this.course_title,
    credit = credit,
    class_time = this.class_time,
    class_time_json = this.class_time_json?.map { it.toNetworkModel() },
    location = this.location,
    instructor = this.instructor,
    quota = this.quota,
    enrollment = this.enrollment,
    remark = this.remark,
    category = this.category,
    colorIndex = this.colorIndex,
    color = this.color?.toNetworkModel(),
    isForced = this.isForced,
)

fun PutTableParamsT.toNetworkModel() = PutTableParams(
    title = this.title,
)

fun PutTableThemeParamsT.toNetworkModel() = PutTableThemeParams(
    theme = this.theme,
    themeId = this.themeId,
)

fun PutUserPasswordParamsT.toNetworkModel() = PutUserPasswordParams(
    newPassword = this.newPassword,
    oldPassword = this.oldPassword,
)

fun PutUserPasswordResultsT.toNetworkModel() = PutUserPasswordResults(
    token = this.token,
)

fun RegisterFirebaseTokenParamsT.toNetworkModel() = RegisterFirebaseTokenParams()

fun RegisterFirebaseTokenResultsT.toNetworkModel() = RegisterFirebaseTokenResults(
    message = this.message,
)

fun RemoteConfigDtoT.toNetworkModel() = RemoteConfigDto(
    reactNativeBundleSrc = this.reactNativeBundleSrc?.toNetworkModel(),
    vacancyBannerConfig = this.vacancyBannerConfig.toNetworkModel(),
    vacancyUrlConfig = this.vacancyUrlConfig.toNetworkModel(),
    settingsBadgeConfig = this.settingsBadgeConfig.toNetworkModel(),
    disableMapFeature = this.disableMapFeature,
)

fun RemoteConfigDtoT.ReactNativeBundleSrcT.toNetworkModel() = RemoteConfigDto.ReactNativeBundleSrc(
    src = this.src,
)

fun RemoteConfigDtoT.SettingsBadgeConfigT.toNetworkModel() = RemoteConfigDto.SettingsBadgeConfig(
    new = this.new,
)

fun RemoteConfigDtoT.VacancyBannerConfigT.toNetworkModel() = RemoteConfigDto.VacancyBannerConfig(
    visible = this.visible,
)

fun RemoteConfigDtoT.VacancyUrlConfigT.toNetworkModel() = RemoteConfigDto.VacancyUrlConfig(
    url = this.url,
)

fun SearchTimeDtoT.toNetworkModel() = SearchTimeDto(
    day = this.day,
    startMinute = this.startMinute,
    endMinute = this.endMinute,
)

fun SimpleTableDtoT.toNetworkModel() = SimpleTableDto(
    id = this.id,
    year = this.year,
    semester = this.semester,
    title = this.title,
    updatedAt = this.updatedAt,
    totalCredit = this.totalCredit,
    isPrimary = this.isPrimary,
)

fun TableDtoT.toNetworkModel() = TableDto(
    id = this.id,
    year = this.year,
    semester = this.semester,
    title = this.title,
    lectureList = this.lectureList.map { it.toNetworkModel()},
    updatedAt = this.updatedAt,
    totalCredit = this.totalCredit,
    theme = this.theme,
    themeId = this.themeId,
    isPrimary = this.isPrimary,
)

fun CustomThemeT.toNetworkModel() = CustomTheme(
    id = this.id,
    name = this.name,
    colors = this.colors.map { it.toNetworkModel() },
)

fun BuiltInThemeT.toNetworkModel() = BuiltInTheme(
    code = this.code,
    name = this.name,
)

fun ThemeDtoT.toNetworkModel() = ThemeDto(
    id = this.id,
    theme = this.theme,
    name = this.name,
    colors = this.colors.map { it.toNetworkModel() },
    isCustom = this.isCustom,
)

fun UserDtoT.toNetworkModel() = UserDto(
    isAdmin = this.isAdmin,
    regDate = this.regDate,
    notificationCheckedAt = this.notificationCheckedAt,
    email = this.email,
    localId = this.localId,
    fbName = this.fbName,
    nickname = this.nickname?.toNetworkModel(),
)