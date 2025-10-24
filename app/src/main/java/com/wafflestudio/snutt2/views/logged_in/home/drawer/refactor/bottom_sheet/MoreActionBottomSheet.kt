package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor.bottom_sheet

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
import com.wafflestudio.snutt2.components.compose.PaletteIcon
import com.wafflestudio.snutt2.components.compose.PeopleIcon
import com.wafflestudio.snutt2.components.compose.PeopleOffIcon
import com.wafflestudio.snutt2.components.compose.ShareIcon
import com.wafflestudio.snutt2.components.compose.TrashIcon
import com.wafflestudio.snutt2.components.compose.WriteIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode

@Composable
fun MoreActionSheet(
    tableSummary: TableSummary,
    onClickChangeTableName: (tableSummary: TableSummary) -> Unit,
    onClickSetPrimary: (tableSummary: TableSummary) -> Unit,
    onClickUnsetPrimary: (tableSummary: TableSummary) -> Unit,
    onClickShareTable: (tableSummary: TableSummary) -> Unit,
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
            icon = { WriteIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.home_drawer_table_title_change),
            onClick = { onClickChangeTableName(tableSummary) },
        )
        MoreActionItem(
            icon = {
                if (tableSummary.isPrimary) {
                    PeopleOffIcon(
                        modifier = Modifier.size(30.dp),
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                } else {
                    PeopleIcon(
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
            text = stringResource(R.string.home_drawer_share_table_image),
        ) {
            onClickShareTable(tableSummary)
        }
        MoreActionItem(
            icon = { PaletteIcon(modifier = Modifier.size(30.dp)) },
            text = stringResource(R.string.home_drawer_table_theme_change),
        ) {
            onClickSetTheme(tableSummary)
        }
        MoreActionItem(
            icon = { TrashIcon(modifier = Modifier.size(30.dp)) },
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
