package com.wafflestudio.snutt2.lib.network

interface ApiOnProgress {

    var progressShowing: Boolean
    fun showProgress(title: String? = null)
    fun hideProgress()
}
