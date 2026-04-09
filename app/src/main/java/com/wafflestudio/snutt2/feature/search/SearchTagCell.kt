package com.wafflestudio.snutt2.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.CloseIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.components.compose.displayName
import com.wafflestudio.snutt2.domain.model.SearchTag
import com.wafflestudio.snutt2.ui.util.color
import com.wafflestudio.snutt2.domain.model.TagType
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun SearchTagCell(
    searchTag: SearchTag,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(30.dp)
            .background(color = searchTag.type.color(), shape = RoundedCornerShape(15.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = (if (searchTag.type == TagType.CATEGORY_PRE2025) stringResource(R.string.search_option_tag_type_general_pre2025) + " " else "") + searchTag.displayName(),
            style = SNUTTTypography.body1.copy(fontSize = 14.sp, color = SNUTTColors.AllWhite),
            textAlign = TextAlign.Center,
        )
        CloseIcon(
            modifier = Modifier
                .width(20.dp)
                .clicks { onClick() },
            colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
        )
        Spacer(modifier = Modifier.width(5.dp))
    }
}

@Preview
@Composable
private fun SearchTagCellPreview() {
    Row {
        SearchTagCell(
            searchTag = SearchTag.Regular(TagType.DEPARTMENT, "컴퓨터공학부"),
            onClick = {},
        )
        SearchTagCell(
            searchTag = SearchTag.Regular(TagType.CLASSIFICATION, "교양"),
            onClick = {},
            modifier = Modifier.padding(start = 5.dp),
        )
        SearchTagCell(
            searchTag = SearchTag.TimeEmpty,
            onClick = {},
            modifier = Modifier.padding(start = 5.dp),
        )
    }
}
