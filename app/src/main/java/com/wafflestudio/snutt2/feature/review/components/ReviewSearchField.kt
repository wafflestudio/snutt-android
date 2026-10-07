package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewSearchField(
    query: String,
    isSearching: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit,
    onFilterClick: () -> Unit,
    onSearchSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val shape = RoundedCornerShape(12.dp)

    LaunchedEffect(isSearching) {
        if (isSearching) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(shape)
            .background(SNUTTColors.BackgroundLight)
            .then(if (isSearching) Modifier else Modifier.clickable(onClick = onSearchClick))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Image(
            painter = painterResource(if (isSearching) R.drawable.ic_arrow_back else R.drawable.ic_search_unselected),
            contentDescription = if (isSearching) stringResource(R.string.review_search_back) else null,
            colorFilter = ColorFilter.tint(if (isSearching) SNUTTColors.TextAlternative else SNUTTColors.TextMed),
            modifier = Modifier
                .size(if (isSearching) 24.dp else 20.dp)
                .then(if (isSearching) Modifier.clickable(onClick = onBackClick) else Modifier),
        )

        if (isSearching) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                textStyle = SNUTTTypography.body1.copy(fontSize = 16.sp, color = SNUTTColors.Black900),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() }),
            )
            if (query.isNotEmpty()) {
                Image(
                    painter = painterResource(R.drawable.ic_close_circle),
                    contentDescription = stringResource(R.string.review_search_clear),
                    colorFilter = ColorFilter.tint(SNUTTColors.SearchClear),
                    modifier = Modifier.size(20.dp).clickable(onClick = onClearClick),
                )
            }
        } else {
            Text(
                text = stringResource(R.string.review_search_placeholder),
                style = SNUTTTypography.body1.copy(fontSize = 16.sp, color = SNUTTColors.TextMed),
                modifier = Modifier.weight(1f),
            )
        }

        Image(
            painter = painterResource(R.drawable.ic_filter),
            contentDescription = stringResource(R.string.review_filter),
            colorFilter = ColorFilter.tint(SNUTTColors.TextPlain),
            modifier = Modifier.size(24.dp).clickable(onClick = onFilterClick),
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewSearchField_Default() {
    SnuttPreviewCenteredSurface {
        ReviewSearchField("", false, {}, {}, {}, {}, {}, {})
    }
}
