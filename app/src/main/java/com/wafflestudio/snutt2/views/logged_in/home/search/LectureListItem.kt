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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.wafflestudio.snutt2.views.logged_in.bookmark.showDeleteBookmarkDialog
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.LectureListItem(
    lectureDataWithState: DataWithState<LectureDto, LectureState>,
    reviewWebViewContainer: WebViewContainer,
    isBookmarkPage: Boolean = false,
    searchViewModel: SearchViewModel,
    timetableViewModel: TimetableViewModel,
    tableListViewModel: TableListViewModel,
    lectureDetailViewModel: LectureDetailViewModel,
    userViewModel: UserViewModel,
    vacancyViewModel: VacancyViewModel,
) {
    val scope = rememberCoroutineScope()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val bottomSheet = LocalBottomSheetState.current
    val pageController = LocalHomePageController.current
    val context = LocalContext.current
    val navController = LocalNavController.current
    val composableStates = ComposableStatesWithScope(scope)

    val selected = lectureDataWithState.state.selected
    val contained = lectureDataWithState.state.contained
    val bookmarkList by searchViewModel.bookmarkList.collectAsState()
    val bookmarked = bookmarkList.map { it.item.id }.contains(lectureDataWithState.item.lecture_id ?: lectureDataWithState.item.id)
    val vacancyList by vacancyViewModel.vacancyLectures.collectAsState()
    val vacancyRegistered = vacancyList.map { it.item.id }.contains(lectureDataWithState.item.id)

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
        Column(
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .clicks {
                    scope.launch {
                        searchViewModel.toggleLectureSelection(lectureDataWithState.item)
                    }
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lectureTitle,
                    style = SNUTTTypography.h4.copy(color = SNUTTColors.AllWhite),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = instructorCreditText,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.AllWhite),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                TagIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = tagText,
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                )
            }
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                RemarkIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = remarkText.ifEmpty { "없음" },
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.Light
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        AnimatedVisibility(visible = lectureDataWithState.state.selected) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            lectureDetailViewModel.initializeEditingLectureDetail(
                                lectureDataWithState.item, ModeType.Viewing
                            )
                            bottomSheet.setSheetContent {
                                LectureDetailPage(
                                    searchViewModel = searchViewModel,
                                    onCloseViewMode = { scope ->
                                        scope.launch { bottomSheet.hide() }
                                    }
                                )
                            }
                            scope.launch { bottomSheet.show() }
                        },
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DetailIcon(
                        modifier = Modifier.size(23.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                    Text(
                        text = stringResource(R.string.search_result_item_detail_button),
                        style = SNUTTTypography.body2.copy(
                            color = SNUTTColors.AllWhite,
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            verifyEmailBeforeApi(
                                composableStates,
                                api = {
                                    val url =
                                        searchViewModel.getLectureReviewUrl(lectureDataWithState.item)
                                    openReviewBottomSheet(url, reviewWebViewContainer, bottomSheet)
                                },
                                onUnverified = {
                                    if (isBookmarkPage) navController.navigateAsOrigin(
                                        NavigationDestination.Home
                                    )
                                    pageController.update(HomeItem.Review())
                                }
                            )
                        },
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ReviewIcon(
                        modifier = Modifier.size(23.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                    Text(
                        text = stringResource(R.string.search_result_item_review_button),
                        style = SNUTTTypography.body2.copy(
                            color = SNUTTColors.AllWhite,
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            scope.launch {
                                launchSuspendApi(apiOnProgress, apiOnError) {
                                    if (isBookmarkPage) {
                                        showDeleteBookmarkDialog(composableStates, onConfirm = {
                                            searchViewModel.deleteBookmark(lectureDataWithState.item)
                                            searchViewModel.toggleLectureSelection(
                                                lectureDataWithState.item
                                            )
                                        })
                                    } else {
                                        if (bookmarked) {
                                            searchViewModel.deleteBookmark(lectureDataWithState.item)
                                        } else {
                                            searchViewModel.addBookmark(lectureDataWithState.item)
                                            if (userViewModel.firstBookmarkAlert.value) {
                                                userViewModel.setFirstBookmarkAlertShown()
                                                context.toast(context.getString(R.string.bookmark_first_alert_message))
                                            }
                                        }
                                    }
                                }
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BookmarkIcon(
                        modifier = Modifier
                            .size(23.dp),
                        marked = bookmarked,
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                    Text(
                        text = stringResource(R.string.search_result_item_bookmark_button),
                        style = SNUTTTypography.body2.copy(
                            color = SNUTTColors.AllWhite,
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            scope.launch {
                                launchSuspendApi(apiOnProgress, apiOnError) {
                                    if (vacancyRegistered) {
                                        vacancyViewModel.removeVacancyLecture(lectureDataWithState.item.id)
                                    } else {
                                        vacancyViewModel.addVacancyLecture(lectureDataWithState.item.id)
                                    }
                                }
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RingingAlarmIcon(
                        modifier = Modifier.size(23.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                        marked = vacancyRegistered
                    )
                    Text(
                        text = stringResource(R.string.search_result_item_vacancy_button),
                        style = SNUTTTypography.body2.copy(
                            color = SNUTTColors.AllWhite,
                            fontSize = 10.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                Column(
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
                                checkLectureOverlap(
                                    composableStates,
                                    api = {
                                        timetableViewModel.addLecture(
                                            lecture = lectureDataWithState.item,
                                            is_force = false
                                        )
                                        searchViewModel.toggleLectureSelection(lectureDataWithState.item)
                                        tableListViewModel.fetchTableMap()
                                    },
                                    onLectureOverlap = { message ->
                                        showLectureOverlapDialog(
                                            composableStates,
                                            message,
                                            forceAddApi = {
                                                timetableViewModel.addLecture(
                                                    lecture = lectureDataWithState.item,
                                                    is_force = true
                                                )
                                                searchViewModel.toggleLectureSelection(
                                                    lectureDataWithState.item
                                                )
                                            }
                                        )
                                    }
                                )
                            }
                        },
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (contained) {
                        RemoveCircleIcon(
                            modifier = Modifier.size(23.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.AllWhite)
                        )
                    } else {
                        AddCircleIcon(
                            modifier = Modifier.size(23.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.AllWhite)
                        )
                    }
                    Text(
                        text = if (contained) stringResource(R.string.search_result_item_remove_button) else stringResource(
                            R.string.search_result_item_add_button
                        ),
                        style = SNUTTTypography.body2.copy(
                            color = SNUTTColors.AllWhite,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
        Divider(color = SNUTTColors.White400)
    }
}
