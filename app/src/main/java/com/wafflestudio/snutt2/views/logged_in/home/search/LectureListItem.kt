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
    val vacancyRegistered = vacancyList.map { it.id }.contains(lectureDataWithState.item.lecture_id ?: lectureDataWithState.item.id)

    val lectureTitle = lectureDataWithState.item.course_title
    val instructorCreditText = stringResource(
        R.string.search_result_item_instructor_credit_text,
        lectureDataWithState.item.instructor,
        lectureDataWithState.item.credit,
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
                    visibilityThreshold = IntOffset.VisibilityThreshold,
                ),
            )
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
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
                    color = SNUTTColors.AllWhite,
                    style = SNUTTTypography.h4,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = instructorCreditText,
                    color = SNUTTColors.AllWhite,
                    style = SNUTTTypography.body2,
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
                    modifier = Modifier.weight(1f),
                    color = SNUTTColors.AllWhite,
                    fontWeight = FontWeight.Light,
                    style = SNUTTTypography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StarIcon(
                        modifier = Modifier
                            .size(12.dp)
                            .offset(y = 1.dp),
                        filled = false,
                        colorFilter = ColorFilter.tint(SNUTTColors.White),
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = lectureDataWithState.item.review?.displayText ?: "-- (0)", // TODO: dto대신 model 사용하고, review를 non-nullable로 만들기
                        color = SNUTTColors.White,
                        fontWeight = FontWeight.Light,
                        fontSize = 12.sp,
                        style = SNUTTTypography.body2,
                    )
                }
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
                        fontWeight = FontWeight.Light,
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
                        fontWeight = FontWeight.Light,
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
                        fontWeight = FontWeight.Light,
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
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LectureListItemButton(
                    title = stringResource(R.string.search_result_item_detail_button),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        lectureDetailViewModel.initializeEditingLectureDetail(
                            lectureDataWithState.item,
                            ModeType.Viewing,
                        )
                        bottomSheet.setSheetContent {
                            LectureDetailPage(
                                searchViewModel = searchViewModel,
                                vacancyViewModel = vacancyViewModel,
                                onCloseViewMode = { scope ->
                                    scope.launch { bottomSheet.hide() }
                                },
                            )
                        }
                        scope.launch { bottomSheet.show() }
                    },
                ) {
                    DetailIcon(
                        modifier = Modifier.size(23.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                LectureListItemButton(
                    title = stringResource(R.string.search_result_item_review_button),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            val url = lectureDataWithState.item.review?.getReviewUrl(context)
                            openReviewBottomSheet(url, reviewWebViewContainer, bottomSheet)
                        }
                    },
                ) {
                    ThickReviewIcon(
                        modifier = Modifier.size(23.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                LectureListItemButton(
                    title = stringResource(R.string.search_result_item_bookmark_button),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                if (isBookmarkPage) {
                                    showDeleteBookmarkDialog(composableStates, onConfirm = {
                                        searchViewModel.deleteBookmark(lectureDataWithState.item)
                                        searchViewModel.toggleLectureSelection(
                                            lectureDataWithState.item,
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
                ) {
                    BookmarkIcon(
                        modifier = Modifier
                            .size(23.dp),
                        marked = bookmarked,
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                LectureListItemButton(
                    title = stringResource(R.string.search_result_item_vacancy_button),
                    modifier = Modifier.weight(1f),
                    onClick = {
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
                ) {
                    RingingAlarmIcon(
                        modifier = Modifier.size(23.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                        marked = vacancyRegistered,
                    )
                }
                Spacer(modifier = Modifier.weight(0.3f))
                LectureListItemButton(
                    title = if (contained) stringResource(R.string.search_result_item_remove_button) else stringResource(R.string.search_result_item_add_button),
                    modifier = Modifier.weight(1f),
                    onClick = {
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
                                        is_force = false,
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
                                                is_force = true,
                                            )
                                            searchViewModel.toggleLectureSelection(
                                                lectureDataWithState.item,
                                            )
                                        },
                                    )
                                },
                            )
                        }
                    },
                ) {
                    if (contained) {
                        RemoveCircleIcon(
                            modifier = Modifier.size(23.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                        )
                    } else {
                        AddCircleIcon(
                            modifier = Modifier.size(23.dp),
                            colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
                        )
                    }
                }
            }
        }
        Divider(color = SNUTTColors.White400)
    }
}

@Composable
fun LectureListItemButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .clicks {
                onClick()
            },
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
        Text(
            text = title,
            style = SNUTTTypography.body2.copy(
                color = SNUTTColors.AllWhite,
                fontSize = 10.sp,
            ),
        )
    }
}
