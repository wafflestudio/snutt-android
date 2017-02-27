package com.wafflestudio.snutt.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wafflestudio.snutt.R;
import com.wafflestudio.snutt.SNUTTBaseFragment;
import com.wafflestudio.snutt.adapter.NotificationAdapter;
import com.wafflestudio.snutt.listener.EndlessRecyclerViewScrollListener;
import com.wafflestudio.snutt.manager.NotiManager;
import com.wafflestudio.snutt.model.Notification;
import com.wafflestudio.snutt.view.DividerItemDecoration;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by makesource on 2016. 1. 16..
 */
public class NotificationFragment extends SNUTTBaseFragment { /**
 * The fragment argument representing the section number for this
 * fragment.
 */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG = "NOTIFICATION_FRAGMENT";
    private EndlessRecyclerViewScrollListener scrollListener;
    private NotificationAdapter adapter;

    public NotificationFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static NotificationFragment newInstance(int sectionNumber) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.notification_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new NotificationAdapter(NotiManager.getInstance().getNotifications());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {
                return NotificationAdapter.VIEW_TYPE.ProgressBar.getValue();
            }
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                NotiManager.getInstance().addProgressBar();
                loadNextDataFromApi(page, totalItemsCount);
                adapter.notifyDataSetChanged();
                Log.d(TAG, "on Load More called. page : " + page + ", totalItemsCount : " + totalItemsCount);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);

        final SwipeRefreshLayout layout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_layout);
        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "swipe refreshed called.");
                NotiManager.getInstance().refreshNotification(new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        layout.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                    }
                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }
        });
        return rootView;
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int page, final int totalItemsCount) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        NotiManager.getInstance().loadData(totalItemsCount, new Callback<List<Notification>>(){
            @Override
            public void success(List<Notification> notifications, Response response) {
                adapter.notifyDataSetChanged();
            }
            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}
