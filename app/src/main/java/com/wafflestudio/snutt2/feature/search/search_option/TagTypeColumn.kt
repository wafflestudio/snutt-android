package com.wafflestudio.snutt2.feature.search.search_option

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.TagType
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import kotlinx.coroutines.launch

@Composable
fun TagTypeColumn(
    tagTypesNotEmpty: List<TagType>,
    selectedTagType: TagType,
    baseAnimatedFloat: State<Float>,
    onSelectTagType: (TagType) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val tagTypeList = listOf(
        stringResource(R.string.search_option_tag_type_sort_criteria) to TagType.SORT_CRITERIA,
        stringResource(R.string.search_option_tag_type_classification) to TagType.CLASSIFICATION,
        stringResource(R.string.search_option_tag_type_department) to TagType.DEPARTMENT,
        stringResource(R.string.search_option_tag_type_academic_year) to TagType.ACADEMIC_YEAR,
        stringResource(R.string.search_option_tag_type_credit) to TagType.CREDIT,
        stringResource(R.string.search_option_tag_type_time) to TagType.TIME,
        stringResource(R.string.search_option_tag_type_general_category) to TagType.CATEGORY,
        stringResource(R.string.search_option_tag_type_general_category_pre2025) to TagType.CATEGORY_PRE2025,
        stringResource(R.string.search_option_tag_type_etc) to TagType.ETC,
    )

    val alphaAnimatedFloat = 1f - baseAnimatedFloat.value
    val offsetXAnimatedDp =
        baseAnimatedFloat.value.dp * -SearchOptionSheetConstants.TAG_COLUMN_WIDTH_DP

    Column(
        modifier = Modifier
            .width(SearchOptionSheetConstants.TAG_COLUMN_WIDTH_DP.dp)
            .alpha(alphaAnimatedFloat)
            .offset(x = offsetXAnimatedDp)
            .padding(start = 20.dp, bottom = 10.dp),
    ) {
        tagTypeList.filter { tagTypesNotEmpty.contains(it.second) }.forEach { (name, type) ->
            Text(
                text = name,
                style = SNUTTTypography.h2.copy(
                    fontSize = 17.sp,
                    color = if (type == selectedTagType) {
                        SNUTTColors.Black900
                    } else {
                        SNUTTColors.Gray200
                    },
                ),
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clicks {
                        scope.launch {
                            onSelectTagType(type)
                        }
                    },
            )
        }
    }
}
