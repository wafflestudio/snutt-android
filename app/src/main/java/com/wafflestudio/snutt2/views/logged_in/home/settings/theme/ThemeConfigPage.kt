package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AddIcon
import com.wafflestudio.snutt2.components.compose.BottomSheet
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingColumn
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ThemeConfigPage() {
    val navController = LocalNavController.current
    val bottomSheet = BottomSheet()
    val scope = rememberCoroutineScope()
    val themeList = listOf(
        stringResource(R.string.home_select_theme_snutt) to painterResource(R.drawable.theme_preview_snutt),
        stringResource(R.string.home_select_theme_modern) to painterResource(R.drawable.theme_preview_modern),
        stringResource(R.string.home_select_theme_autumn) to painterResource(R.drawable.theme_preview_autumn),
        stringResource(R.string.home_select_theme_pink) to painterResource(R.drawable.theme_preview_pink),
        stringResource(R.string.home_select_theme_ice) to painterResource(R.drawable.theme_preview_ice),
        stringResource(R.string.home_select_theme_grass) to painterResource(R.drawable.theme_preview_grass),
    )

    val onBackPressed: () -> Unit = {
        if (bottomSheet.isVisible) {
            scope.launch { bottomSheet.hide() }
        } else {
            navController.popBackStack()
        }
    }

    BackHandler {
        onBackPressed()
    }

    ModalBottomSheetLayout(
        sheetState = bottomSheet.state,
        sheetContent = bottomSheet.content,
        sheetShape = RoundedCornerShape(5.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            SimpleTopBar(
                title = "시간표 테마",
                onClickNavigateBack = {
                    onBackPressed()
                },
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
            ) {
                SettingColumn(
                    title = "커스텀 테마",
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.surface)
                            .padding(top = 20.dp, bottom = 12.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.Start,
                    ) {
                        AddThemeItem(
                            onClick = {
                                bottomSheet.setSheetContent {
                                    ThemeDetailPage(
                                        onClickCancel = {
                                            scope.launch { bottomSheet.hide() }
                                        },
                                        onClickSave = {
                                            scope.launch { bottomSheet.hide() }
                                        },
                                    )
                                }
                                scope.launch { bottomSheet.show() }
                            },
                        )
                    }
                }
                SettingColumn(
                    title = "제공 테마",
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.surface)
                            .padding(top = 20.dp, bottom = 12.dp)
                            .horizontalScroll(rememberScrollState()),
                    ) {
                        Spacer(modifier = Modifier.width(20.dp))
                        themeList.forEachIndexed { themeIdx, nameAndIdPair ->
                            ThemeItem(
                                name = nameAndIdPair.first,
                                painter = nameAndIdPair.second,
                                modifier = Modifier.clicks { },
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                        }
                    }
                }
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
        modifier = modifier
            .clicks { onClick() },
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
            )
        }
        Spacer(modifier.height(10.dp))
        Text(
            text = stringResource(R.string.theme_new),
            style = SNUTTTypography.body1,
        )
    }
}

@Composable
fun ThemeItem(
    name: String,
    painter: Painter,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Image(
            painter = painter, contentDescription = "", modifier = Modifier.size(80.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = name, textAlign = TextAlign.Center, style = SNUTTTypography.body1)
        }
    }
}
