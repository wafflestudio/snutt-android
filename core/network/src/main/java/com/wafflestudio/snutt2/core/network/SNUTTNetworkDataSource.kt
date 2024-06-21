package com.wafflestudio.snutt2.core.network

import com.wafflestudio.snutt2.lib.network.dto.DeleteFirebaseTokenResults
import com.wafflestudio.snutt2.lib.network.dto.DeleteLectureResults
import com.wafflestudio.snutt2.lib.network.dto.DeleteTableResults
import com.wafflestudio.snutt2.lib.network.dto.DeleteUserAccountResults
import com.wafflestudio.snutt2.lib.network.dto.DeleteUserFacebookResults
import com.wafflestudio.snutt2.lib.network.dto.GetBookmarkListResults
import com.wafflestudio.snutt2.lib.network.dto.GetCoursebookResults
import com.wafflestudio.snutt2.lib.network.dto.GetCoursebooksOfficialResults
import com.wafflestudio.snutt2.lib.network.dto.GetLecturesIdResults
import com.wafflestudio.snutt2.lib.network.dto.GetNotificationCountResults
import com.wafflestudio.snutt2.lib.network.dto.GetNotificationResults
import com.wafflestudio.snutt2.lib.network.dto.GetPopupResults
import com.wafflestudio.snutt2.lib.network.dto.GetRecentTableResults
import com.wafflestudio.snutt2.lib.network.dto.GetRemoteConfigResponse
import com.wafflestudio.snutt2.lib.network.dto.GetTableByIdResults
import com.wafflestudio.snutt2.lib.network.dto.GetTableListResults
import com.wafflestudio.snutt2.lib.network.dto.GetTagListResults
import com.wafflestudio.snutt2.lib.network.dto.GetThemesResults
import com.wafflestudio.snutt2.lib.network.dto.GetUserFacebookResults
import com.wafflestudio.snutt2.lib.network.dto.GetUserInfoResults
import com.wafflestudio.snutt2.lib.network.dto.GetVacancyLecturesResults
import com.wafflestudio.snutt2.lib.network.dto.PatchThemeParams
import com.wafflestudio.snutt2.lib.network.dto.PatchThemeResults
import com.wafflestudio.snutt2.lib.network.dto.PatchUserInfoParams
import com.wafflestudio.snutt2.lib.network.dto.PatchUserInfoResults
import com.wafflestudio.snutt2.lib.network.dto.PostBookmarkParams
import com.wafflestudio.snutt2.lib.network.dto.PostCheckEmailByIdParams
import com.wafflestudio.snutt2.lib.network.dto.PostCheckEmailByIdResults
import com.wafflestudio.snutt2.lib.network.dto.PostCopyTableResults
import com.wafflestudio.snutt2.lib.network.dto.PostCopyThemeResults
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureResults
import com.wafflestudio.snutt2.lib.network.dto.PostFeedbackParams
import com.wafflestudio.snutt2.lib.network.dto.PostFeedbackResults
import com.wafflestudio.snutt2.lib.network.dto.PostFindIdParams
import com.wafflestudio.snutt2.lib.network.dto.PostFindIdResults
import com.wafflestudio.snutt2.lib.network.dto.PostForceLogoutParams
import com.wafflestudio.snutt2.lib.network.dto.PostForceLogoutResults
import com.wafflestudio.snutt2.lib.network.dto.PostLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PostLoginFacebookParams
import com.wafflestudio.snutt2.lib.network.dto.PostLoginFacebookResults
import com.wafflestudio.snutt2.lib.network.dto.PostResetPasswordParams
import com.wafflestudio.snutt2.lib.network.dto.PostSearchQueryParams
import com.wafflestudio.snutt2.lib.network.dto.PostSearchQueryResults
import com.wafflestudio.snutt2.lib.network.dto.PostSendCodeToEmailParams
import com.wafflestudio.snutt2.lib.network.dto.PostSendPwResetCodeParams
import com.wafflestudio.snutt2.lib.network.dto.PostSignInParams
import com.wafflestudio.snutt2.lib.network.dto.PostSignInResults
import com.wafflestudio.snutt2.lib.network.dto.PostSignUpParams
import com.wafflestudio.snutt2.lib.network.dto.PostSignUpResults
import com.wafflestudio.snutt2.lib.network.dto.PostTableParams
import com.wafflestudio.snutt2.lib.network.dto.PostTableResults
import com.wafflestudio.snutt2.lib.network.dto.PostThemeParams
import com.wafflestudio.snutt2.lib.network.dto.PostThemeResults
import com.wafflestudio.snutt2.lib.network.dto.PostUserFacebookParams
import com.wafflestudio.snutt2.lib.network.dto.PostUserFacebookResults
import com.wafflestudio.snutt2.lib.network.dto.PostUserPasswordParams
import com.wafflestudio.snutt2.lib.network.dto.PostUserPasswordResults
import com.wafflestudio.snutt2.lib.network.dto.PostVerifyEmailCodeParams
import com.wafflestudio.snutt2.lib.network.dto.PostVerifyPwResetCodeParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureResults
import com.wafflestudio.snutt2.lib.network.dto.PutTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableResults
import com.wafflestudio.snutt2.lib.network.dto.PutTableThemeParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableThemeResult
import com.wafflestudio.snutt2.lib.network.dto.PutUserPasswordParams
import com.wafflestudio.snutt2.lib.network.dto.PutUserPasswordResults
import com.wafflestudio.snutt2.lib.network.dto.RegisterFirebaseTokenParams
import com.wafflestudio.snutt2.lib.network.dto.RegisterFirebaseTokenResults
import com.wafflestudio.snutt2.lib.network.dto.ResetLectureResults
import com.wafflestudio.snutt2.lib.network.dto.core.BuildingsResponse

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