package com.wafflestudio.snutt2.core.network.retrofit

import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.core.network.model.BuildingsResponse
import com.wafflestudio.snutt2.core.network.model.DeleteFirebaseTokenResults
import com.wafflestudio.snutt2.core.network.model.DeleteLectureResults
import com.wafflestudio.snutt2.core.network.model.DeleteTableResults
import com.wafflestudio.snutt2.core.network.model.DeleteUserAccountResults
import com.wafflestudio.snutt2.core.network.model.DeleteUserFacebookResults
import com.wafflestudio.snutt2.core.network.model.GetBookmarkListResults
import com.wafflestudio.snutt2.core.network.model.GetCoursebookResults
import com.wafflestudio.snutt2.core.network.model.GetCoursebooksOfficialResults
import com.wafflestudio.snutt2.core.network.model.GetLecturesIdResults
import com.wafflestudio.snutt2.core.network.model.GetNotificationCountResults
import com.wafflestudio.snutt2.core.network.model.GetNotificationResults
import com.wafflestudio.snutt2.core.network.model.GetPopupResults
import com.wafflestudio.snutt2.core.network.model.GetRecentTableResults
import com.wafflestudio.snutt2.core.network.model.GetRemoteConfigResponse
import com.wafflestudio.snutt2.core.network.model.GetTableByIdResults
import com.wafflestudio.snutt2.core.network.model.GetTableListResults
import com.wafflestudio.snutt2.core.network.model.GetTagListResults
import com.wafflestudio.snutt2.core.network.model.GetThemesResults
import com.wafflestudio.snutt2.core.network.model.GetUserFacebookResults
import com.wafflestudio.snutt2.core.network.model.GetUserInfoResults
import com.wafflestudio.snutt2.core.network.model.GetVacancyLecturesResults
import com.wafflestudio.snutt2.core.network.model.PatchThemeParams
import com.wafflestudio.snutt2.core.network.model.PatchThemeResults
import com.wafflestudio.snutt2.core.network.model.PatchUserInfoParams
import com.wafflestudio.snutt2.core.network.model.PatchUserInfoResults
import com.wafflestudio.snutt2.core.network.model.PostBookmarkParams
import com.wafflestudio.snutt2.core.network.model.PostCheckEmailByIdParams
import com.wafflestudio.snutt2.core.network.model.PostCheckEmailByIdResults
import com.wafflestudio.snutt2.core.network.model.PostCopyTableResults
import com.wafflestudio.snutt2.core.network.model.PostCopyThemeResults
import com.wafflestudio.snutt2.core.network.model.PostCustomLectureParams
import com.wafflestudio.snutt2.core.network.model.PostCustomLectureResults
import com.wafflestudio.snutt2.core.network.model.PostFeedbackParams
import com.wafflestudio.snutt2.core.network.model.PostFeedbackResults
import com.wafflestudio.snutt2.core.network.model.PostFindIdParams
import com.wafflestudio.snutt2.core.network.model.PostFindIdResults
import com.wafflestudio.snutt2.core.network.model.PostForceLogoutParams
import com.wafflestudio.snutt2.core.network.model.PostForceLogoutResults
import com.wafflestudio.snutt2.core.network.model.PostLectureParams
import com.wafflestudio.snutt2.core.network.model.PostResetPasswordParams
import com.wafflestudio.snutt2.core.network.model.PostSearchQueryParams
import com.wafflestudio.snutt2.core.network.model.PostSearchQueryResults
import com.wafflestudio.snutt2.core.network.model.PostSendCodeToEmailParams
import com.wafflestudio.snutt2.core.network.model.PostSendPwResetCodeParams
import com.wafflestudio.snutt2.core.network.model.PostSignInParams
import com.wafflestudio.snutt2.core.network.model.PostSignInResults
import com.wafflestudio.snutt2.core.network.model.PostSignUpParams
import com.wafflestudio.snutt2.core.network.model.PostSignUpResults
import com.wafflestudio.snutt2.core.network.model.PostSocialLoginParams
import com.wafflestudio.snutt2.core.network.model.PostSocialLoginResults
import com.wafflestudio.snutt2.core.network.model.PostTableParams
import com.wafflestudio.snutt2.core.network.model.PostTableResults
import com.wafflestudio.snutt2.core.network.model.PostThemeParams
import com.wafflestudio.snutt2.core.network.model.PostThemeResults
import com.wafflestudio.snutt2.core.network.model.PostUserFacebookParams
import com.wafflestudio.snutt2.core.network.model.PostUserFacebookResults
import com.wafflestudio.snutt2.core.network.model.PostUserPasswordParams
import com.wafflestudio.snutt2.core.network.model.PostUserPasswordResults
import com.wafflestudio.snutt2.core.network.model.PostVerifyEmailCodeParams
import com.wafflestudio.snutt2.core.network.model.PostVerifyPwResetCodeParams
import com.wafflestudio.snutt2.core.network.model.PutLectureParams
import com.wafflestudio.snutt2.core.network.model.PutLectureResults
import com.wafflestudio.snutt2.core.network.model.PutTableParams
import com.wafflestudio.snutt2.core.network.model.PutTableResults
import com.wafflestudio.snutt2.core.network.model.PutTableThemeParams
import com.wafflestudio.snutt2.core.network.model.PutTableThemeResult
import com.wafflestudio.snutt2.core.network.model.PutUserPasswordParams
import com.wafflestudio.snutt2.core.network.model.PutUserPasswordResults
import com.wafflestudio.snutt2.core.network.model.RegisterFirebaseTokenParams
import com.wafflestudio.snutt2.core.network.model.RegisterFirebaseTokenResults
import com.wafflestudio.snutt2.core.network.model.ResetLectureResults
import com.wafflestudio.snutt2.core.qualifiers.CoreNetwork
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitSNUTTNetwork @Inject constructor(
    @CoreNetwork private val networkApi: RetrofitSNUTTNetworkApi,
) : SNUTTNetworkDataSource {
    override suspend fun _getNotification(
        limit: Int,
        offset: Int,
        explicit: Int,
    ): GetNotificationResults =
        networkApi._getNotification(limit = limit, offset = offset, explicit = explicit)

    override suspend fun _getNotificationCount(): GetNotificationCountResults =
        networkApi._getNotificationCount()

    override suspend fun _getTagList(
        year: Int,
        semester: Int,
    ): GetTagListResults =
        networkApi._getTagList(year = year, semester = semester)

    override suspend fun _postSearchQuery(
        body: PostSearchQueryParams,
    ): PostSearchQueryResults =
        networkApi._postSearchQuery(body = body)

    override suspend fun _getCoursebook(): GetCoursebookResults {
        return networkApi._getCoursebook()
    }

    override suspend fun _getTableList(): GetTableListResults =
        networkApi._getTableList()

    override suspend fun _postTable(
        body: PostTableParams,
    ): PostTableResults =
        networkApi._postTable(body = body)

    override suspend fun _getTableById(
        id: String,
    ): GetTableByIdResults =
        networkApi._getTableById(id = id)

    override suspend fun _getRecentTable(): GetRecentTableResults =
        networkApi._getRecentTable()

    override suspend fun _deleteTable(
        id: String,
    ): DeleteTableResults =
        networkApi._deleteTable(id = id)

    override suspend fun _putTable(
        id: String,
        body: PutTableParams,
    ): PutTableResults =
        networkApi._putTable(id = id, body = body)

    override suspend fun _putTableTheme(
        id: String,
        body: PutTableThemeParams,
    ): PutTableThemeResult =
        networkApi._putTableTheme(id = id, body = body)

    override suspend fun _copyTable(
        id: String,
    ): PostCopyTableResults =
        networkApi._copyTable(id = id)

    override suspend fun _postCustomLecture(
        id: String,
        body: PostCustomLectureParams,
    ): PostCustomLectureResults =
        networkApi._postCustomLecture(id = id, body = body)

    override suspend fun _postAddLecture(
        id: String,
        lecture_id: String,
        is_forced: PostLectureParams,
    ): PostCustomLectureResults =
        networkApi._postAddLecture(id = id, lecture_id = lecture_id, is_forced = is_forced)

    override suspend fun _deleteLecture(
        id: String,
        lecture_id: String,
    ): DeleteLectureResults =
        networkApi._deleteLecture(id = id, lecture_id = lecture_id)

    override suspend fun _putLecture(
        id: String,
        lecture_id: String,
        body: PutLectureParams,
    ): PutLectureResults =
        networkApi._putLecture(id = id, lecture_id = lecture_id, body = body)

    override suspend fun _resetLecture(
        id: String,
        lecture_id: String,
    ): ResetLectureResults =
        networkApi._resetLecture(id = id, lecture_id = lecture_id)

    override suspend fun _getCoursebooksOfficial(
        year: Long,
        semester: Long,
        courseNumber: String,
        lectureNumber: String,
    ): GetCoursebooksOfficialResults =
        networkApi._getCoursebooksOfficial(
            year = year,
            semester = semester,
            courseNumber = courseNumber,
            lectureNumber = lectureNumber,
        )

    override suspend fun _postSignUp(
        body: PostSignUpParams,
    ): PostSignUpResults =
        networkApi._postSignUp(body = body)

    override suspend fun _postSignIn(
        body: PostSignInParams,
    ): PostSignInResults =
        networkApi._postSignIn(body = body)

    override suspend fun _postLoginFacebook(
        body: PostSocialLoginParams,
    ): PostSocialLoginResults =
        networkApi._postLoginFacebook(body = body)

    override suspend fun _postLoginKakao(body: PostSocialLoginParams): PostSocialLoginResults =
        networkApi._postLoginKakao(body = body)

    override suspend fun _postLoginGoogle(
        body: PostSocialLoginParams,
    ): PostSocialLoginResults =
        networkApi._postLoginGoogle(body = body)

    override suspend fun _postForceLogout(
        body: PostForceLogoutParams,
    ): PostForceLogoutResults =
        networkApi._postForceLogout(body = body)

    override suspend fun _postFindId(
        body: PostFindIdParams,
    ): PostFindIdResults =
        networkApi._postFindId(body = body)

    override suspend fun _postCheckEmailById(
        body: PostCheckEmailByIdParams,
    ): PostCheckEmailByIdResults =
        networkApi._postCheckEmailById(body = body)

    override suspend fun _postSendPwResetCodeToEmailById(
        body: PostSendPwResetCodeParams,
    ) =
        networkApi._postSendPwResetCodeToEmailById(body = body)

    override suspend fun _postVerifyCodeToResetPassword(
        body: PostVerifyPwResetCodeParams,
    ) =
        networkApi._postVerifyCodeToResetPassword(body = body)

    override suspend fun _postResetPassword(
        body: PostResetPasswordParams,
    ) =
        networkApi._postResetPassword(body = body)

    override suspend fun _postSendCodeToEmail(
        body: PostSendCodeToEmailParams,
    ) =
        networkApi._postSendCodeToEmail(body = body)

    override suspend fun _postVerifyEmailCode(
        body: PostVerifyEmailCodeParams,
    ) =
        networkApi._postVerifyEmailCode(body = body)

    override suspend fun _getUserInfo(): GetUserInfoResults =
        networkApi._getUserInfo()

    override suspend fun _patchUserInfo(
        body: PatchUserInfoParams,
    ): PatchUserInfoResults =
        networkApi._patchUserInfo(body = body)

    override suspend fun _putUserPassword(
        body: PutUserPasswordParams,
    ): PutUserPasswordResults =
        networkApi._putUserPassword(body = body)

    override suspend fun _postUserPassword(
        body: PostUserPasswordParams,
    ): PostUserPasswordResults =
        networkApi._postUserPassword(body = body)

    override suspend fun _postUserFacebook(
        body: PostUserFacebookParams,
    ): PostUserFacebookResults =
        networkApi._postUserFacebook(body = body)

    override suspend fun _deleteUserFacebook(): DeleteUserFacebookResults =
        networkApi._deleteUserFacebook()

    override suspend fun _getUserFacebook(): GetUserFacebookResults =
        networkApi._getUserFacebook()

    override suspend fun _registerFirebaseToken(
        id: String,
        body: RegisterFirebaseTokenParams,
    ): RegisterFirebaseTokenResults =
        networkApi._registerFirebaseToken(id = id, body = body)

    override suspend fun _deleteFirebaseToken(
        id: String,
    ): DeleteFirebaseTokenResults =
        networkApi._deleteFirebaseToken(id = id)

    override suspend fun _deleteUserAccount(): DeleteUserAccountResults =
        networkApi._deleteUserAccount()

    override suspend fun _postFeedback(
        body: PostFeedbackParams,
    ): PostFeedbackResults =
        networkApi._postFeedback(body = body)

    override suspend fun _getLecturesId(
        courseNumber: String,
        instructor: String,
    ): GetLecturesIdResults =
        networkApi._getLecturesId(courseNumber = courseNumber, instructor = instructor)

    override suspend fun _getPopup(): GetPopupResults =
        networkApi._getPopup()

    override suspend fun _getBookmarkList(
        year: Long,
        semester: Long,
    ): GetBookmarkListResults =
        networkApi._getBookmarkList(year = year, semester = semester)

    override suspend fun _addBookmark(
        body: PostBookmarkParams,
    ) =
        networkApi._addBookmark(body = body)

    override suspend fun _deleteBookmark(
        body: PostBookmarkParams,
    ) =
        networkApi._deleteBookmark(body = body)

    override suspend fun _getVacancyLectures(): GetVacancyLecturesResults =
        networkApi._getVacancyLectures()

    override suspend fun _postVacancyLecture(
        lectureId: String,
    ) =
        networkApi._postVacancyLecture(lectureId = lectureId)

    override suspend fun _deleteVacancyLecture(
        lectureId: String,
    ) =
        networkApi._deleteVacancyLecture(lectureId = lectureId)

    override suspend fun _getRemoteConfig(): GetRemoteConfigResponse =
        networkApi._getRemoteConfig()

    override suspend fun _postPrimaryTable(
        tableId: String,
    ) =
        networkApi._postPrimaryTable(tableId = tableId)

    override suspend fun _deletePrimaryTable(
        tableId: String,
    ) =
        networkApi._deletePrimaryTable(tableId = tableId)

    override suspend fun _getThemes(): GetThemesResults =
        networkApi._getThemes()

    override suspend fun _postTheme(
        body: PostThemeParams,
    ): PostThemeResults =
        networkApi._postTheme(body = body)

    override suspend fun _deleteTheme(
        themeId: String,
    ) =
        networkApi._deleteTheme(themeId = themeId)

    override suspend fun _patchTheme(
        themeId: String,
        patchThemeParams: PatchThemeParams,
    ): PatchThemeResults =
        networkApi._patchTheme(themeId = themeId, patchThemeParams = patchThemeParams)

    override suspend fun _postCopyTheme(
        themeId: String,
    ): PostCopyThemeResults =
        networkApi._postCopyTheme(themeId = themeId)

    override suspend fun _getBuildings(
        places: String,
    ): BuildingsResponse =
        networkApi._getBuildings(places = places)
}
