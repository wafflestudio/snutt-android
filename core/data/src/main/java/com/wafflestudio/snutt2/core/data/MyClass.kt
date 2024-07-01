package com.wafflestudio.snutt2.core.data

import com.wafflestudio.snutt2.core.network.model.*
import com.wafflestudio.snutt2.core.data.model.*

fun BuildingsResponse.toTempModel() = BuildingsResponseT(
    content = this.content.map { it.toTempModel() },
    totalCount = this.totalCount,
)

fun Campus.toTempModel(): CampusT {
    return when (this){
        Campus.GWANAK -> CampusT.GWANAK
        Campus.YEONGEON -> CampusT.YEONGEON
        Campus.PYEONGCHANG -> CampusT.PYEONGCHANG
    }
}

fun ClassTimeDto.toTempModel() = ClassTimeDtoT(
    day = this.day,
    place = this.place,
    id = this.id,
    startMinute = this.startMinute,
    endMinute = this.endMinute,
)

fun ColorDto.toTempModel() = ColorDtoT(
    fgRaw = this.fgRaw,
    bgRaw = this.bgRaw,
)

fun CourseBookDto.toTempModel() = CourseBookDtoT(
    semester = this.semester,
    year = this.year,
)

fun DeleteFirebaseTokenResults.toTempModel() =
    DeleteFirebaseTokenResultsT(
        message = this.message,
    )

fun DeleteTableResults.toTempModel() = this.map { it.toTempModel() }

fun DeleteUserAccountResults.toTempModel() =
    DeleteUserAccountResultsT(
        message = this.message,
    )

fun DeleteUserFacebookResults.toTempModel() =
    DeleteUserFacebookResultsT(
        token = this.token,
    )

fun ErrorDTO.toTempModel() = ErrorDTOT(
    code = this.code,
    message = this.message,
    displayMessage = this.displayMessage,
    ext = this.ext,
)

fun GeoCoordinate.toTempModel() = GeoCoordinateT(
    latitude = this.latitude,
    longitude = this.longitude,
)

fun GetBookmarkListResults.toTempModel() =
    GetBookmarkListResultsT(
        year = this.year,
        semester = this.semester,
        lectures = this.lectures.map { it.toTempModel() },
    )

fun GetCoursebooksOfficialResults.toTempModel() =
    GetCoursebooksOfficialResultsT(
        url = this.url,
    )

fun GetLecturesIdResults.toTempModel() =
    GetLecturesIdResultsT(
        id = this.id,
    )

fun GetNotificationCountResults.toTempModel() =
    GetNotificationCountResultsT(
        count = this.count,
    )

fun GetNotificationResults.toTempModel() = this.map { it.toTempModel() }

fun GetPopupResults.toTempModel() = GetPopupResultsT(
    popups = this.popups.map { it.toTempModel() },
)

fun GetPopupResults.Popup.toTempModel() = GetPopupResultsT.PopupT(
    key = this.key,
    uri = this.uri,
    popupHideDays = popupHideDays,
)

fun GetTagListResults.toTempModel() = GetTagListResultsT(
    classification = this.classification,
    department = this.department,
    academicYear = this.academicYear,
    credit = this.credit,
    instructor = this.instructor,
    category = this.category,
)

fun GetThemesResults.toTempModel() = this.map { it.toTempModel() }

fun GetUserFacebookResults.toTempModel() =
    GetUserFacebookResultsT(
        name = this.name,
        attached = this.attached,
    )

fun LectureBuildingDto.toTempModel() = LectureBuildingDtoT(
    id = this.id,
    buildingNumber = this.buildingNumber,
    buildingNameKor = this.buildingNameKor,
    buildingNameEng = this.buildingNameEng,
    locationInDMS = this.locationInDMS.toTempModel(),
    locationInDecimal = this.locationInDecimal.toTempModel(),
    campus = this.campus.toTempModel(),
)

fun LectureDto.toTempModel() = LectureDtoT(
    id = this.id,
    lecture_id = this.lecture_id,
    classification = this.classification,
    department = this.department,
    academic_year = this.academic_year,
    course_number = this.course_number,
    lecture_number = this.lecture_number,
    course_title = this.course_title,
    credit = this.credit,
    class_time_json = this.class_time_json.map { it.toTempModel() },
    instructor = this.instructor,
    quota = this.quota,
    freshmanQuota = this.freshmanQuota,
    remark = this.remark,
    category = this.category,
    colorIndex = this.colorIndex,
    color = this.color.toTempModel(),
    registrationCount = this.registrationCount,
    wasFull = this.wasFull,
)

fun NicknameDto.toTempModel() = NicknameDtoT(
    nickname = this.nickname,
    tag = this.tag,
)

fun NotificationDto.toTempModel() = NotificationDtoT(
    id = this.id,
    title = this.title,
    message = this.message,
    createdAt = this.createdAt,
    type = this.type,
    detail = this.detail?.toTempModel(),
    deeplink = this.deeplink,
)

fun NotificationDto.Detail.toTempModel() = NotificationDtoT.DetailT(
    courseTitle = this.courseTitle,
    lectureNumber = this.lectureNumber,
    courseNumber = this.courseNumber,
)

fun PatchThemeParams.toTempModel() = PatchThemeParamsT(
    name = this.name,
    colors = this.colors.map { it.toTempModel() },
)

fun PatchUserInfoParams.toTempModel() =
    PatchUserInfoParamsT(
        nickname = this.nickname,
    )

fun PostBookmarkParams.toTempModel() = PostBookmarkParamsT(
    id = this.id,
)

fun PostCheckEmailByIdParams.toTempModel() =
    PostCheckEmailByIdParamsT(
        id = this.id,
    )

fun PostCheckEmailByIdResults.toTempModel() =
    PostCheckEmailByIdResultsT(
        email = this.email,
    )

fun PostCustomLectureParams.toTempModel() =
    PostCustomLectureParamsT(
        id = this.id,
        classification = this.classification,
        department = this.department,
        academic_year = this.academic_year,
        course_number = this.course_number,
        lecture_number = this.lecture_number,
        course_title = this.course_title,
        credit = credit,
        class_time = this.class_time,
        class_time_json = this.class_time_json?.map { it.toTempModel() },
        location = this.location,
        instructor = this.instructor,
        quota = this.quota,
        enrollment = this.enrollment,
        remark = this.remark,
        category = this.category,
        colorIndex = this.colorIndex,
        color = this.color?.toTempModel(),
        isForced = this.isForced,
    )

fun PostFeedbackParams.toTempModel() = PostFeedbackParamsT(
    email = this.email,
    message = this.message,
)

fun PostFeedbackResults.toTempModel() =
    PostFeedbackResultsT(
        message = this.message,
    )

fun PostFindIdParams.toTempModel() = PostFindIdParamsT(
    email = this.email,
)

fun PostFindIdResults.toTempModel() = PostFindIdResultsT(
    message = this.message,
)

fun PostForceLogoutParams.toTempModel() =
    PostForceLogoutParamsT(
        userId = this.userId,
        registrationId = this.registrationId,
    )

fun PostForceLogoutResults.toTempModel() =
    PostForceLogoutResultsT(
        message = this.message,
    )

fun PostLectureParams.toTempModel() = PostLectureParamsT(
    id = this.id,
)

fun PostLoginFacebookParams.toTempModel() =
    PostLoginFacebookParamsT(
        facebookId = this.facebookId,
        facebookToken = this.facebookToken,
    )

fun PostLoginFacebookResults.toTempModel() =
    PostLoginFacebookResultsT(
        token = this.token,
        userId = this.userId,
    )

fun PostResetPasswordParams.toTempModel() =
    PostResetPasswordParamsT(
        id = this.id,
        password = this.password,
    )

fun PostSearchQueryParams.toTempModel() =
    PostSearchQueryParamsT(
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
        times = this.times?.map { it.toTempModel() },
        timesToExclude = this.timesToExclude?.map { it.toTempModel() },
        offset = this.offset,
        limit = this.limit,
    )

fun PostSendCodeToEmailParams.toTempModel() =
    PostSendCodeToEmailParamsT(
        email = this.email,
    )

fun PostSendPwResetCodeParams.toTempModel() =
    PostSendPwResetCodeParamsT(
        email = this.email,
    )

fun PostSignInParams.toTempModel() = PostSignInParamsT(
    id = this.id,
    password = this.password,
)

fun PostSignInResults.toTempModel() = PostSignInResultsT(
    token = this.token,
    userId = this.userId,
)

fun PostSignUpParams.toTempModel() = PostSignUpParamsT(
    id = this.id,
    password = this.password,
    email = this.email,
)

fun PostSignUpResults.toTempModel() = PostSignUpResultsT(
    message = this.message,
    token = this.token,
    userId = this.userId,
)

fun PostTableParams.toTempModel() = PostTableParamsT(
    year = this.year,
    semester = this.semester,
    title = this.title,
)

fun PostThemeParams.toTempModel() = PostThemeParamsT(
    name = this.name,
    colors = this.colors.map { it.toTempModel() },
)

fun PostUserFacebookParams.toTempModel() =
    PostUserFacebookParamsT(
        facebookId = this.facebookId,
        facebookToken = this.facebookToken,
    )

fun PostUserFacebookResults.toTempModel() =
    PostUserFacebookResultsT(
        token = this.token,
    )

fun PostUserPasswordParams.toTempModel() =
    PostUserPasswordParamsT(
        id = this.id,
        password = this.password,
    )

fun PostUserPasswordResults.toTempModel() =
    PostUserPasswordResultsT(
        token = this.token,
    )

fun PostVerifyEmailCodeParams.toTempModel() =
    PostVerifyEmailCodeParamsT(
        code = this.code,
    )

fun PostVerifyPwResetCodeParams.toTempModel() =
    PostVerifyPwResetCodeParamsT(
        id = this.id,
        code = this.code,
    )

fun PutLectureParams.toTempModel() = PutLectureParamsT(
    id = this.id,
    classification = this.classification,
    department = this.department,
    academic_year = this.academic_year,
    course_number = this.course_number,
    lecture_number = this.lecture_number,
    course_title = this.course_title,
    credit = credit,
    class_time = this.class_time,
    class_time_json = this.class_time_json?.map { it.toTempModel() },
    location = this.location,
    instructor = this.instructor,
    quota = this.quota,
    enrollment = this.enrollment,
    remark = this.remark,
    category = this.category,
    colorIndex = this.colorIndex,
    color = this.color?.toTempModel(),
    isForced = this.isForced,
)

fun PutTableParams.toTempModel() = PutTableParamsT(
    title = this.title,
)

fun PutTableThemeParams.toTempModel() =
    PutTableThemeParamsT(
        theme = this.theme,
        themeId = this.themeId,
    )

fun PutUserPasswordParams.toTempModel() =
    PutUserPasswordParamsT(
        newPassword = this.newPassword,
        oldPassword = this.oldPassword,
    )

fun PutUserPasswordResults.toTempModel() =
    PutUserPasswordResultsT(
        token = this.token,
    )

fun RegisterFirebaseTokenParams.toTempModel() =
    RegisterFirebaseTokenParamsT()

fun RegisterFirebaseTokenResults.toTempModel() =
    RegisterFirebaseTokenResultsT(
        message = this.message,
    )

fun RemoteConfigDto.toTempModel() = RemoteConfigDtoT(
    reactNativeBundleSrc = this.reactNativeBundleSrc?.toTempModel(),
    vacancyBannerConfig = this.vacancyBannerConfig.toTempModel(),
    vacancyUrlConfig = this.vacancyUrlConfig.toTempModel(),
    settingsBadgeConfig = this.settingsBadgeConfig.toTempModel(),
    disableMapFeature = this.disableMapFeature,
)

fun RemoteConfigDto.ReactNativeBundleSrc.toTempModel() = RemoteConfigDtoT.ReactNativeBundleSrcT(
    src = this.src,
)

fun RemoteConfigDto.SettingsBadgeConfig.toTempModel() = RemoteConfigDtoT.SettingsBadgeConfigT(
    new = this.new,
)

fun RemoteConfigDto.VacancyBannerConfig.toTempModel() = RemoteConfigDtoT.VacancyBannerConfigT(
    visible = this.visible,
)

fun RemoteConfigDto.VacancyUrlConfig.toTempModel() = RemoteConfigDtoT.VacancyUrlConfigT(
    url = this.url,
)

fun SearchTimeDto.toTempModel() = SearchTimeDtoT(
    day = this.day,
    startMinute = this.startMinute,
    endMinute = this.endMinute,
)

fun SimpleTableDto.toTempModel() = SimpleTableDtoT(
    id = this.id,
    year = this.year,
    semester = this.semester,
    title = this.title,
    updatedAt = this.updatedAt,
    totalCredit = this.totalCredit,
    isPrimary = this.isPrimary,
)

fun TableDto.toTempModel() = TableDtoT(
    id = this.id,
    year = this.year,
    semester = this.semester,
    title = this.title,
    lectureList = this.lectureList.map { it.toTempModel() },
    updatedAt = this.updatedAt,
    totalCredit = this.totalCredit,
    theme = this.theme,
    themeId = this.themeId,
    isPrimary = this.isPrimary,
)

fun CustomTheme.toTempModel() = CustomThemeT(
    id = this.id,
    name = this.name,
    colors = this.colors.map { it.toTempModel() },
)

fun BuiltInTheme.toTempModel() = BuiltInThemeT(
    code = this.code,
    name = this.name,
)

fun ThemeDto.toTempModel() = ThemeDtoT(
    id = this.id,
    theme = this.theme,
    name = this.name,
    colors = this.colors.map { it.toTempModel() },
    isCustom = this.isCustom,
)

fun UserDto.toTempModel() = UserDtoT(
    isAdmin = this.isAdmin,
    regDate = this.regDate,
    notificationCheckedAt = this.notificationCheckedAt,
    email = this.email,
    localId = this.localId,
    fbName = this.fbName,
    nickname = this.nickname?.toTempModel(),
)