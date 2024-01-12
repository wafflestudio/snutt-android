package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTheme

@Composable
fun ThemeIcon(
    theme: TableTheme,
    modifier: Modifier = Modifier,
) {
    if (theme is CustomTheme) {
        Row(
            modifier = modifier,
        ) {
            val colors = theme.colors.map { Color(it.bgColor!!) }
            when (theme.colors.size) {
                1 -> {
                    Box(
                        modifier = Modifier
                            .background(colors[0])
                            .fillMaxSize(),
                    )
                }

                2 -> {
                    Box(
                        modifier = Modifier
                            .background(colors[0])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    Box(
                        modifier = Modifier
                            .background(colors[1])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }

                3 -> {
                    Box(
                        modifier = Modifier
                            .background(colors[0])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    Box(
                        modifier = Modifier
                            .background(colors[1])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    Box(
                        modifier = Modifier
                            .background(colors[2])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }

                4 -> {
                    Box(
                        modifier = Modifier
                            .background(colors[0])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    Column2Colors(colors[1], colors[2])
                    Box(
                        modifier = Modifier
                            .background(colors[3])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }

                5 -> {
                    Column2Colors(colors[0], colors[1])
                    Box(
                        modifier = Modifier
                            .background(colors[2])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    Column2Colors(colors[3], colors[4])
                }

                6 -> {
                    Box(
                        modifier = Modifier
                            .background(colors[0])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    Column2Colors(colors[1], colors[2])
                    Column2Colors(colors[3], colors[4])
                    Box(
                        modifier = Modifier
                            .background(colors[5])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                }

                7 -> {
                    Column2Colors(colors[0], colors[1])
                    Column3Colors(colors[2], colors[3], colors[4])
                    Column2Colors(colors[5], colors[6])
                }

                8 -> {
                    Column3Colors(colors[0], colors[1], colors[2])
                    Column2Colors(colors[3], colors[4])
                    Column3Colors(colors[5], colors[6], colors[7])
                }

                9 -> {
                    Box(
                        modifier = Modifier
                            .background(colors[0])
                            .weight(1f)
                            .fillMaxHeight(),
                    )
                    Column3Colors(colors[1], colors[2], colors[3])
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        Box(
                            modifier = Modifier
                                .background(colors[4])
                                .weight(3f)
                                .fillMaxWidth(),
                        )
                        Box(
                            modifier = Modifier
                                .background(colors[5])
                                .weight(5f)
                                .fillMaxWidth(),
                        )
                        Box(
                            modifier = Modifier
                                .background(colors[6])
                                .weight(6f)
                                .fillMaxWidth(),
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        Box(
                            modifier = Modifier
                                .background(colors[7])
                                .weight(7f)
                                .fillMaxWidth(),
                        )
                        Box(
                            modifier = Modifier
                                .background(colors[8])
                                .weight(8f)
                                .fillMaxWidth(),
                        )
                    }
                }
            }
        }
    } else {
        when ((theme as BuiltInTheme).code) {
            BuiltInTheme.SNUTT.code -> {
                Image(
                    painter = painterResource(R.drawable.theme_preview_snutt),
                    contentDescription = null,
                    modifier = modifier,
                )
            }
            BuiltInTheme.MODERN.code -> {
                Image(
                    painter = painterResource(R.drawable.theme_preview_modern),
                    contentDescription = null,
                    modifier = modifier,
                )
            }
            BuiltInTheme.AUTUMN.code -> {
                Image(
                    painter = painterResource(R.drawable.theme_preview_autumn),
                    contentDescription = null,
                    modifier = modifier,
                )
            }
            BuiltInTheme.CHERRY.code -> {
                Image(
                    painter = painterResource(R.drawable.theme_preview_pink),
                    contentDescription = null,
                    modifier = modifier,
                )
            }
            BuiltInTheme.ICE.code -> {
                Image(
                    painter = painterResource(R.drawable.theme_preview_ice),
                    contentDescription = null,
                    modifier = modifier,
                )
            }
            BuiltInTheme.GRASS.code -> {
                Image(
                    painter = painterResource(R.drawable.theme_preview_grass),
                    contentDescription = null,
                    modifier = modifier,
                )
            }
        }
    }
}

@Composable
private fun RowScope.Column2Colors(color1: Color, color2: Color) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
    ) {
        Box(
            modifier = Modifier
                .background(color1)
                .weight(1f)
                .fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .background(color2)
                .weight(1f)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun RowScope.Column3Colors(color1: Color, color2: Color, color3: Color) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
    ) {
        Box(
            modifier = Modifier
                .background(color1)
                .weight(1f)
                .fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .background(color2)
                .weight(1f)
                .fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .background(color3)
                .weight(1f)
                .fillMaxWidth(),
        )
    }
}
