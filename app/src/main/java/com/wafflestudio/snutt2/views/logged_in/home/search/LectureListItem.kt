package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.android.webview.WebViewContainer
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.reviews.ReviewWebView
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import kotlinx.coroutines.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.LectureListItem(
    lectureDataWithState: DataWithState<LectureDto, LectureState>,
    reviewWebViewContainer: WebViewContainer,
    isBookmarkPage: Boolean = false,
    searchViewModel: SearchViewModel = hiltViewModel(),
    timetableViewModel: TimetableViewModel = hiltViewModel(),
    tableListViewModel: TableListViewModel = hiltViewModel(),
    lectureDetailViewModel: LectureDetailViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val bottomSheet = LocalBottomSheetState.current
    val modalState = LocalModalState.current
    val pageController = LocalHomePageController.current
    val context = LocalContext.current

    val selected = lectureDataWithState.state.selected
    val contained = lectureDataWithState.state.contained
    val bookmarked = lectureDataWithState.state.bookmarked

    val lectureTitle = lectureDataWithState.item.course_title
    val instructorCreditText = stringResource(
        R.string.search_result_item_instructor_credit_text,
        lectureDataWithState.item.instructor,
        lectureDataWithState.item.credit
    )
    val remarkText = lectureDataWithState.item.remark
    val tagText = SNUTTStringUtils.getLectureTagText(lectureDataWithState.item)
    val classTimeText = SNUTTStringUtils.getSimplifiedClassTime(lectureDataWithState.item)

    val backgroundColor = if (selected) SNUTTColors.Dim2 else SNUTTColors.Transparent

    Column(
        modifier = Modifier
            .animateItemPlacement(
                animationSpec = spring(
                    stiffness = Spring.StiffnessHigh,
                    visibilityThreshold = IntOffset.VisibilityThreshold
                )
            )
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Divider(color = SNUTTColors.White400)
        Column(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .clicks {
                    scope.launch {
                        searchViewModel.toggleLectureSelection(lectureDataWithState.item)
                    }
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lectureTitle,
                    style = SNUTTTypography.h4.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = instructorCreditText,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TagIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (selected && remarkText.isNotBlank()) remarkText else tagText, // TODO: MARQUEE effect
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ClockIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = classTimeText,
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LocationIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = SNUTTStringUtils.getSimplifiedLocation(lectureDataWithState.item),
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                )
            }
        }
        AnimatedVisibility(visible = lectureDataWithState.state.selected) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.search_result_item_detail_button),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            lectureDetailViewModel.initializeEditingLectureDetail(
                                lectureDataWithState.item
                            )
                            lectureDetailViewModel.setViewMode(true)
                            bottomSheet.setSheetContent {
                                LectureDetailPage(onCloseViewMode = { scope ->
                                    scope.launch {
                                        bottomSheet.hide()
                                    }
                                }, vm = lectureDetailViewModel, searchViewModel = searchViewModel)
                            }
                            scope.launch { bottomSheet.show() }
                        }
                )
                Text(
                    text = stringResource(R.string.search_result_item_review_button),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            scope.launch {
                                handleReviewPageWithEmailVerifyCheck(
                                    apiOnProgress, apiOnError,
                                    api = {
                                        val url =
                                            searchViewModel.getLectureReviewUrl(lectureDataWithState.item)
                                        val job: CompletableJob = Job()
                                        scope.launch {
                                            reviewWebViewContainer.openPage("$url&on_back=close")
                                            job.complete()
                                        }
                                        joinAll(job)
                                        scope.launch(Dispatchers.Main) {
                                            bottomSheet.setSheetContent {
                                                CompositionLocalProvider(LocalReviewWebView provides reviewWebViewContainer) {
                                                    ReviewWebView(0.95f)
                                                }
                                            }
                                            bottomSheet.show()
                                        }
                                    },
                                    onUnVerified = {
                                        modalState
                                            .set(
                                                onDismiss = { modalState.hide() },
                                                title = context.getString(R.string.email_unverified_cta_title),
                                                positiveButton = context.getString(R.string.common_ok),
                                                negativeButton = context.getString(R.string.common_cancel),
                                                onConfirm = {
                                                    modalState.hide()
                                                    scope.launch {
                                                        pageController.update(HomeItem.Review())
                                                    }
                                                }
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.email_unverified_cta_message),
                                                    style = SNUTTTypography.button,
                                                )
                                            }
                                            .show()
                                    }
                                )
                            }
                        }
                )
                Spacer(modifier = Modifier.weight(0.3f))
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            scope.launch {
                                launchSuspendApi(apiOnProgress, apiOnError) {
                                    if (isBookmarkPage) {
                                        modalState
                                            .set(
                                                onDismiss = { modalState.hide() },
                                                onConfirm = {
                                                    scope.launch {
                                                        launchSuspendApi(
                                                            apiOnProgress,
                                                            apiOnError
                                                        ) {
                                                            searchViewModel.deleteBookmark(
                                                                lectureDataWithState.item
                                                            )
                                                            searchViewModel.toggleLectureSelection(
                                                                lectureDataWithState.item
                                                            )
                                                            modalState.hide()
                                                            context.toast(context.getString(R.string.bookmark_remove_toast))
                                                        }
                                                    }
                                                },
                                                title = context.getString(R.string.notifications_app_bar_title),
                                                content = {
                                                    Text(
                                                        text = stringResource(R.string.bookmark_remove_check_message),
                                                        style = SNUTTTypography.body1
                                                    )
                                                },
                                                positiveButton = context.getString(R.string.common_ok),
                                                negativeButton = context.getString(R.string.common_cancel),
                                            )
                                            .show()
                                    } else {
                                        if (lectureDataWithState.state.bookmarked) {
                                            searchViewModel.deleteBookmark(lectureDataWithState.item)
                                        } else {
                                            searchViewModel.addBookmark(lectureDataWithState.item)
                                            if(userViewModel.firstBookmarkAlert.value) {
                                                userViewModel.setFirstBookmarkAlertShown()
                                                context.toast(context.getString(R.string.bookmark_first_alert_message))
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BookmarkIcon(
                        modifier = Modifier
                            .size(15.dp),
                        marked = bookmarked,
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = stringResource(R.string.search_result_item_bookmark_button),
                        textAlign = TextAlign.Center,
                        style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    )
                }
                Text(
                    text = if (contained) stringResource(R.string.search_result_item_remove_button) else stringResource(
                        R.string.search_result_item_add_button
                    ),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            if (contained) {
                                scope.launch(Dispatchers.IO) {
                                    launchSuspendApi(apiOnProgress, apiOnError) {
                                        timetableViewModel.removeLecture(lectureDataWithState.item)
                                        searchViewModel.toggleLectureSelection(lectureDataWithState.item)
                                        tableListViewModel.fetchTableMap()
                                    }
                                }
                            } else {
                                scope.launch(Dispatchers.IO) {
                                    lectureApiWithOverlapDialog(
                                        apiOnProgress,
                                        apiOnError,
                                        onLectureOverlap = { message ->
                                            modalState
                                                .set(
                                                    onDismiss = { modalState.hide() },
                                                    onConfirm = {
                                                        scope.launch {
                                                            searchViewModel.selectedLecture.value?.let { lecture ->
                                                                launchSuspendApi(
                                                                    apiOnProgress,
                                                                    apiOnError
                                                                ) {
                                                                    timetableViewModel.addLecture(
                                                                        lecture = lecture,
                                                                        is_force = true
                                                                    )
                                                                    searchViewModel.toggleLectureSelection(
                                                                        lecture
                                                                    )
                                                                }
                                                            }
                                                            modalState.hide()
                                                        }
                                                    },
                                                    title = context.getString(R.string.lecture_overlap_error_message),
                                                    positiveButton = context.getString(R.string.common_ok),
                                                    negativeButton = context.getString(R.string.common_cancel),
                                                    content = {
                                                        Text(
                                                            text = message,
                                                            style = SNUTTTypography.body1
                                                        )
                                                    }
                                                )
                                                .show()
                                        }
                                    ) {
                                        timetableViewModel.addLecture(
                                            lecture = lectureDataWithState.item,
                                            is_force = false
                                        )
                                        searchViewModel.toggleLectureSelection(lectureDataWithState.item)
                                        tableListViewModel.fetchTableMap()
                                    }
                                }
                            }
                        }
                )
            }
        }
    }
}
