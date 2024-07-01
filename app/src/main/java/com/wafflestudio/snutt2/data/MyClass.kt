package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.core.data.model.*
import com.wafflestudio.snutt2.lib.network.ErrorDTO
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.dto.core.*
import com.wafflestudio.snutt2.model.*

fun BuildingsResponseT.toExternalModel() = BuildingsResponse(
    content = this.content.map { it.toExternalModel() },
    totalCount = this.totalCount,
)

fun CampusT.toExternalModel(): Campus {
    return when (this) {
        CampusT.GWANAK -> Campus.GWANAK
        CampusT.YEONGEON -> Campus.YEONGEON
        CampusT.PYEONGCHANG -> Campus.PYEONGCHANG
    }
}

fun ClassTimeDtoT.toExternalModel() = ClassTimeDto(
    day = this.day,
    place = this.place,
    id = this.id,
    startMinute = this.startMinute,
    endMinute = this.endMinute,
)

fun ColorDtoT.toExternalModel() = ColorDto(
    fgRaw = this.fgRaw,
    bgRaw = this.bgRaw,
)

fun CourseBookDtoT.toExternalModel() = CourseBookDto(
    semester = this.semester,
    year = this.year,
)

fun DeleteFirebaseTokenResultsT.toExternalModel() = DeleteFirebaseTokenResults(
    message = this.message,
)

fun DeleteTableResultsT.toExternalModel() = this.map { it.toExternalModel() }

fun DeleteUserAccountResultsT.toExternalModel() = DeleteUserAccountResults(
    message = this.message,
)

fun DeleteUserFacebookResultsT.toExternalModel() = DeleteUserFacebookResults(
    token = this.token,
)

fun ErrorDTOT.toExternalModel() = ErrorDTO(
    code = this.code,
    message = this.message,
    displayMessage = this.displayMessage,
    ext = this.ext,
)

fun GeoCoordinateT.toExternalModel() = GeoCoordinate(
    latitude = this.latitude,
    longitude = this.longitude,
)

fun GetBookmarkListResultsT.toExternalModel() = GetBookmarkListResults(
    year = this.year,
    semester = this.semester,
    lectures = this.lectures.map { it.toExternalModel() },
)

fun GetCoursebooksOfficialResultsT.toExternalModel() = GetCoursebooksOfficialResults(
    url = this.url,
)

fun GetLecturesIdResultsT.toExternalModel() = GetLecturesIdResults(
    id = this.id,
)

fun GetNotificationCountResultsT.toExternalModel() = GetNotificationCountResults(
    count = this.count,
)

fun GetNotificationResultsT.toExternalModel() = this.map { it.toExternalModel() }

fun GetPopupResultsT.toExternalModel() = GetPopupResults(
    popups = this.popups.map { it.toExternalModel() },
)

fun GetPopupResultsT.PopupT.toExternalModel() = GetPopupResults.Popup(
    key = this.key,
    uri = this.uri,
    popupHideDays = popupHideDays,
)

fun GetTagListResultsT.toExternalModel() = GetTagListResults(
    classification = this.classification,
    department = this.department,
    academicYear = this.academicYear,
    credit = this.credit,
    instructor = this.instructor,
    category = this.category,
)

fun GetThemesResultsT.toExternalModel() = this.map { it.toExternalModel() }

fun GetUserFacebookResultsT.toExternalModel() = GetUserFacebookResults(
    name = this.name,
    attached = this.attached,
)

fun GetVacancyLecturesResultsT.toExternalModel() = GetVacancyLecturesResults(
    lectures = this.lectures.map { it.toExternalModel() },
)

fun LectureBuildingDtoT.toExternalModel() = LectureBuildingDto(
    id = this.id,
    buildingNumber = this.buildingNumber,
    buildingNameKor = this.buildingNameKor,
    buildingNameEng = this.buildingNameEng,
    locationInDMS = this.locationInDMS.toExternalModel(),
    locationInDecimal = this.locationInDecimal.toExternalModel(),
    campus = this.campus.toExternalModel(),
)

fun LectureDtoT.toExternalModel() = LectureDto(
    id = this.id,
    lecture_id = this.lecture_id,
    classification = this.classification,
    department = this.department,
    academic_year = this.academic_year,
    course_number = this.course_number,
    lecture_number = this.lecture_number,
    course_title = this.course_title,
    credit = this.credit,
    class_time_json = this.class_time_json.map { it.toExternalModel() },
    instructor = this.instructor,
    quota = this.quota,
    freshmanQuota = this.freshmanQuota,
    remark = this.remark,
    category = this.category,
    colorIndex = this.colorIndex,
    color = this.color.toExternalModel(),
    registrationCount = this.registrationCount,
    wasFull = this.wasFull,
)

fun NicknameDtoT.toExternalModel() = NicknameDto(
    nickname = this.nickname,
    tag = this.tag,
)

fun NotificationDtoT.toExternalModel() = NotificationDto(
    id = this.id,
    title = this.title,
    message = this.message,
    createdAt = this.createdAt,
    type = this.type,
    detail = this.detail?.toExternalModel(),
    deeplink = this.deeplink,
)

fun NotificationDtoT.DetailT.toExternalModel() = NotificationDto.Detail(
    courseTitle = this.courseTitle,
    lectureNumber = this.lectureNumber,
    courseNumber = this.courseNumber,
)

fun PatchThemeParamsT.toExternalModel() = PatchThemeParams(
    name = this.name,
    colors = this.colors.map { it.toExternalModel() },
)

fun PatchUserInfoParamsT.toExternalModel() = PatchUserInfoParams(
    nickname = this.nickname,
)

fun PostBookmarkParamsT.toExternalModel() = PostBookmarkParams(
    id = this.id,
)

fun PostCheckEmailByIdParamsT.toExternalModel() = PostCheckEmailByIdParams(
    id = this.id,
)

fun PostCheckEmailByIdResultsT.toExternalModel() = PostCheckEmailByIdResults(
    email = this.email,
)

fun PostCustomLectureParamsT.toExternalModel() = PostCustomLectureParams(
    id = this.id,
    classification = this.classification,
    department = this.department,
    academic_year = this.academic_year,
    course_number = this.course_number,
    lecture_number = this.lecture_number,
    course_title = this.course_title,
    credit = credit,
    class_time = this.class_time,
    class_time_json = this.class_time_json?.map { it.toExternalModel() },
    location = this.location,
    instructor = this.instructor,
    quota = this.quota,
    enrollment = this.enrollment,
    remark = this.remark,
    category = this.category,
    colorIndex = this.colorIndex,
    color = this.color?.toExternalModel(),
    isForced = this.isForced,
)

fun PostFeedbackParamsT.toExternalModel() = PostFeedbackParams(
    email = this.email,
    message = this.message,
)

fun PostFeedbackResultsT.toExternalModel() = PostFeedbackResults(
    message = this.message,
)

fun PostFindIdParamsT.toExternalModel() = PostFindIdParams(
    email = this.email,
)

fun PostFindIdResultsT.toExternalModel() = PostFindIdResults(
    message = this.message,
)

fun PostForceLogoutParamsT.toExternalModel() = PostForceLogoutParams(
    userId = this.userId,
    registrationId = this.registrationId,
)

fun PostForceLogoutResultsT.toExternalModel() = PostForceLogoutResults(
    message = this.message,
)

fun PostLectureParamsT.toExternalModel() = PostLectureParams(
    id = this.id,
)

fun PostLoginFacebookParamsT.toExternalModel() = PostLoginFacebookParams(
    facebookId = this.facebookId,
    facebookToken = this.facebookToken,
)

fun PostLoginFacebookResultsT.toExternalModel() = PostLoginFacebookResults(
    token = this.token,
    userId = this.userId,
)

fun PostResetPasswordParamsT.toExternalModel() = PostResetPasswordParams(
    id = this.id,
    password = this.password,
)

fun PostSearchQueryParamsT.toExternalModel() = PostSearchQueryParams(
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
    times = this.times?.map { it.toExternalModel() },
    timesToExclude = this.timesToExclude?.map { it.toExternalModel() },
    offset = this.offset,
    limit = this.limit,
)

fun PostSearchQueryResultsT.toExternalModel() = this.map { it.toExternalModel() }

fun PostSendCodeToEmailParamsT.toExternalModel() = PostSendCodeToEmailParams(
    email = this.email,
)

fun PostSendPwResetCodeParamsT.toExternalModel() = PostSendPwResetCodeParams(
    email = this.email,
)

fun PostSignInParamsT.toExternalModel() = PostSignInParams(
    id = this.id,
    password = this.password,
)

fun PostSignInResultsT.toExternalModel() = PostSignInResults(
    token = this.token,
    userId = this.userId,
)

fun PostSignUpParamsT.toExternalModel() = PostSignUpParams(
    id = this.id,
    password = this.password,
    email = this.email,
)

fun PostSignUpResultsT.toExternalModel() = PostSignUpResults(
    message = this.message,
    token = this.token,
    userId = this.userId,
)

fun PostTableParamsT.toExternalModel() = PostTableParams(
    year = this.year,
    semester = this.semester,
    title = this.title,
)

fun PostThemeParamsT.toExternalModel() = PostThemeParams(
    name = this.name,
    colors = this.colors.map { it.toExternalModel() },
)

fun PostUserFacebookParamsT.toExternalModel() = PostUserFacebookParams(
    facebookId = this.facebookId,
    facebookToken = this.facebookToken,
)

fun PostUserFacebookResultsT.toExternalModel() = PostUserFacebookResults(
    token = this.token,
)

fun PostUserPasswordParamsT.toExternalModel() = PostUserPasswordParams(
    id = this.id,
    password = this.password,
)

fun PostUserPasswordResultsT.toExternalModel() = PostUserPasswordResults(
    token = this.token,
)

fun PostVerifyEmailCodeParamsT.toExternalModel() = PostVerifyEmailCodeParams(
    code = this.code,
)

fun PostVerifyPwResetCodeParamsT.toExternalModel() =
    PostVerifyPwResetCodeParams(
        id = this.id,
        code = this.code,
    )

fun PutLectureParamsT.toExternalModel() = PutLectureParams(
    id = this.id,
    classification = this.classification,
    department = this.department,
    academic_year = this.academic_year,
    course_number = this.course_number,
    lecture_number = this.lecture_number,
    course_title = this.course_title,
    credit = credit,
    class_time = this.class_time,
    class_time_json = this.class_time_json?.map { it.toExternalModel() },
    location = this.location,
    instructor = this.instructor,
    quota = this.quota,
    enrollment = this.enrollment,
    remark = this.remark,
    category = this.category,
    colorIndex = this.colorIndex,
    color = this.color?.toExternalModel(),
    isForced = this.isForced,
)

fun PutTableParamsT.toExternalModel() = PutTableParams(
    title = this.title,
)

fun PutTableThemeParamsT.toExternalModel() = PutTableThemeParams(
    theme = this.theme,
    themeId = this.themeId,
)

fun PutUserPasswordParamsT.toExternalModel() = PutUserPasswordParams(
    newPassword = this.newPassword,
    oldPassword = this.oldPassword,
)

fun PutUserPasswordResultsT.toExternalModel() = PutUserPasswordResults(
    token = this.token,
)

fun RegisterFirebaseTokenParamsT.toExternalModel() = RegisterFirebaseTokenParams()

fun RegisterFirebaseTokenResultsT.toExternalModel() = RegisterFirebaseTokenResults(
    message = this.message,
)

fun RemoteConfigDtoT.toExternalModel() = RemoteConfigDto(
    reactNativeBundleSrc = this.reactNativeBundleSrc?.toExternalModel(),
    vacancyBannerConfig = this.vacancyBannerConfig.toExternalModel(),
    vacancyUrlConfig = this.vacancyUrlConfig.toExternalModel(),
    settingsBadgeConfig = this.settingsBadgeConfig.toExternalModel(),
    disableMapFeature = this.disableMapFeature,
)

fun RemoteConfigDtoT.ReactNativeBundleSrcT.toExternalModel() = RemoteConfigDto.ReactNativeBundleSrc(
    src = this.src,
)

fun RemoteConfigDtoT.SettingsBadgeConfigT.toExternalModel() = RemoteConfigDto.SettingsBadgeConfig(
    new = this.new,
)

fun RemoteConfigDtoT.VacancyBannerConfigT.toExternalModel() = RemoteConfigDto.VacancyBannerConfig(
    visible = this.visible,
)

fun RemoteConfigDtoT.VacancyUrlConfigT.toExternalModel() = RemoteConfigDto.VacancyUrlConfig(
    url = this.url,
)

fun SearchTimeDtoT.toExternalModel() = SearchTimeDto(
    day = this.day,
    startMinute = this.startMinute,
    endMinute = this.endMinute,
)

fun SimpleTableDtoT.toExternalModel() = SimpleTableDto(
    id = this.id,
    year = this.year,
    semester = this.semester,
    title = this.title,
    updatedAt = this.updatedAt,
    totalCredit = this.totalCredit,
    isPrimary = this.isPrimary,
)

fun TableDtoT.toExternalModel() = TableDto(
    id = this.id,
    year = this.year,
    semester = this.semester,
    title = this.title,
    lectureList = this.lectureList.map { it.toExternalModel() },
    updatedAt = this.updatedAt,
    totalCredit = this.totalCredit,
    theme = this.theme,
    themeId = this.themeId,
    isPrimary = this.isPrimary,
)

fun CustomThemeT.toExternalModel() = CustomTheme(
    id = this.id,
    name = this.name,
    colors = this.colors.map { it.toExternalModel() },
)

fun BuiltInThemeT.toExternalModel() = BuiltInTheme(
    code = this.code,
    name = this.name,
)

fun ThemeDtoT.toExternalModel() = ThemeDto(
    id = this.id,
    theme = this.theme,
    name = this.name,
    colors = this.colors.map { it.toExternalModel() },
    isCustom = this.isCustom,
)

fun UserDtoT.toExternalModel() = UserDto(
    isAdmin = this.isAdmin,
    regDate = this.regDate,
    notificationCheckedAt = this.notificationCheckedAt,
    email = this.email,
    localId = this.localId,
    fbName = this.fbName,
    nickname = this.nickname?.toExternalModel(),
)
