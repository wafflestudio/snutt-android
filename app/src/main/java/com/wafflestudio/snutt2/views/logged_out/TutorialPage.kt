package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.wafflestudio.snutt2.R

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TutorialPage(
    onClickSignIn: () -> Unit,
    onClickSignUp: () -> Unit
) {
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                modifier = Modifier
                    .weight(1f),
                onClick = onClickSignIn
            ) {
                Text(text = "로그인")
            }

            Button(
                modifier = Modifier.weight(1f),
                onClick = onClickSignUp
            ) {
                Text(text = "가입")
            }
        }
    }
}

@Preview
@Composable
fun TutorialPagePreview() {
    TutorialPage(
        onClickSignIn = {},
        onClickSignUp = {},
    )
}
