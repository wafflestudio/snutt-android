package com.wafflestudio.snutt2.core.network

interface ApiOnProgress {

    var progressShowing: Boolean
    fun showProgress(title: String? = null)
    fun hideProgress()
}
