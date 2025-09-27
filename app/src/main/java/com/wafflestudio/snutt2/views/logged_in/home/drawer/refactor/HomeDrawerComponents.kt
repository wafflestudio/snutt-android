package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BigPeopleIcon
import com.wafflestudio.snutt2.components.compose.DuplicateIcon
import com.wafflestudio.snutt2.components.compose.MoreIcon
import com.wafflestudio.snutt2.components.compose.VividCheckedIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.domainmodel.TableSummary
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun CourseBookDrawerItem(
    table: TableSummary,
    isSelectedTable: Boolean,
) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clicks {
                    // TODO: 시간표 선택
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            VividCheckedIcon(
                modifier = Modifier
                    .size(15.dp)
                    .alpha(if (isSelectedTable) 1f else 0f),
            )
            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = table.title,
                style = SNUTTTypography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(
                    R.string.home_drawer_table_credit, table.totalCredit ?: 0L,
                ),
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Black300),
                maxLines = 1,
            )
            if (table.isPrimary) {
                BigPeopleIcon(
                    modifier = Modifier.size(15.dp),
                    isSelected = true,
                    colorFilter = ColorFilter.tint(SNUTTColors.SNUTTTheme),
                )
            }
        }
        DuplicateIcon(
            modifier = Modifier
                .size(30.dp)
                .clicks {
                    // TODO: 시간표 복사
                },
            colorFilter = ColorFilter.tint(SNUTTColors.Black500),
        )
        Spacer(modifier = Modifier.width(10.dp))
        MoreIcon(
            modifier = Modifier
                .size(30.dp)
                .clicks {
                    // TODO: 더보기 버튼
                },
            colorFilter = ColorFilter.tint(SNUTTColors.Black500),
        )
    }
}

@Composable
fun CreateTableItem(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .clicks { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(25.dp))
        Text(
            text = stringResource(R.string.home_drawer_timetable_add_button),
            style = SNUTTTypography.body1,
        )
    }
}
