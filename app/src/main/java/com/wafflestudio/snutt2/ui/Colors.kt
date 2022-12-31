package com.wafflestudio.snutt2.ui

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object SNUTTColors {
    val Colors.Gray100 @Composable get() = if (isLight) Color(0xfff2f2f2) else Color(0xff505050)
    val Gray100 @Composable get() = MaterialTheme.colors.Gray100

    val Colors.Gray200 @Composable get() = if (isLight) Color(0xffb3b3b3) else Color(0xffb3b3b3)
    val Gray200 @Composable get() = MaterialTheme.colors.Gray200

    val Colors.Gray400 @Composable get() = if (isLight) Color(0xfff2f2f2) else Color(0xff3d3d3d)
    val Gray400 @Composable get() = MaterialTheme.colors.Gray400

    val Colors.Gray600 @Composable get() = if (isLight) Color(0xff777777) else Color(0xffffffff)
    val Gray600 @Composable get() = MaterialTheme.colors.Gray600

    val Red = Color(0xffe54459)
    val Orange = Color(0xfff58d3d)
    val Grass = Color(0xffa6d930)
    val Sky = Color(0xff1bd0c8)
    val Blue = Color(0xff1d99e8)
    val Violet = Color(0xffaf56b3)

    val FacebookBlue = Color(0x993c5dd4)

    val Colors.SNUTTTheme @Composable get() = if (isLight) Color(0x991BD0C8) else Color(0xff58c1b7)
    val SNUTTTheme @Composable get() = MaterialTheme.colors.SNUTTTheme

    val Transparent = Color(0x00000000)

    val Colors.Black050 @Composable get() = if (isLight) Color(0x0d000000) else Color(0x0dffffff)
    val Black050 @Composable get() = MaterialTheme.colors.Black050

    val Colors.Black250 @Composable get() = if (isLight) Color(0x26000000) else Color(0x26ffffff)
    val Black250 @Composable get() = MaterialTheme.colors.Black250

    val Colors.Black300 @Composable get() = if (isLight) Color(0x4d000000) else Color(0x4dffffff)
    val Black300 @Composable get() = MaterialTheme.colors.Black300

    val Colors.Black400 @Composable get() = if (isLight) Color(0x66000000) else Color(0x66ffffff)
    val Black400 @Composable get() = MaterialTheme.colors.Black400

    val Colors.Black500 @Composable get() = if (isLight) Color(0x80000000) else Color(0x80ffffff)
    val Black500 @Composable get() = MaterialTheme.colors.Black500

    val Colors.Black600 @Composable get() = if (isLight) Color(0x99000000) else Color(0x99ffffff)
    val Black600 @Composable get() = MaterialTheme.colors.Black600

    val Colors.Black900 @Composable get() = if (isLight) Color(0xff000000) else Color(0xffffffff)
    val Black900 @Composable get() = MaterialTheme.colors.Black900

    val Colors.White400 @Composable get() = if (isLight) Color(0x4dffffff) else Color(0x4dffffff)
    val White400 @Composable get() = MaterialTheme.colors.White400

    val Colors.White500 @Composable get() = if (isLight) Color(0x80ffffff) else Color(0x80ffffff)
    val White500 @Composable get() = MaterialTheme.colors.White500

    val Colors.White700 @Composable get() = if (isLight) Color(0xb3ffffff) else Color(0xb3ffffff)
    val White700 @Composable get() = MaterialTheme.colors.White700

    val Colors.White800 @Composable get() = if (isLight) Color(0xccffffff) else Color(0xcc2b2b2b)
    val White800 @Composable get() = MaterialTheme.colors.White800

    val Colors.White900 @Composable get() = if (isLight) Color(0xffffffff) else Color(0xff2b2b2b)
    val White900 @Composable get() = MaterialTheme.colors.White900

    val Colors.Dim @Composable get() = if (isLight) Color(0x99000000) else Color(0x99000000)
    val Dim @Composable get() = MaterialTheme.colors.Dim

    val Colors.Dim2 @Composable get() = if (isLight) Color(0x80000000) else Color(0x60000000)
    val Dim2 @Composable get() = MaterialTheme.colors.Dim2

    val Colors.TableGrid @Composable get() = if (isLight) Color(0xffebebeb) else Color(0xff3c3c3c)
    val TableGrid @Composable get() = MaterialTheme.colors.TableGrid

    val Colors.TableGrid2 @Composable get() = if (isLight) Color(0xfff3f3f3) else Color(0xff3c3c3c)
    val TableGrid2 @Composable get() = MaterialTheme.colors.TableGrid2

    val DarkGray @Composable get() = MaterialTheme.colors.Gray600

    val Colors.AllWhite @Composable get() = Color(0xffffffff)
    val AllWhite @Composable get() = MaterialTheme.colors.AllWhite
}

val theme_snutt_0 = Color(0xffE54459)
// val theme_snutt_1 = Color(0xffF58D3D)
// val theme_snutt_2 = Color(0xffFAC42D)
// val theme_snutt_3 = Color(0xffA6D930)
// val theme_snutt_4 = Color(0xff2BC267)
// val theme_snutt_5 = Color(0xff1BD0C8)
// val theme_snutt_6 = Color(0xff1D99E8)
// val theme_snutt_7 = Color(0xff4F48C4)
// val theme_snutt_8 = Color(0xffAF56B3)
//
// val theme_autumn_0 = Color(0xffB82E31)
// val theme_autumn_1 = Color(0xffDB701C)
// val theme_autumn_2 = Color(0xffEAA32A)
// val theme_autumn_3 = Color(0xffC6C013)
// val theme_autumn_4 = Color(0xff3A856E)
// val theme_autumn_5 = Color(0xff19B2AC)
// val theme_autumn_6 = Color(0xff3994CE)
// val theme_autumn_7 = Color(0xff3F3A9C)
// val theme_autumn_8 = Color(0xff924396)
//
// val theme_modern_0 = Color(0xffF0652A)
// val theme_modern_1 = Color(0xffF5AD3E)
// val theme_modern_2 = Color(0xff998F36)
// val theme_modern_3 = Color(0xff89C291)
// val theme_modern_4 = Color(0xff266F55)
// val theme_modern_5 = Color(0xff13808F)
// val theme_modern_6 = Color(0xff366689)
// val theme_modern_7 = Color(0xff432920)
// val theme_modern_8 = Color(0xffD82F3D)
//
// val theme_cherry_0 = Color(0xffFD79A8)
// val theme_cherry_1 = Color(0xffFEC9DD)
// val theme_cherry_2 = Color(0xffFEB0CC)
// val theme_cherry_3 = Color(0xffFE93BF)
// val theme_cherry_4 = Color(0xffE9B1D0)
// val theme_cherry_5 = Color(0xffC67D97)
// val theme_cherry_6 = Color(0xffBB8EA7)
// val theme_cherry_7 = Color(0xffBDB4BF)
// val theme_cherry_8 = Color(0xffE16597)
//
// val theme_ice_0 = Color(0xffAABDCF)
// val theme_ice_1 = Color(0xffC0E9E8)
// val theme_ice_2 = Color(0xff66B6CA)
// val theme_ice_3 = Color(0xff015F95)
// val theme_ice_4 = Color(0xffA8D0DB)
// val theme_ice_5 = Color(0xff458ED0)
// val theme_ice_6 = Color(0xff62A9D1)
// val theme_ice_7 = Color(0xff20363D)
// val theme_ice_8 = Color(0xff6D8A96)
//
// val theme_grass_0 = Color(0xff4FBEAA)
// val theme_grass_1 = Color(0xff9FC1A4)
// val theme_grass_2 = Color(0xff5A8173)
// val theme_grass_3 = Color(0xff84AEB1)
// val theme_grass_4 = Color(0xff266F55)
// val theme_grass_5 = Color(0xffD0E0C4)
// val theme_grass_6 = Color(0xff59886D)
// val theme_grass_7 = Color(0xff476060)
// val theme_grass_8 = Color(0xff3D7068)
