package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController

@Composable
fun VacancyPage() {
    val navController = LocalNavController.current
    val vacancyViewModel: VacancyViewModel = hiltViewModel()
    val vacancyPagingItems by vacancyViewModel.vacancyLectures.collectAsState()

    Column(modifier = Modifier.background(SNUTTColors.White900)) {
        TopBar(
            title = {
                Text(
                    text = stringResource(R.string.vacancy_app_bar_title),
                    style = SNUTTTypography.h2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                ArrowBackIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { navController.popBackStack() },
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                )
            },
            actions = {
                Text(
                    text = stringResource(R.string.vacancy_app_bar_edit),
                    style = SNUTTTypography.body1,
                    modifier = Modifier
                        .clicks { }
                )
            }
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(vacancyPagingItems) {
                VacancyListItem(it)
            }
        }
    }
}
