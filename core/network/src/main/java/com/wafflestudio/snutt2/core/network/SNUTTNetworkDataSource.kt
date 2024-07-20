package com.wafflestudio.snutt2.core.network

import com.wafflestudio.snutt2.core.network.model.*
import retrofit2.http.Body

// TODO : 아직은 원래거를 쓰고 있고 얘는 그냥 시도

interface SNUTTNetworkDataSource {
    suspend fun _getNotification(
        limit: Int,
        offset: Int,
        explicit: Int,
    ): GetNotificationResults

    suspend fun _getNotificationCount(): GetNotificationCountResults

    suspend fun _getTagList(
        year: Int,
        semester: Int,
    ): GetTagListResults

    suspend fun _postSearchQuery(
        body: PostSearchQueryParams,
    ): PostSearchQueryResults

    suspend fun _getCoursebook(): GetCoursebookResults

    suspend fun _getTableList(): GetTableListResults

    suspend fun _postTable(
        body: PostTableParams,
    ): PostTableResults

    suspend fun _getTableById(
        id: String,
    ): GetTableByIdResults

    suspend fun _getRecentTable(): GetRecentTableResults

    suspend fun _deleteTable(
        id: String,
    ): DeleteTableResults

    suspend fun _putTable(
        id: String,
        body: PutTableParams,
    ): PutTableResults

    suspend fun _putTableTheme(
        id: String,
        body: PutTableThemeParams,
    ): PutTableThemeResult

    suspend fun _copyTable(
        id: String,
    ): PostCopyTableResults

    suspend fun _postCustomLecture(
        id: String,
        body: PostCustomLectureParams,
    ): PostCustomLectureResults

    suspend fun _postAddLecture(
        id: String,
        lecture_id: String,
        is_forced: PostLectureParams,
    ): PostCustomLectureResults

    suspend fun _deleteLecture(
        id: String,
        lecture_id: String,
    ): DeleteLectureResults

    suspend fun _putLecture(
        id: String,
        lecture_id: String,
        body: PutLectureParams,
    ): PutLectureResults

    suspend fun _resetLecture(
        id: String,
        lecture_id: String,
    ): ResetLectureResults

    suspend fun _getCoursebooksOfficial(
        year: Long,
        semester: Long,
        courseNumber: String,
        lectureNumber: String,
    ): GetCoursebooksOfficialResults

    suspend fun _postSignUp(
        body: PostSignUpParams,
    ): PostSignUpResults

    suspend fun _postSignIn(
        body: PostSignInParams,
    ): PostSignInResults

    suspend fun _postLoginFacebook(
        @Body body: PostSocialLoginParams,
    ): PostSocialLoginResults

    suspend fun _postLoginGoogle(
        @Body body: PostSocialLoginParams,
    ): PostSocialLoginResults

    suspend fun _postLoginKakao(
        @Body body: PostSocialLoginParams,
    ): PostSocialLoginResults

    suspend fun _postForceLogout(
        body: PostForceLogoutParams,
    ): PostForceLogoutResults

    suspend fun _postFindId(
        body: PostFindIdParams,
    ): PostFindIdResults

    suspend fun _postCheckEmailById(
        body: PostCheckEmailByIdParams,
    ): PostCheckEmailByIdResults

    suspend fun _postSendPwResetCodeToEmailById(
        body: PostSendPwResetCodeParams,
    )

    suspend fun _postVerifyCodeToResetPassword(
        body: PostVerifyPwResetCodeParams,
    )

    suspend fun _postResetPassword(
        body: PostResetPasswordParams,
    )

    suspend fun _postSendCodeToEmail(
        body: PostSendCodeToEmailParams,
    )

    suspend fun _postVerifyEmailCode(
        body: PostVerifyEmailCodeParams,
    )

    suspend fun _getUserInfo(): GetUserInfoResults

    suspend fun _patchUserInfo(
        body: PatchUserInfoParams,
    ): PatchUserInfoResults

    suspend fun _putUserPassword(
        body: PutUserPasswordParams,
    ): PutUserPasswordResults

    suspend fun _postUserPassword(
        body: PostUserPasswordParams,
    ): PostUserPasswordResults

    suspend fun _postUserFacebook(
        body: PostUserFacebookParams,
    ): PostUserFacebookResults

    suspend fun _deleteUserFacebook(): DeleteUserFacebookResults

    suspend fun _getUserFacebook(): GetUserFacebookResults

    suspend fun _registerFirebaseToken(
        id: String,
        body: RegisterFirebaseTokenParams,
    ): RegisterFirebaseTokenResults

    suspend fun _deleteFirebaseToken(
        id: String,
    ): DeleteFirebaseTokenResults

    suspend fun _deleteUserAccount(): DeleteUserAccountResults

    suspend fun _postFeedback(
        body: PostFeedbackParams,
    ): PostFeedbackResults

    suspend fun _getLecturesId(
        courseNumber: String,
        instructor: String,
    ): GetLecturesIdResults

    suspend fun _getPopup(): GetPopupResults

    suspend fun _getBookmarkList(
        year: Long,
        semester: Long,
    ): GetBookmarkListResults

    suspend fun _addBookmark(
        body: PostBookmarkParams,
    )

    suspend fun _deleteBookmark(
        body: PostBookmarkParams,
    )

    suspend fun _getVacancyLectures(): GetVacancyLecturesResults

    suspend fun _postVacancyLecture(
        lectureId: String,
    )

    suspend fun _deleteVacancyLecture(
        lectureId: String,
    )

    suspend fun _getRemoteConfig(): GetRemoteConfigResponse

    suspend fun _postPrimaryTable(
        tableId: String,
    )

    suspend fun _deletePrimaryTable(
        tableId: String,
    )

    suspend fun _getThemes(): GetThemesResults

    suspend fun _postTheme(
        body: PostThemeParams,
    ): PostThemeResults

    suspend fun _deleteTheme(
        themeId: String,
    )

    suspend fun _patchTheme(
        themeId: String,
        patchThemeParams: PatchThemeParams,
    ): PatchThemeResults

    suspend fun _postCopyTheme(
        themeId: String,
    ): PostCopyThemeResults

    suspend fun _getBuildings(
        places: String,
    ): BuildingsResponse
}
