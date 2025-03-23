package com.wafflestudio.snutt2.views.logged_in.table_lectures

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.RightArrowIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
private fun TableLectureAddNew(modifier: Modifier, onClickAdd: () -> Unit) {
    Column(
        modifier = modifier.clicks { onClickAdd.invoke() },
    ) {
        Row {
            Text(
                text = stringResource(R.string.lecture_list_add_button),
                style = SNUTTTypography.body1,
            )
            Spacer(modifier = Modifier.weight(1f))
            RightArrowIcon(modifier = Modifier.size(22.dp, 22.dp))
        }
    }
    Spacer(Modifier.height(20.dp))
}

@Preview(showBackground = true)
@Composable
fun TableLectureAddPreviewNew() {
    TableLectureAddNew(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
    ) {}
}
