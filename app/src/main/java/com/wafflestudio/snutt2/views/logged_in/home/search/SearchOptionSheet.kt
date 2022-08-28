package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.components.compose.VividCheckedIcon
import com.wafflestudio.snutt2.components.compose.VividUncheckedIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.data.lecture_search.SearchViewModelNew
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import kotlinx.coroutines.launch

@Composable
fun SearchOptionSheet(
    tagsByTagType: State<List<Selectable<TagDto>>>,
    applyOption: () -> Unit,
) {
    val viewModel = hiltViewModel<SearchViewModelNew>()
    val scope = rememberCoroutineScope()

    // TODO: 더 깔끔하게
    val tagTypeList = listOf(
        "학년" to TagType.ACADEMIC_YEAR,
        "구분" to TagType.CLASSIFICATION,
        "학점" to TagType.CREDIT,
        "학과" to TagType.DEPARTMENT,
        "교양분류" to TagType.CATEGORY,
        "기타" to TagType.ETC
    )
    val departmentItems by remember {
        mutableStateOf(
            tagsByTagType.value.filter {
                it.item.type == TagType.DEPARTMENT
            }
        )
    }
    val selectedTagType = viewModel.selectedTagType

    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .clicks { }
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                LazyColumn(state = rememberLazyListState()) {
                    items(tagTypeList) { (name, type) ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = name,
                            fontSize = 17.sp,
                            fontWeight = if (type == selectedTagType.value) FontWeight.Bold else FontWeight.Light,
                            modifier = Modifier.clicks {
                                scope.launch { viewModel.setTagType(type) }
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(260.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                tagsByTagType.value.forEach {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .fillMaxWidth()
                            .clicks {
                                scope.launch { viewModel.toggleTag(it.item) }
                            }
                    ) {
                        if (it.state) VividCheckedIcon(modifier = Modifier.size(15.dp))
                        else VividUncheckedIcon(modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = it.item.name, modifier = Modifier.weight(1f), fontSize = 14.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
        Box(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .fillMaxWidth()
                .clicks { applyOption() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "필터 적용")
        }
    }
}

@Composable
@Preview
fun SearchOptionSheetPreview() {
    SearchOptionSheet(mutableStateOf(emptyList())) {}
}
