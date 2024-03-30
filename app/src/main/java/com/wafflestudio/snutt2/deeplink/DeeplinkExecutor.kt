package com.wafflestudio.snutt2.deeplink

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.SNUTTUtils.semesterStringToLong
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType
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
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val homePageBackStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavigationDestination.Home)
    }
    val homePageLectureDetailViewModel =
        hiltViewModel<LectureDetailViewModel>(homePageBackStackEntry)
    val homePageTableListViewModel = hiltViewModel<TableListViewModel>(homePageBackStackEntry)
    val searchViewModel = hiltViewModel<SearchViewModel>()

    LaunchedEffect(deeplinkUri) {
        if (deeplinkUri == Uri.EMPTY) return@LaunchedEffect

        withContext(Dispatchers.IO) {
            launchSuspendApi(
                apiOnProgress,
                apiOnError,
                loadingIndicatorTitle = "잠시만 기다려 주세요...",
            ) {
                when (deeplinkUri.host) {
                    // 시간표 강의 업데이트 알림 딥링크 이동
                    NavigationDestination.TimetableLecture -> {
                        val timetableId = deeplinkUri.getQueryParameter("timetableId")
                            ?: return@launchSuspendApi
                        val lectureId = deeplinkUri.getQueryParameter("lectureId")
                            ?: return@launchSuspendApi

                        val lectureToShow =
                            homePageTableListViewModel.searchTableById(timetableId).lectureList.find {
                                it.lecture_id == lectureId
                            }

                        homePageLectureDetailViewModel.initializeEditingLectureDetail(
                            lectureToShow,
                            ModeType.Viewing,
                        )
                        withContext(Dispatchers.Main) {
                            navController.navigate("${NavigationDestination.TimetableLecture}?tableId=$timetableId")
                        }
                    }
                    // 관심강좌 강의 업데이트 알림 딥링크 이동
                    NavigationDestination.Bookmark -> {
                        val year = deeplinkUri.getQueryParameter("year")?.toLongOrNull()
                            ?: return@launchSuspendApi
                        val semester =
                            deeplinkUri.getQueryParameter("semester")?.semesterStringToLong()
                                ?: return@launchSuspendApi
                        val lectureId = deeplinkUri.getQueryParameter("lectureId")
                            ?: return@launchSuspendApi
                        val lectureToShow =
                            searchViewModel.getBookmarkLecture(year, semester, lectureId)

                        homePageLectureDetailViewModel.initializeEditingLectureDetail(
                            lectureToShow,
                            ModeType.Viewing,
                        )
                        withContext(Dispatchers.Main) {
                            navController.navigate(NavigationDestination.TimetableLecture)
                        }
                    }
                }
            }
        }
    }
}
