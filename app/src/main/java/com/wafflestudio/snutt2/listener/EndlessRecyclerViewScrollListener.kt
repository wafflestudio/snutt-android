package com.wafflestudio.snutt2.listener

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by makesource on 2017. 2. 27..
 */
abstract class EndlessRecyclerViewScrollListener(layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    //set visibleThreshold   default: 5
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    val visibleThreshold = 5

    // The current offset index of data you have loaded
    private var currentPage = 0

    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0

    // True if we are still waiting for the last set of data to load.
    private var loading = true

    //set startingPageIndex   default: 0
    // Sets the starting page index
    val startingPageIndex = 0

    // Sets the  footerViewType
    private val defaultNoFooterViewType = -1
    private var footerViewType = -1
    private val mTag = "scroll-listener"
    var mLayoutManager: RecyclerView.LayoutManager

    //init from  self-define
    fun init() {
        footerViewType = getFooterViewType(defaultNoFooterViewType)
        currentPage = 0
        previousTotalItemCount = 0
        loading = true
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {

        ////when dy=0---->list is clear totalItemCount == 0 or init load  previousTotalItemCount=0
        if (dy <= 0) return
        //        Log.i(mTag, "onScrolled-------dy:" + dy);
        val adapter = view.adapter
        val totalItemCount = adapter!!.itemCount
        val lastVisibleItemPosition = lastVisibleItemPosition
        val isAllowLoadMore = lastVisibleItemPosition + visibleThreshold > totalItemCount
        if (isAllowLoadMore) {
            if (isUseFooterView) {
                if (!isFooterView(adapter)) {
                    if (totalItemCount < previousTotalItemCount) { // swipe refresh reload result to change list size ,reset page index
                        currentPage = startingPageIndex
                    } else if (totalItemCount == previousTotalItemCount) { //if load failure or load empty data , we rollback  page index
                        currentPage = if (currentPage == startingPageIndex) startingPageIndex else --currentPage
                    }
                    loading = false
                }
            } else {
                if (totalItemCount > previousTotalItemCount) loading = false
            }
            if (!loading) {

                // If it isn’t currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                // threshold should reflect how many total columns there are too
                if (previousTotalItemCount == totalItemCount) return
                previousTotalItemCount = totalItemCount
                currentPage++
                onLoadMore(currentPage, totalItemCount)
                loading = true
                Log.i(mTag, "request pageindex:$currentPage,totalItemsCount:$totalItemCount")
            }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
    }

    val isUseFooterView: Boolean
        get() = footerViewType != defaultNoFooterViewType

    fun isFooterView(padapter: RecyclerView.Adapter<*>?): Boolean {
        var isFooterView = false
        val ptotalItemCount = padapter!!.itemCount
        if (ptotalItemCount > 0) {
            val lastPosition = ptotalItemCount - 1
            val lastViewType = padapter.getItemViewType(lastPosition)

            //  check the lastview is footview
            isFooterView = lastViewType == footerViewType
        }
        Log.i(mTag, "isFooterView:$isFooterView")
        return isFooterView
    }

    private val lastVisibleItemPosition: Int
        private get() {
            var lastVisibleItemPosition = 0
            lastVisibleItemPosition = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            return lastVisibleItemPosition
        }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    // set FooterView type
    // if don't use footview load more  default: -1
    abstract fun getFooterViewType(defaultNoFooterViewType: Int): Int

    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore(page: Int, totalItemsCount: Int)

    init {
        init()
        mLayoutManager = layoutManager
    }
}