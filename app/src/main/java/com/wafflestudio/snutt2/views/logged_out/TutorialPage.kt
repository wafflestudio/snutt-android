package com.wafflestudio.snutt2.views.logged_out

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BorderButton
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTheme
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import dagger.hilt.android.AndroidEntryPoint

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

@Composable
fun TutorialPage(modifier: Modifier = Modifier, titleImg: Painter, contentImg: Painter) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(Modifier.weight(3.5f)) {}

        Image(
            modifier = Modifier
                .width(250.dp)
                .height(40.dp),
            painter = titleImg,
            contentDescription = "tutorial title"
        )

        Surface(Modifier.weight(1.0f)) {}

        Image(
            modifier = Modifier
                .height(300.dp),
            painter = contentImg,
            contentDescription = "tutorial contents"
        )

        Surface(Modifier.weight(1.5f)) {}
    }
}

@Composable
fun ActionButtons(
    modifier: Modifier = Modifier,
    onClickSignIn: () -> Unit,
    onClickSignUp: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {

        Surface(
            modifier = Modifier
                .background(SNUTTColors.Gray200)
                .fillMaxWidth()
                .height(1.dp)
        ) {}

        Row(modifier = modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = SNUTTColors.White900,
                    contentColor = SNUTTColors.Black900
                ),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                onClick = onClickSignIn
            ) {
                Text(
                    text = "로그인",
                    style = MaterialTheme.typography.button
                )
            }

            Surface(
                modifier = Modifier
                    .background(SNUTTColors.Gray200)
                    .fillMaxHeight()
                    .width(1.dp)
            ) {}

            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = SNUTTColors.White900,
                    contentColor = SNUTTColors.Black900
                ),
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                onClick = onClickSignUp
            ) {
                Text(
                    text = "가입",
                    style = MaterialTheme.typography.button
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TutorialScreen(
    onClickSignIn: () -> Unit,
    onClickSignUp: () -> Unit
) {
    val pagerState = rememberPagerState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
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
            activeColor = SNUTTColors.Gray400,
            inactiveColor = SNUTTColors.Gray100,
            indicatorHeight = 12.dp,
            indicatorWidth = 12.dp,
        )

        ActionButtons(onClickSignIn = onClickSignIn, onClickSignUp = onClickSignUp)
    }
}

@AndroidEntryPoint
class TutorialFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SNUTTTheme {
                    TutorialScreen(
                        onClickSignUp = { routeSignUp() },
                        onClickSignIn = { routeSignIn() }
                    )
                }
            }
        }
    }

    private fun routeSignUp() {
        findNavController().navigate(
            R.id.action_tutorialFragment_to_signUpFragment
        )
    }

    private fun routeSignIn() {
        findNavController().navigate(
            R.id.action_tutorialFragment_to_loginFragment
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialPagePreview() {
    TutorialPage()
}
