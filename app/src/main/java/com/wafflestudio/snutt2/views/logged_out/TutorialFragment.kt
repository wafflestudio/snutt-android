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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.fragment.findNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.ui.*
import dagger.hilt.android.AndroidEntryPoint

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
fun ActionButtons(modifier: Modifier = Modifier, onClickSignIn: () -> Unit, onClickSignUp: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {

        Surface(
            modifier = Modifier
                .background(Gray200)
                .fillMaxWidth()
                .height(1.dp)
        ) {}

        Row(modifier = modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = White900,
                    contentColor = Black900
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
                    .background(Gray200)
                    .fillMaxHeight()
                    .width(1.dp)
            ) {}

            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = White900,
                    contentColor = Black900
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
            .background(White900)
    ) {
        HorizontalPager(modifier = Modifier.weight(1f), count = 3, state = pagerState) {
            TutorialPage(
                modifier = Modifier.fillMaxWidth(),
                titleImg = painterResource(id = R.drawable.imgintrotitle1),
                contentImg = painterResource(id = R.drawable.imgintro1)
            )
        }

        HorizontalPagerIndicator(
            modifier = Modifier
                .padding(30.dp)
                .align(Alignment.CenterHorizontally),
            pagerState = pagerState,
            activeColor = Gray400,
            inactiveColor = Gray100
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
                SnuttTheme {
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

@Preview
@Composable
fun TutorialScreenPreview() {
    SnuttTheme {
        TutorialScreen({}, {})
    }
}
