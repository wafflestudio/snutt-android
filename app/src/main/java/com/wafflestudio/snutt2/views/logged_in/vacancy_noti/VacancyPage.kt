package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import android.content.Intent
import android.net.Uri
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
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
import com.wafflestudio.snutt2.views.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VacancyPage() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val scope = rememberCoroutineScope()
    val vacancyViewModel: VacancyViewModel = hiltViewModel()
    val vacancyLectures by vacancyViewModel.vacancyLectures.collectAsState()
    val isRefreshing by vacancyViewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullRefreshState(isRefreshing, { vacancyViewModel.refreshVacancyLectures() })
    val selectedLectures = vacancyViewModel.selectedLectures

    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (vacancyViewModel.isEditMode) {
                    vacancyViewModel.toggleEditMode()
                } else {
                    if (navController.currentDestination?.route == NavigationDestination.VacancyNotification) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onBackPressedDispatcherOwner?.onBackPressedDispatcher?.addCallback(onBackPressedCallback)
        onDispose { onBackPressedCallback.remove() }
    }

    LaunchedEffect(Unit) {
        launchSuspendApi(apiOnProgress, apiOnError) {
            vacancyViewModel.getVacancyLectures()
        }
    }

    Box(
        modifier = Modifier.background(SNUTTColors.White900)
    ) {
        Column {
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
                        text = if (!vacancyViewModel.isEditMode) stringResource(R.string.vacancy_app_bar_edit) else "삭제",
                        style = SNUTTTypography.body1,
                        modifier = Modifier
                            .clicks {
                                scope.launch {
                                    launchSuspendApi(apiOnProgress, apiOnError) {
                                        if (vacancyViewModel.isEditMode) {
                                            vacancyViewModel.deleteSelectedLectures()
                                            vacancyViewModel.getVacancyLectures()
                                        }
                                        vacancyViewModel.toggleEditMode()
                                    }
                                }
                            }
                    )
                }
            )
            Box(
                modifier = Modifier.pullRefresh(pullRefreshState)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(vacancyLectures) {
                        val lectureId = it.item.id
                        VacancyListItem(
                            lectureDataWithVacancy = it,
                            editing = vacancyViewModel.isEditMode,
                            checked = selectedLectures.contains(lectureId),
                            onCheckedChange = { vacancyViewModel.toggleLectureSelected(lectureId) }
                        )
                    }
                }
                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
        ExtendedFloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 30.dp, bottom = 30.dp),
            text = {
                Text(
                    text = stringResource(R.string.vacancy_floating_button),
                    style = SNUTTTypography.h4.copy(color = SNUTTColors.AllWhite)
                )
            },
            contentColor = SNUTTColors.SNUTTTheme,
            onClick = {
                val sugangSnuUrl = "https://sugang.snu.ac.kr/sugang/ca/ca102.action?workType=F"
                val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(sugangSnuUrl))
                context.startActivity(intent)
            }
        )
    }
}
