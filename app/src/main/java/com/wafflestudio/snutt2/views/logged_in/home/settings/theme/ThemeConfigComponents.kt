package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AddIcon
import com.wafflestudio.snutt2.components.compose.QuestionCircleIcon
import com.wafflestudio.snutt2.components.compose.ThemeIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.components.compose.displayName
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.TableTheme
import com.wafflestudio.snutt2.domainmodel.preview.PreviewData
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.ui.onSurfaceVariant
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingColumn

@Composable
internal fun ThemesRow(
    title: String,
    themes: List<TableTheme>,
    onClickItem: (TableTheme) -> Unit,
    modifier: Modifier = Modifier,
    leadingItem: (@Composable () -> Unit)? = null,
) {
    SettingColumn(
        title = title,
        titleStyle = SNUTTTypography.body2.copy(
            color = MaterialTheme.colors.onSurfaceVariant,
            fontSize = 13.sp,
        ),
        modifier = modifier,
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .padding(top = 20.dp, bottom = 12.dp),
        ) {
            item {
                Spacer(modifier = Modifier.width(20.dp))
                leadingItem?.let {
                    it()
                    Spacer(modifier = Modifier.width(20.dp))
                }
            }

            items(items = themes) { theme ->
                ThemeItem(
                    theme = theme,
                    onClick = { onClickItem(theme) },
                )
                Spacer(modifier = Modifier.width(20.dp))
            }
        }
    }
}

@Composable
fun AddThemeItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clicks { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(color = SNUTTColors.VacancyGray, shape = RoundedCornerShape(6.dp)),
        ) {
            AddIcon(
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurface),
            )
        }
        Spacer(modifier.height(8.dp))
        Text(
            text = stringResource(R.string.theme_create),
            style = SNUTTTypography.body2,
        )
    }
}

@Composable
private fun ThemeItem(
    theme: TableTheme,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clicks { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            ThemeIcon(
                theme = theme,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(6.dp)),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .widthIn(max = 80.dp)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = theme.displayName(),
                modifier = Modifier.weight(1f, false),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = SNUTTTypography.body2,
            )
        }
    }
}

@Composable
internal fun ThemeGuideTexts(
    modifier: Modifier = Modifier,
) {
    val texts = listOf(
        stringResource(R.string.theme_config_guide_0),
        stringResource(R.string.theme_config_guide_1),
        stringResource(R.string.theme_config_guide_2),
    )
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            QuestionCircleIcon(
                modifier = Modifier.size(14.dp),
                colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.theme_config_guide_title),
                style = SNUTTTypography.h5.copy(color = if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(ParagraphStyle(lineHeight = 12.sp * 1.3f)) {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(texts[0])
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.Normal)) {
                        append(texts[1])
                    }
                }
            },
            style = SNUTTTypography.body2.copy(color = if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.theme_config_guide_2),
            style = SNUTTTypography.body2.copy(color = if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2),
        )
    }
}

// region Previews

@Preview
@Composable
private fun ThemesRowWithLeadingItemPreview() {
    SNUTTTheme {
        ThemesRow(
            title = "내 커스텀 테마",
            themes = listOf(PreviewData.previewCustomTheme1, PreviewData.previewCustomTheme2),
            onClickItem = {},
            leadingItem = { AddThemeItem(onClick = {}) },
        )
    }
}

@Preview
@Composable
private fun ThemesRowWithoutLeadingItemPreview() {
    SNUTTTheme {
        ThemesRow(
            title = "기본 테마",
            themes = List(6) { BuiltInTheme.fromCode(it) },
            onClickItem = {},
        )
    }
}

@Preview
@Composable
private fun AddThemeItemPreview() {
    SNUTTTheme {
        AddThemeItem(onClick = {})
    }
}

@Preview
@Composable
private fun ThemeItemBuiltInPreview() {
    SNUTTTheme {
        ThemeItem(theme = BuiltInTheme.fromCode(0), onClick = {})
    }
}

@Preview
@Composable
private fun ThemeItemCustomPreview() {
    SNUTTTheme {
        ThemeItem(theme = PreviewData.previewCustomTheme1, onClick = {})
    }
}

@Preview
@Composable
private fun ThemeGuideTextsPreview() {
    SNUTTTheme {
        ThemeGuideTexts()
    }
}

// endregion
