package com.wafflestudio.snutt2.feature.home.drawer.bottomsheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.ui.components.compose.ShareIcon
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.theme.isDarkMode

@Composable
fun MoreActionSheet(
    tableSummary: TableSummary,
    onClickChangeTableName: (tableSummary: TableSummary) -> Unit,
    onClickSetPrimary: (tableSummary: TableSummary) -> Unit,
    onClickUnsetPrimary: (tableSummary: TableSummary) -> Unit,
    onClickShare: (tableSummary: TableSummary) -> Unit,
    onClickSetTheme: (tableSummary: TableSummary) -> Unit,
    onClickDeleteTable: (tableSummary: TableSummary) -> Unit,
) {
    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        MoreActionItem(
            icon = { SnuttIcon(R.drawable.ic_write, modifier = Modifier.size(30.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900)) },
            text = stringResource(R.string.home_drawer_table_title_change),
            onClick = { onClickChangeTableName(tableSummary) },
        )
        MoreActionItem(
            icon = {
                if (tableSummary.isPrimary) {
                    SnuttIcon(
                        R.drawable.ic_people_off,
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                } else {
                    SnuttIcon(
                        R.drawable.ic_people_on,
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                }
            },
            text = if (tableSummary.isPrimary) {
                stringResource(R.string.home_drawer_table_set_not_primary)
            } else {
                stringResource(
                    R.string.home_drawer_table_set_primary,
                )
            },
        ) {
            if (tableSummary.isPrimary) {
                onClickUnsetPrimary(tableSummary)
            } else {
                onClickSetPrimary(tableSummary)
            }
        }
        MoreActionItem(
            icon = { ShareIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.home_drawer_table_share),
        ) {
            onClickShare(tableSummary)
        }
        MoreActionItem(
            icon = { SnuttIcon(R.drawable.ic_palette, modifier = Modifier.size(30.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900)) },
            text = stringResource(R.string.home_drawer_table_theme_change),
        ) {
            onClickSetTheme(tableSummary)
        }
        MoreActionItem(
            icon = { SnuttIcon(R.drawable.ic_trash, modifier = Modifier.size(30.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900)) },
            text = stringResource(R.string.home_drawer_table_delete),
        ) {
            onClickDeleteTable(tableSummary)
        }
    }
}

@Composable
private fun MoreActionItem(
    icon: @Composable () -> Unit,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clicks { if (enabled) onClick() }
            .padding(vertical = 10.dp, horizontal = 22.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            icon()
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = text,
                style = SNUTTTypography.body1.copy(
                    color = if (enabled) {
                        MaterialTheme.colors.onSurface
                    } else {
                        if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2
                    },
                ),
            )
        }
    }
}

@SnuttPreview
@Composable
private fun MoreActionSheet_PrimaryTable() {
    SnuttPreviewSurface {
        MoreActionSheet(
            tableSummary = TableSummary.Default.copy(
                title = "2025-1학기",
                isPrimary = true,
            ),
            onClickChangeTableName = {},
            onClickSetPrimary = {},
            onClickUnsetPrimary = {},
            onClickShare = {},
            onClickSetTheme = {},
            onClickDeleteTable = {},
        )
    }
}

@SnuttPreview
@Composable
private fun MoreActionSheet_NonPrimaryTable() {
    SnuttPreviewSurface {
        MoreActionSheet(
            tableSummary = TableSummary.Default.copy(
                title = "백업 시간표",
                isPrimary = false,
            ),
            onClickChangeTableName = {},
            onClickSetPrimary = {},
            onClickUnsetPrimary = {},
            onClickShare = {},
            onClickSetTheme = {},
            onClickDeleteTable = {},
        )
    }
}
