package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.VividCheckedIcon
import com.wafflestudio.snutt2.components.compose.VividUncheckedIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import kotlinx.coroutines.launch

@Composable
fun SearchOptionSheet(
    tagsByTagType: List<Selectable<TagDto>>,
    selectedTagType: TagType,
    applyOption: () -> Unit,
) {
    val viewModel = hiltViewModel<SearchViewModel>()
    val scope = rememberCoroutineScope()

    // TODO: 다른 곳에 const하게 박아 놓으면 좋을 것 같다.
    val tagTypeList = listOf(
        stringResource(R.string.search_option_tag_type_academic_year) to TagType.ACADEMIC_YEAR,
        stringResource(R.string.search_option_tag_type_classification) to TagType.CLASSIFICATION,
        stringResource(R.string.search_option_tag_type_credit) to TagType.CREDIT,
        stringResource(R.string.search_option_tag_type_department) to TagType.DEPARTMENT,
        stringResource(R.string.search_option_tag_type_general_category) to TagType.CATEGORY,
        stringResource(R.string.search_option_tag_type_etc) to TagType.ETC
    )

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxWidth()
            .clicks { }
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        // tag column의 높이를 tagType column의 높이로 설정
        SubcomposeLayout(modifier = Modifier) { constraints ->
            val tagTypeColumn = subcompose(slotId = 1) {
                Column(
                    modifier = Modifier
                        .width(120.dp)
                        .padding(start = 20.dp, bottom = 10.dp),
                ) {
                    tagTypeList.forEach { (name, type) ->
                        Text(
                            text = name,
                            style = SNUTTTypography.h2.copy(
                                fontSize = 17.sp,
                                color = if (type == selectedTagType) SNUTTColors.Black900
                                else SNUTTColors.Gray200
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .clicks {
                                    scope.launch { viewModel.setTagType(type) }
                                }
                        )
                    }
                }
            }
            val tagTypePlaceables = tagTypeColumn.map {
                it.measure(constraints)
            }
            val maxSize = tagTypePlaceables.fold(IntSize.Zero) { currentMax, placeable ->
                IntSize(
                    width = maxOf(currentMax.width, placeable.width),
                    height = maxOf(currentMax.height, placeable.height)
                )
            }

            layout(maxSize.width, maxSize.height) {
                val tagListColumn = subcompose(slotId = 2, content = {
                    LazyColumn(
                        modifier = Modifier
                            .height(maxSize.height.toDp())
                            .padding(start = 20.dp, bottom = 10.dp),
                        // FIXME: rememberLazyListState() 넣으면 오류
                    ) {
                        items(tagsByTagType) {
                            Row(
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth()
                                    .clicks {
                                        scope.launch { viewModel.toggleTag(it.item) }
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (it.state) VividCheckedIcon(modifier = Modifier.size(15.dp))
                                else VividUncheckedIcon(modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = it.item.name,
                                    modifier = Modifier.weight(1f),
                                    style = SNUTTTypography.body1,
                                )
                            }
                        }
                    }
                })
                val tagListPlaceables: List<Placeable> = tagListColumn.map {
                    it.measure(constraints)
                }
                tagTypePlaceables.forEach { it.placeRelative(0, 0) }
                tagListPlaceables.forEach { it.placeRelative(maxSize.width, 0) }
            }
        }
        Row(
            modifier = Modifier
                .background(SNUTTColors.Sky)
                .fillMaxWidth()
                .clicks { applyOption() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.search_option_apply_button),
                textAlign = TextAlign.Center,
                style = SNUTTTypography.h3.copy(fontSize = 17.sp, color = SNUTTColors.White900),
                modifier = Modifier.padding(vertical = 20.dp)
            )
        }
    }
}

@Composable
@Preview
fun SearchOptionSheetPreview() {
}
