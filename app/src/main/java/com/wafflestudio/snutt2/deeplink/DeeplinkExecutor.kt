package com.wafflestudio.snutt2.deeplink

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils.semesterStringToLong
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalHomePageController
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType
import com.wafflestudio.snutt2.views.navigateAsOrigin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DeeplinkExecutor {
    val deeplinkUri = mutableStateOf(Uri.EMPTY)
    fun execute(deeplink: String?) {
        deeplink?.let {
            deeplinkUri.value = Uri.parse(it)
        }
    }
}

@Composable
fun InstallInAppDeeplinkExecutor() {
    val deeplinkUri by DeeplinkExecutor.deeplinkUri
    if (deeplinkUri == Uri.EMPTY) return

    val navController = LocalNavController.current
    val homePageController = LocalHomePageController.current
    val context = LocalContext.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val homePageBackStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavigationDestination.Home)
    }
    val homePageLectureDetailViewModel =
        hiltViewModel<LectureDetailViewModel>(homePageBackStackEntry)
    val homePageTableListViewModel = hiltViewModel<TableListViewModel>(homePageBackStackEntry)
    val searchViewModel = hiltViewModel<SearchViewModel>()

    // deeplink host별 처리 로직들
    suspend fun handleTimetableLectureDeeplink() {
        val timetableId = deeplinkUri.getQueryParameter("timetableId") ?: return
        val lectureId = deeplinkUri.getQueryParameter("lectureId") ?: return

        val lectureToShow = run {
            val table = try {
                homePageTableListViewModel.searchTableById(timetableId)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    context.toast(context.getString(R.string.deeplink_page_timetable_lecture_page_not_existing_table))
                }
                return
            }
            table.lectureList.find {
                it.lecture_id == lectureId
            } ?: run {
                withContext(Dispatchers.Main) {
                    context.toast(context.getString(R.string.deeplink_page_timetable_lecture_page_not_existing_lecture))
                }
                return
            }
        }

        homePageLectureDetailViewModel.initializeEditingLectureDetail(
            lectureToShow,
            ModeType.Viewing,
        )
        withContext(Dispatchers.Main) {
            navController.navigate("${NavigationDestination.TimetableLecture}?tableId=$timetableId")
        }
    }

    suspend fun handleBookmarkDeeplink() {
        val year = deeplinkUri.getQueryParameter("year")?.toLongOrNull() ?: return
        val semester = deeplinkUri.getQueryParameter("semester")?.semesterStringToLong() ?: return
        val lectureId = deeplinkUri.getQueryParameter("lectureId") ?: return
        val lectureToShow = searchViewModel.getBookmarkLecture(year, semester, lectureId)
        if (lectureToShow == null) {
            withContext(Dispatchers.Main) {
                context.toast(context.getString(R.string.deeplink_page_timetable_lecture_page_not_existing_bookmark_lecture))
            }
            return
        }

        homePageLectureDetailViewModel.initializeEditingLectureDetail(
            lectureToShow,
            ModeType.Viewing,
        )
        withContext(Dispatchers.Main) {
            navController.navigate(NavigationDestination.TimetableLecture)
        }
    }

    fun handleFriendsDeeplink() {
        // TODO: 친구탭 서랍 열기
        val openDrawer = deeplinkUri.getQueryParameter("openDrawer") ?: return
        navController.navigateAsOrigin(NavigationDestination.Home)
        homePageController.update(HomeItem.Friends)
    }

    LaunchedEffect(deeplinkUri) {
        if (deeplinkUri == Uri.EMPTY) return@LaunchedEffect

        when (deeplinkUri.host) {
            // 시간표 강의 업데이트 알림 딥링크 이동
            NavigationDestination.TimetableLecture -> {
                launchSuspendApi(
                    apiOnProgress, apiOnError,
                    loadingIndicatorTitle = context.getString(R.string.deeplink_page_timetable_lecture_page_loading_text),
                ) {
                    handleTimetableLectureDeeplink()
                }
            }
            // 관심강좌 강의 업데이트 알림 딥링크 이동
            NavigationDestination.Bookmark -> {
                launchSuspendApi(
                    apiOnProgress, apiOnError,
                    loadingIndicatorTitle = context.getString(R.string.deeplink_page_timetable_lecture_page_loading_text),
                ) {
                    handleBookmarkDeeplink()
                }
            }
            NavigationDestination.Friends -> {
                handleFriendsDeeplink()
            }
        }

        // 딥링크 핸들링이 끝났으면 초기화해 줘야, 똑같은 딥링크를 다시 눌렀을 때 또 동작할 수 있다.
        DeeplinkExecutor.deeplinkUri.value = Uri.EMPTY
    }
}
