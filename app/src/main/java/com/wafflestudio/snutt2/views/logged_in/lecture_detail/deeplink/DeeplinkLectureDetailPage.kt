package com.wafflestudio.snutt2.views.logged_in.lecture_detail.deeplink

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailPage
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import kotlinx.coroutines.launch

@Composable
fun DeeplinkLectureDetailPage(
    lectureDetailViewModel: LectureDetailViewModel,
    tableListViewModel: TableListViewModel,
) {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val tableId = navController.currentBackStackEntry?.arguments?.getString("tableId")

    Box(modifier = Modifier.fillMaxSize()) {
        LectureDetailPage(
            lectureDetailViewModel,
            onCloseViewMode = {
                navController.popBackStack()
            },
        )
        if (tableId != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 15.dp)
                    .background(shape = RoundedCornerShape(30.dp), color = SNUTTColors.Gray20)
                    .size(width = 150.dp, height = 40.dp)
                    .clicks {
                        scope.launch {
                            tableListViewModel.changeSelectedTable(tableId)
                            navController.navigate(NavigationDestination.Home) {
                                popUpTo(NavigationDestination.Home) {
                                    inclusive = false
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "해당 시간표로 이동", style = SNUTTTypography.body1)
            }
        }
    }
}
