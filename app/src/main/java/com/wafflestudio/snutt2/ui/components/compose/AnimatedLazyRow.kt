package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun <T> AnimatedLazyRow(
    modifier: Modifier = Modifier,
    itemList: List<T>,
    itemKey: ((T) -> Any)?,
    itemContent: @Composable LazyItemScope.(T) -> Unit,
) {
    AnimatedVisibility(visible = itemList.isNotEmpty()) {
        LazyRow(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(items = itemList, key = itemKey) {
                itemContent(it)
            }
        }
    }
}

@SnuttPreview
@Composable
private fun AnimatedLazyRow_TagList() {
    SnuttPreviewSurface {
        AnimatedLazyRow(
            modifier = Modifier.padding(8.dp),
            itemList = listOf("월요일", "화요일", "수요일", "목요일", "금요일"),
            itemKey = { it },
        ) { tag ->
            Text(
                text = tag,
                style = SNUTTTypography.body2,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SNUTTColors.Gray100)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}
