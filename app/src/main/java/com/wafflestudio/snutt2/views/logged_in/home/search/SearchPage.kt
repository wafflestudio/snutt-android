@file:OptIn(ExperimentalFoundationApi::class)

package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.data.lecture_search.LectureStateNew
import com.wafflestudio.snutt2.data.lecture_search.SearchViewModelNew
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getLectureTagText
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getSimplifiedClassTime
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getSimplifiedLocation
import com.wafflestudio.snutt2.lib.getColor
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import com.wafflestudio.snutt2.views.logged_in.home.HideBottomSheet
import com.wafflestudio.snutt2.views.logged_in.home.HomeItem
import com.wafflestudio.snutt2.views.logged_in.home.ShowBottomSheet
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModelNew
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchPage(
    searchResultPagingItems: LazyPagingItems<DataWithState<LectureDto, LectureStateNew>>,
) {
    val navController = LocalNavController.current
    val searchViewModel = hiltViewModel<SearchViewModelNew>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()
    val pageController = LocalHomePageController.current

    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModelNew>()

    val searchKeyword = searchViewModel.searchTitle.collectAsState()
    var searchEditTextFocused by remember { mutableStateOf(false) }
    val keyBoardController = LocalSoftwareKeyboardController.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current

    val scope = rememberCoroutineScope()
    val lazyListState = searchViewModel.lazyListState
    val loadState = searchResultPagingItems.loadState
    val selectedLecture by searchViewModel.selectedLecture.collectAsState()
    val tagsByTagType by searchViewModel.tagsByTagType.collectAsState()
    val selectedTagType by searchViewModel.selectedTagType.collectAsState()
    val selectedTags by searchViewModel.selectedTags.collectAsState()
    var searchOptionSheetState by remember { mutableStateOf(false) }

    Column {
        SearchTopBar {
            SearchIcon(
                modifier = Modifier.clicks {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            searchViewModel.query()
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            // FIXME: EditText 글자가 살짝 중간에서 아래로 치우쳐 있다.
            EditText(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onFocusChanged { searchEditTextFocused = it.isFocused },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            searchViewModel.query()
                            keyBoardController?.hide()
                        }
                    }
                }),
                value = searchKeyword.value,
                onValueChange = {
                    scope.launch {
                        searchViewModel.setTitle(it)
                    }
                },
                singleLine = true,
                hint = stringResource(R.string.search_hint),
                underlineEnabled = false,
            )
            Spacer(modifier = Modifier.width(12.dp))
            if (searchEditTextFocused) ExitIcon(modifier = Modifier.clicks { scope.launch { searchViewModel.clearEditText() } })
            else FilterIcon(
                modifier = Modifier.clicks {
                    searchOptionSheetState = true
                }
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TimeTable(touchEnabled = false, selectedLecture = selectedLecture)
            Column(
                modifier = Modifier
                    .background(SNUTTColors.Black500)
                    .fillMaxSize()
            ) {
                AnimatedVisibility(visible = selectedTags.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(items = selectedTags, key = { it.name }) {
                            TagCell(tagDto = it) {
                                scope.launch {
                                    launchSuspendApi(apiOnProgress, apiOnError) {
                                        searchViewModel.toggleTag(it)
                                        searchViewModel.query()
                                    }
                                }
                            }
                        }
                    }
                }
                when {
                    loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached.not() && searchResultPagingItems.itemCount < 1 -> {
                        SearchPlaceHolder()
                    }
                    loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && searchResultPagingItems.itemCount < 1 || loadState.refresh is LoadState.Error -> {
                        SearchEmptyPage()
                    }
                    else -> {
                        LazyColumn(
                            state = lazyListState, modifier = Modifier.fillMaxSize()
                        ) {
                            items(searchResultPagingItems, key = { it.item.id }) {
                                it?.let {
                                    SearchListItem(lectureDataWithState = it, onSelect = {
                                        scope.launch {
                                            searchViewModel.toggleLectureSelection(it.item)
                                        }
                                    }, onClickAdd = {
                                        scope.launch(Dispatchers.IO) {
                                            launchSuspendApi(apiOnProgress, apiOnError) {
                                                timetableViewModel.addLecture(
                                                    lecture = it.item,
                                                    is_force = false // TODO: is_forced
                                                )
                                                searchViewModel.toggleLectureSelection(it.item)
                                            }
                                        }
                                    }, onClickRemove = {
                                        scope.launch(Dispatchers.IO) {
                                            launchSuspendApi(apiOnProgress, apiOnError) {
                                                timetableViewModel.removeLecture(it.item)
                                                searchViewModel.toggleLectureSelection(it.item)
                                            }
                                        }
                                    }, onClickDetail = {
                                        // FIXME: PagingItem 정보 소실되는 문제
                                        lectureDetailViewModel.initializeEditingLectureDetail(it.item)
                                        navController.navigate(NavigationDestination.LectureDetail)
                                    }, onClickReview = {
                                        scope.launch {
                                            pageController.update(
                                                HomeItem.Review(
                                                    searchViewModel.getLectureReviewUrl(it.item)
                                                )
                                            )
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val showBottomSheet = ShowBottomSheet.current
    val hideBottomSheet = HideBottomSheet.current
    if (searchOptionSheetState) {
        LaunchedEffect(Unit) {
            showBottomSheet(380.dp) {
                SearchOptionSheet(tagsByTagType, selectedTagType) {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            searchViewModel.query()
                        }
                    }
                    scope.launch { hideBottomSheet.invoke(false) }
                }
            }
            searchOptionSheetState = false
        }
    }
}

@Composable
fun LazyItemScope.SearchListItem(
    lectureDataWithState: DataWithState<LectureDto, LectureStateNew>,
    onSelect: () -> Unit,
    onClickAdd: () -> Unit,
    onClickRemove: () -> Unit,
    onClickDetail: () -> Unit,
    onClickReview: () -> Unit,
) {
    val selected = lectureDataWithState.state.selected
    val contained = lectureDataWithState.state.contained

    val lectureTitle = lectureDataWithState.item.course_title
    val instructorCreditText = stringResource(
        R.string.search_result_item_instructor_credit_text,
        lectureDataWithState.item.instructor,
        lectureDataWithState.item.credit
    )
    val remarkText = lectureDataWithState.item.remark
    val tagText = getLectureTagText(lectureDataWithState.item)
    val classTimeText = getSimplifiedClassTime(lectureDataWithState.item)

    val backgroundColor = if (selected) SNUTTColors.Black400 else SNUTTColors.Transparent

    Column(
        modifier = Modifier
            .animateItemPlacement()
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 9.dp)
                .clicks {
                    onSelect()
                }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lectureTitle,
                    style = SNUTTTypography.h4.copy(color = SNUTTColors.White900),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = instructorCreditText,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.White900),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TagIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.White900)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (selected && remarkText.isNotBlank()) remarkText else tagText, // TODO: MARQUEE effect
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.White800),
                    maxLines = 1,
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                ClockIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.White900)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = classTimeText,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.White800),
                    maxLines = 1,
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LocationIcon(
                    modifier = Modifier.size(15.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.White900)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = getSimplifiedLocation(lectureDataWithState.item),
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.White800),
                    maxLines = 1,
                )
            }
        }
        // TODO: 리스트 마지막 아이템은 expand & shrink 애니메이션이 아니라 그냥 생기고 사라지는 걸루.
        // TODO: 애니메이션 속도 좀 더 빠르게
        AnimatedVisibility(visible = lectureDataWithState.state.selected) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 13.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.search_result_item_detail_button),
                    textAlign = TextAlign.Start,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.White900),
                    modifier = Modifier
                        .weight(1f)
                        .clicks { onClickDetail() }
                )
                Text(
                    text = stringResource(R.string.search_result_item_review_button),
                    textAlign = TextAlign.Center,
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.White900),
                    modifier = Modifier
                        .weight(1f)
                        .clicks { onClickReview() }
                )
                Text(
                    text = if (contained) stringResource(R.string.search_result_item_remove_button) else stringResource(
                        R.string.search_result_item_add_button
                    ),
                    textAlign = TextAlign.End,
                    style = SNUTTTypography.body2.copy(
                        color = SNUTTColors.White900, fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clicks {
                            if (contained) onClickRemove() else onClickAdd()
                        }
                )
            }
        }
        Divider(color = SNUTTColors.White400)
    }
}

@Composable
fun SearchPlaceHolder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BigSearchIcon(
            modifier = Modifier
                .width(78.dp)
                .height(76.dp)
                .padding(10.dp)
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_1),
            style = SNUTTTypography.h1.copy(fontSize = 25.sp, color = SNUTTColors.White700)
        )
        Spacer(modifier = Modifier.height(35.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_2),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_3),
            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.White500)
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_4),
            style = SNUTTTypography.subtitle1.copy(fontSize = 18.sp, color = SNUTTColors.White700)
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder_5),
            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.White500)
        )
    }
}

@Composable
fun SearchEmptyPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BigSearchIcon(
            modifier = Modifier
                .width(58.dp)
                .height(56.dp)
        )
        Spacer(modifier = Modifier.height(35.dp))
        Text(
            text = stringResource(R.string.search_result_empty),
            style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.White900, fontSize = 18.sp)
        )
    }
}

@Composable
private fun LazyItemScope.TagCell(
    tagDto: TagDto,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .animateItemPlacement()
            .padding(horizontal = 5.dp)
            .height(30.dp)
            .background(color = tagDto.type.getColor(), shape = RoundedCornerShape(15.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = tagDto.name,
            style = SNUTTTypography.body1.copy(fontSize = 15.sp, color = SNUTTColors.White900),
            textAlign = TextAlign.Center,
        )
        WhiteCloseIcon(
            modifier = Modifier
                .size(20.dp)
                .padding(2.5.dp)
                .clicks { onClick() }
        )
        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Preview
@Composable
fun SearchPagePreview() {
//    SearchPage()
}
