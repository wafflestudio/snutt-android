package com.wafflestudio.snutt2.views.logged_in.vacancy_noti

import android.content.Intent
import android.net.Uri
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ArrowBackIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.WebViewStyleButton
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTColors.SNUTTTheme
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
    val deleteEnabled by remember {
        derivedStateOf { vacancyViewModel.isEditMode && selectedLectures.size > 0 }
    }
    val density = LocalDensity.current

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
                        text = if (!vacancyViewModel.isEditMode) stringResource(R.string.vacancy_app_bar_edit) else stringResource(R.string.vacancy_app_bar_cancel),
                        style = SNUTTTypography.body1,
                        modifier = Modifier
                            .clicks {
                                scope.launch {
                                    launchSuspendApi(apiOnProgress, apiOnError) {
                                        vacancyViewModel.toggleEditMode()
                                    }
                                }
                            }
                    )
                }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (!vacancyViewModel.isEditMode)
                            Modifier.pullRefresh(pullRefreshState)
                        else
                            Modifier
                    )
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = vacancyLectures,
                        key = { it.item.id }
                    ) {
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
            AnimatedVisibility (
                visible = vacancyViewModel.isEditMode,
                modifier = Modifier
                    .fillMaxWidth(),
                enter = slideInVertically {
                    with(density) { 60.dp.roundToPx() }
                },
                exit = slideOutVertically {
                    with(density) { 60.dp.roundToPx() }
                }
            ) {
                WebViewStyleButton(
                    onClick = {
                        scope.launch {
                            launchSuspendApi(apiOnProgress, apiOnError) {
                                vacancyViewModel.deleteSelectedLectures()
                                vacancyViewModel.getVacancyLectures()
                                vacancyViewModel.toggleEditMode()
                            }
                        }
                    },
                    enabled = deleteEnabled,
                    disabledColor = Color(0xFFC4C4C4)       //TODO: 다크모드 색상?
                ) {
                    Text(
                        text = stringResource(R.string.vacancy_delete_selected),
                        style = SNUTTTypography.h3.copy(
                            color = SNUTTColors.AllWhite,
                        )
                    )
                }
            }
        }
        AnimatedVisibility (
            visible = !vacancyViewModel.isEditMode,
            modifier = Modifier
                .align(Alignment.BottomEnd),
            enter = slideInVertically {
                with(density) { 10.dp.roundToPx() }
            } + fadeIn(),
            exit = slideOutVertically {
                with(density) { 10.dp.roundToPx() }
            } + fadeOut()
        ) {
            ExtendedFloatingActionButton(
                modifier = Modifier.padding(30.dp),
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
                },
                elevation = FloatingActionButtonDefaults.elevation(3.dp, 3.dp)
            )
        }
    }
}
