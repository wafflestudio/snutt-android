package com.wafflestudio.snutt2.views.logged_in.home.drawer

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

/**
 * 논의
 * 특정 domain model 을 UI 로 사상하는 Composable 의 경우만 별도 파일 분리를 하면 어떨까 생각했어
 * 도메인 모델이랑 딱히 상관 없는 순수 UI 조각은, 사용되는 파일에서 private fun 으로 두고.
 */
@Composable
fun CourseBookDrawerItem(
    tableSummary: TableSummary,
    isSelectedTable: Boolean,
    onSelectTable: (tableId: String) -> Unit,
    onClickCopyIcon: (tableId: String) -> Unit,
    onClickMoreIcon: (tableSummary: TableSummary) -> Unit,
) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clicks {
                    onSelectTable(tableSummary.id)
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
                text = tableSummary.title,
                style = SNUTTTypography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(
                    R.string.home_drawer_table_credit, tableSummary.totalCredit ?: 0L,
                ),
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Black300),
                maxLines = 1,
            )
            if (tableSummary.isPrimary) {
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
                    onClickCopyIcon(tableSummary.id)
                },
            colorFilter = ColorFilter.tint(SNUTTColors.Black500),
        )
        Spacer(modifier = Modifier.width(10.dp))
        MoreIcon(
            modifier = Modifier
                .size(30.dp)
                .clicks {
                    onClickMoreIcon(tableSummary)
                },
            colorFilter = ColorFilter.tint(SNUTTColors.Black500),
        )
    }
}
