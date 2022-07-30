package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.items
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getLectureTagText
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getSimplifiedClassTime
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getSimplifiedLocation
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.views.logged_in.home.SearchLazyListContext
import com.wafflestudio.snutt2.views.logged_in.home.SearchResultContext
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable

@Composable
fun SearchPage(
    selectLecture: (LectureDto) -> Unit,
    addLecture: (LectureDto, Boolean) -> Unit,
    removeLecture: (LectureDto) -> Unit,
    searchLecture: (String) -> Unit
) {
    var searchKeyword by remember { mutableStateOf("") }

    val searchResultPagingItems = SearchResultContext.current
    val loadState = searchResultPagingItems.loadState

    val listState = SearchLazyListContext.current

    Column {
        TopAppBar {
            EditText(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = searchKeyword,
                onValueChange = { searchKeyword = it },
                hint = stringResource(R.string.search_hint),
                onSearch = { searchLecture(searchKeyword) }
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            TimeTable(touchEnabled = false)

            Box(
                modifier = Modifier
                    .background(Color(0x80000000)) // TODO: 임시
                    .fillMaxSize()
            ) {
                if (loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached.not() && searchResultPagingItems.itemCount < 1) {
                    SearchPlaceHolder()
                } else if (loadState.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && searchResultPagingItems.itemCount < 1 || loadState.refresh is LoadState.Error) {
                    SearchEmptyPage()
                } else
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .background(Color(0x80000000)) // TODO: 임시
                            .fillMaxSize()
                    ) {
                        items(searchResultPagingItems) {
                            it?.let {
                                SearchListItem(
                                    lectureDataWithState = it,
                                    onSelect = { selectLecture(it.item) },
                                    onClickAdd = { addLecture(it.item, false) },
                                    onClickRemove = { removeLecture(it.item) }
                                )
                            }
                        }
                    }
            }
        }
    }
}

@Composable
fun SearchListItem(
    lectureDataWithState: DataWithState<LectureDto, LectureState>,
    onSelect: () -> Unit,
    onClickAdd: () -> Unit,
    onClickRemove: () -> Unit
) {
    val selected = lectureDataWithState.state.selected
    val contained = lectureDataWithState.state.contained

    val lectureTitle = lectureDataWithState.item.course_title
    val instructorCreditText =
        lectureDataWithState.item.instructor + " / " + lectureDataWithState.item.credit + "학점"
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
                        text = lectureTitle,
                        modifier = Modifier.weight(1f)
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
                        maxLines = 1,
                        modifier = Modifier.alpha(0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(7.dp))
                Row {
                    ClockIcon()
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = classTimeText,
                        maxLines = 1,
                        modifier = Modifier.alpha(0.8f)
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
                    Text(text = "강의계획서", modifier = Modifier.clicks { })
                    Text(text = "강의평")
                    Text(
                        text = if (contained) "삭제하기" else "추가하기",
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
        Text(text = "SNUTT 검색 꿀팁 \uD83C\uDF6F", fontSize = 25.sp) // TODO: 나중에 typography 맞추기
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
        Text(text = "검색 결과가 없습니다.", fontSize = 25.sp) // TODO: 나중에 typography 맞추기
    }
}

// @Preview
// @Composable
// fun SearchPagePreview() {
//    SearchPage(
//        selectLecture = { _ -> },
//        addLecture = { _, _ -> },
//        removeLecture = { _ -> },
//        searchLecture = { _ -> },
//    )
// }
