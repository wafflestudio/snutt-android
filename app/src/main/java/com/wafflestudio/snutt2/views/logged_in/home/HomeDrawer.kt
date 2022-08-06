package com.wafflestudio.snutt2.views.logged_in.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.toFormattedString

@Composable
fun HomeDrawer(
    selectedCourseBook: CourseBookDto,
    tableListOfSelectedCourseBook: List<SimpleTableDto>,
    onClickTableItem: (String) -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(20.dp)) {
        Text(text = selectedCourseBook.toFormattedString(context))
        Spacer(modifier = Modifier.height(10.dp))
        Column(Modifier.verticalScroll(rememberScrollState())) {
            tableListOfSelectedCourseBook.forEach {
                TableItem(tableDto = it, onClick = onClickTableItem)
            }
        }
    }
}

@Composable
private fun TableItem(tableDto: SimpleTableDto, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .clicks { onClick(tableDto.id) }
            .padding(10.dp)
    ) {
        Text(text = tableDto.title)
        Text(text = tableDto.totalCredit.toString())
    }
}

@Preview
@Composable
fun HomeDrawerPreview() {
}
