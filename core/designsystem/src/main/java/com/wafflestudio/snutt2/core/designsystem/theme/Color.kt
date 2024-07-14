package com.wafflestudio.snutt2.core.designsystem.theme

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Colors.Gray200 @Composable get() = if (isLight) Color(0xffb3b3b3) else Color(0xffb3b3b3)
val Gray200 @Composable get() = MaterialTheme.colors.Gray200

val Colors.Black900 @Composable get() = if (isLight) Color(0xff000000) else Color(0xffffffff)
val Black900 @Composable get() = MaterialTheme.colors.Black900

private val Colors.White900 @Composable get() = if (isLight) Color(0xffffffff) else Color(0xff2b2b2b)
val White900 @Composable get() = MaterialTheme.colors.White900

private val Colors.TableGrid @Composable get() = if (isLight) Color(0xffebebeb) else Color(0xff3c3c3c)
val TableGrid @Composable get() = MaterialTheme.colors.TableGrid