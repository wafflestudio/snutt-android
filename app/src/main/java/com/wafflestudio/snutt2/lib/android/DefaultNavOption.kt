package com.wafflestudio.snutt2.lib.android

import androidx.navigation.NavOptions
import com.wafflestudio.snutt2.R

val defaultNavOptions = NavOptions.Builder()
    .setEnterAnim(R.anim.slide_in)
    .setExitAnim(R.anim.fade_out)
    .setPopExitAnim(R.anim.slide_out)
    .setPopEnterAnim(R.anim.fade_in)
    .build()
