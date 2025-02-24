package com.wafflestudio.snutt2.views.logged_in.home.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CloseIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.color
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.TagCell(
    tagDto: TagDto,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .animateItemPlacement()
            .padding(horizontal = 5.dp)
            .height(30.dp)
            .background(color = tagDto.type.color(), shape = RoundedCornerShape(15.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = (if (tagDto.type == TagType.CATEGORY_PRE2025) stringResource(R.string.search_option_tag_type_general_pre2025) + " " else "") + tagDto.name,
            style = SNUTTTypography.body1.copy(fontSize = 14.sp, color = SNUTTColors.AllWhite),
            textAlign = TextAlign.Center,
        )
        CloseIcon(
            modifier = Modifier
                .width(20.dp) // 스펙 상 15dp이나, CloseIcon이 자체적으로 갖는 여백으로 인해 20dp로 설정
                .clicks { onClick() },
            colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
        )
        Spacer(modifier = Modifier.width(5.dp)) // 스펙 상 5dp이나, CloseIcon이 자체적으로 갖는 여백으로 인해 10dp로 설정
    }
}
