package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BorderButton
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TutorialPage() {
    val navController = LocalNavController.current
    val pagerState = rememberPagerState()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HorizontalPager(modifier = Modifier.weight(1f), count = 3, state = pagerState) { page ->
            TutorialPage(
                modifier = Modifier.fillMaxWidth(),
                titleImg = painterResource(
                    id = when (page) {
                        0 -> R.drawable.imgintrotitle1
                        1 -> R.drawable.imgintrotitle2
                        else -> R.drawable.imgintrotitle3
                    }
                ),
                contentImg = painterResource(
                    id = when (page) {
                        0 -> R.drawable.imgintro1
                        1 -> R.drawable.imgintro2
                        else -> R.drawable.imgintro3
                    }
                )
            )
        }

        HorizontalPagerIndicator(
            modifier = Modifier
                .padding(30.dp)
                .align(Alignment.CenterHorizontally),
            pagerState = pagerState,
            indicatorHeight = 12.dp,
            indicatorWidth = 12.dp,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {

            Box(
                modifier = Modifier
                    .background(SNUTTColors.Gray100)
                    .fillMaxWidth()
                    .height(1.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                BorderButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = SNUTTColors.White900,
                    onClick = { navController.navigate(NavigationDestination.SignIn) }
                ) {
                    Text(
                        text = stringResource(R.string.tutorial_sign_in_button),
                        style = MaterialTheme.typography.button
                    )
                }

                Box(
                    modifier = Modifier
                        .background(SNUTTColors.Gray100)
                        .fillMaxHeight()
                        .width(1.dp)
                )

                BorderButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = SNUTTColors.White900,
                    onClick = { navController.navigate(NavigationDestination.SignUp) }
                ) {
                    Text(
                        text = stringResource(R.string.tutorial_sign_up_button),
                        style = MaterialTheme.typography.button
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialPagePreview() {
    TutorialPage()
}
