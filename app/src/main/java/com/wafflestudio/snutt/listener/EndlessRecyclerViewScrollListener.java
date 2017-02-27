package com.wafflestudio.snutt.listener;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by makesource on 2017. 2. 27..
 */

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;

    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    // Sets the  footerViewType
    private int defaultNoFooterViewType = -1;
    private int footerViewType = -1;


    private String mTag = "scroll-listener";


    RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        init();
        this.mLayoutManager = layoutManager;
    }


    //init from  self-define
    private void init() {
        footerViewType = getFooterViewType(defaultNoFooterViewType);
        startingPageIndex = getStartingPageIndex();

        int threshold = getVisibleThreshold();
        if (threshold > visibleThreshold) {
            visibleThreshold = threshold;
        }
    }


    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(final RecyclerView view, int dx, int dy) {

        ////when dy=0---->list is clear totalItemCount == 0 or init load  previousTotalItemCount=0
        if (dy <= 0) return;
//        Log.i(mTag, "onScrolled-------dy:" + dy);

        RecyclerView.Adapter adapter = view.getAdapter();
        int totalItemCount = adapter.getItemCount();

        int lastVisibleItemPosition = getLastVisibleItemPosition();

        boolean isAllowLoadMore = (lastVisibleItemPosition + visibleThreshold) > totalItemCount;

        if (isAllowLoadMore) {

            if (isUseFooterView()) {
                if (!isFooterView(adapter)) {
                    if (totalItemCount < previousTotalItemCount) { // swipe refresh reload result to change list size ,reset page index
                        this.currentPage = this.startingPageIndex;
//                            Log.i(mTag, "****totalItemCount:" + totalItemCount + ",previousTotalItemCount:" + previousTotalItemCount + ",currentpage=startingPageIndex");
                    } else if (totalItemCount == previousTotalItemCount) { //if load failure or load empty data , we rollback  page index
                        currentPage = currentPage == startingPageIndex ? startingPageIndex : --currentPage;
//                            Log.i(mTag, "!!!!currentpage:" + currentPage);
                    }

                    loading = false;
                }
            } else {
                if (totalItemCount > previousTotalItemCount) loading = false;
            }

            if (!loading) {

                // If it isn’t currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                // threshold should reflect how many total columns there are too

                previousTotalItemCount = totalItemCount;
                currentPage++;
                onLoadMore(currentPage, totalItemCount);
                loading = true;
                Log.i(mTag, "request pageindex:" + currentPage + ",totalItemsCount:" + totalItemCount);

            }
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

    }


    public boolean isUseFooterView() {
        boolean isUse = footerViewType != defaultNoFooterViewType;
//        Log.i(mTag, "isUseFooterView:" + isUse);
        return isUse;
    }


    public boolean isFooterView(RecyclerView.Adapter padapter) {

        boolean isFooterView = false;
        int ptotalItemCount = padapter.getItemCount();

        if (ptotalItemCount > 0) {

            int lastPosition = ptotalItemCount - 1;
            int lastViewType = padapter.getItemViewType(lastPosition);

            //  check the lastview is footview
            isFooterView = lastViewType == footerViewType;
        }
//        Log.i(mTag, "isFooterView:" + isFooterView);

        return isFooterView;
    }

    private int getLastVisibleItemPosition() {
        int lastVisibleItemPosition = 0;
        lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
        return lastVisibleItemPosition;
    }


    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }


    // set FooterView type
    // if don't use footview load more  default: -1
    public abstract int getFooterViewType(int defaultNoFooterViewType);

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);

    //set visibleThreshold   default: 5
    public int getVisibleThreshold() {
        return visibleThreshold;
    }

    //set startingPageIndex   default: 0
    public int getStartingPageIndex() {
        return startingPageIndex;
    }

}