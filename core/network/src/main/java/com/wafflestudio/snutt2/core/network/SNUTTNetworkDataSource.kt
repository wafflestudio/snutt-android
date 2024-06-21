package com.wafflestudio.snutt2.core.network

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
import com.wafflestudio.snutt2.core.network.model.PostLoginFacebookParams
import com.wafflestudio.snutt2.core.network.model.PostLoginFacebookResults
import com.wafflestudio.snutt2.core.network.model.PostResetPasswordParams
import com.wafflestudio.snutt2.core.network.model.PostSearchQueryParams
import com.wafflestudio.snutt2.core.network.model.PostSearchQueryResults
import com.wafflestudio.snutt2.core.network.model.PostSendCodeToEmailParams
import com.wafflestudio.snutt2.core.network.model.PostSendPwResetCodeParams
import com.wafflestudio.snutt2.core.network.model.PostSignInParams
import com.wafflestudio.snutt2.core.network.model.PostSignInResults
import com.wafflestudio.snutt2.core.network.model.PostSignUpParams
import com.wafflestudio.snutt2.core.network.model.PostSignUpResults
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
        body: PostLoginFacebookParams,
    ): PostLoginFacebookResults

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