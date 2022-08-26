package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.views.NavControllerContext
import com.wafflestudio.snutt2.views.NavigationDestination
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
    // TODO: 검색 태그
    val navController = NavControllerContext.current
    val searchViewModel = hiltViewModel<SearchViewModelNew>()
    val timetableViewModel = hiltViewModel<TimetableViewModel>()

    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModelNew>()

    val searchKeyword = searchViewModel.searchTitle.collectAsState()
    var searchEditTextFocused by remember { mutableStateOf(false) }
    val keyBoardController = LocalSoftwareKeyboardController.current

    val scope = rememberCoroutineScope()
    val lazyListState = searchViewModel.lazyListState
    val loadState = searchResultPagingItems.loadState
    val selectedLecture = searchViewModel.selectedLecture.collectAsState()

    Column {
        TopAppBar {
            EditText(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onFocusChanged { searchEditTextFocused = it.isFocused },
                leadingIcon = { SearchIcon() },
                trailingIcon = {
                    if (searchEditTextFocused) ExitIcon() else FilterIcon()
                },
                keyBoardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    scope.launch {
                        searchViewModel.setTitle(searchKeyword.value)
                        searchViewModel.query()
                        keyBoardController?.hide()
                    }
                }),
                value = searchKeyword.value,
                onValueChange = {
                    scope.launch {
                        searchViewModel.setTitle(it)
                    }
                },
                hint = stringResource(R.string.search_hint)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TimeTable(touchEnabled = false, selectedLecture = selectedLecture)

            Box(
                modifier = Modifier
                    .background(Color(0x80000000)) // TODO: 임시
                    .fillMaxSize()
            ) {
                when {
                    loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached.not() && searchResultPagingItems.itemCount < 1 -> {
                        SearchPlaceHolder()
                    }
                    loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && searchResultPagingItems.itemCount < 1 || loadState.refresh is LoadState.Error -> {
                        SearchEmptyPage()
                    }
                    else -> {
                        LazyColumn(
                            state = lazyListState,
                            modifier = Modifier
                                .background(Color(0x80000000)) // TODO: 임시
                                .fillMaxSize()
                        ) {
                            items(searchResultPagingItems) {
                                it?.let {
                                    SearchListItem(lectureDataWithState = it, onSelect = {
                                        scope.launch {
                                            searchViewModel.toggleLectureSelection(it.item)
                                        }
                                    }, onClickAdd = {
                                        scope.launch(Dispatchers.IO) {
                                            // FIXME: data store 에서의 json 인코딩 문제로, 서버 500 발생
                                            timetableViewModel.addLecture(
                                                lecture = it.item,
                                                is_force = false // TODO: is_forced
                                            )
                                            searchViewModel.toggleLectureSelection(it.item)
                                        }
                                    }, onClickRemove = {
                                        // FIXME: data store 에서의 json 인코딩 문제로, 서버 500 발생
                                        scope.launch {
                                            timetableViewModel.removeLecture(it.item)
                                            searchViewModel.toggleLectureSelection(it.item)
                                        }
                                    }, onClickDetail = {
                                        // FIXME: PagingItem 정보 소실되는 문제
                                        lectureDetailViewModel.initializeEditingLectureDetail(it.item)
                                        navController.navigate(NavigationDestination.LectureDetail)
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchListItem(
    lectureDataWithState: DataWithState<LectureDto, LectureStateNew>,
    onSelect: () -> Unit,
    onClickAdd: () -> Unit,
    onClickRemove: () -> Unit,
    onClickDetail: () -> Unit,
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

    val backgroundColor = if (selected) Color(0x60000000) else Color.Transparent // TODO: 임시

    CompositionLocalProvider(
        LocalContentColor.provides(Color.White) // TODO: 임시
    ) {
        Column(
            modifier = Modifier
                .background(backgroundColor) // TODO: 임시
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clicks {
                        onSelect()
                    }
            ) {
                Row {
                    Text(
                        text = lectureTitle, modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = instructorCreditText,
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(7.dp))
                Row {
                    TagIcon()
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (selected && remarkText.isNotBlank()) remarkText else tagText, // TODO: MARQUEE effect
                        maxLines = 1, modifier = Modifier.alpha(0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(7.dp))
                Row {
                    ClockIcon()
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = classTimeText, maxLines = 1, modifier = Modifier.alpha(0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(7.dp))
                Row {
                    LocationIcon()
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = getSimplifiedLocation(lectureDataWithState.item),
                        maxLines = 1,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
            }
            // TODO: 리스트 마지막 아이템은 expand & shrink 애니메이션이 아니라 그냥 생기고 사라지는 걸루.
            // TODO: 애니메이션 속도 좀 더 빠르게
            AnimatedVisibility(visible = lectureDataWithState.state.selected) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 15.dp, horizontal = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.search_result_item_detail_button),
                        modifier = Modifier.clicks { onClickDetail() }
                    )
                    Text(
                        text = stringResource(R.string.search_result_item_review_button),
                        modifier = Modifier.clicks {
                            // TODO
                        }
                    )
                    Text(
                        text = if (contained) stringResource(R.string.search_result_item_remove_button)
                        else stringResource(R.string.search_result_item_add_button),
                        modifier = Modifier.clicks {
                            if (contained) onClickRemove() else onClickAdd()
                        }
                    )
                }
            }
            Divider(color = Color.White) // TODO: 임시
        }
    }
}

@Composable
fun SearchPlaceHolder() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.img_search_big),
            contentDescription = "",
            modifier = Modifier
                .padding(10.dp)
                .width(78.dp)
                .height(76.dp)
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.search_result_placeholder), fontSize = 25.sp
        ) // TODO: 나중에 typography 맞추기
    }
}

@Composable
fun SearchEmptyPage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.img_search_big),
            contentDescription = "",
            modifier = Modifier
                .padding(10.dp)
                .width(78.dp)
                .height(76.dp)
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.search_result_empty), fontSize = 25.sp
        ) // TODO: 나중에 typography 맞추기
    }
}

@Preview
@Composable
fun SearchPagePreview() {
//    SearchPage()
}
