package com.wafflestudio.snutt2.lib

import android.content.Context
import com.wafflestudio.snutt2.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnuttUrls @Inject constructor(@ApplicationContext private val context: Context) {

    fun getReviewMain(): String = context.getString(R.string.review_base_url) + "/main"

    fun getReviewDetail(detailId: String): String =
        context.getString(R.string.review_base_url) + "/detail/$detailId"
}
