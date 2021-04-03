package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import com.wafflestudio.snutt2.adapter.NotificationAdapter
import com.wafflestudio.snutt2.listener.EndlessRecyclerViewScrollListener
import com.wafflestudio.snutt2.manager.NotiManager
import com.wafflestudio.snutt2.manager.NotiManager.OnNotificationReceivedListener
import com.wafflestudio.snutt2.model.Notification
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

/**
 * Created by makesource on 2016. 1. 16..
 */
class NotificationFragment : SNUTTBaseFragment(), OnNotificationReceivedListener {
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private var adapter: NotificationAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var placeholder: LinearLayout? = null
    private var layout: SwipeRefreshLayout? = null
    private var refreshListener: OnRefreshListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_notification, container, false)
        val recyclerView = rootView.findViewById<View>(R.id.notification_recyclerView) as RecyclerView
        placeholder = rootView.findViewById<View>(R.id.placeholder) as LinearLayout
        placeholder!!.visibility = if (NotiManager.instance!!.hasNotifications()) View.GONE else View.VISIBLE
        linearLayoutManager = LinearLayoutManager(context)
        adapter = NotificationAdapter(NotiManager.instance!!.getNotifications()!!)
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager!!) {
            override fun getFooterViewType(defaultNoFooterViewType: Int): Int {
                return NotificationAdapter.VIEW_TYPE.ProgressBar.value
            }

            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                loadNextDataFromApi(page, totalItemsCount)
                Log.d(TAG, "on Load More called. page : $page, totalItemsCount : $totalItemsCount")
            }
        }
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(scrollListener!!)
        recyclerView.layoutManager = linearLayoutManager
        layout = rootView.findViewById<View>(R.id.swipe_layout) as SwipeRefreshLayout
        refreshListener = OnRefreshListener {
            Log.d(TAG, "swipe refreshed called.")
            NotiManager.instance!!.refreshNotification(object : Callback<Any> {
                override fun success(o: Any?, response: Response) {
                    scrollListener!!.init()
                    layout!!.isRefreshing = false
                    adapter!!.notifyDataSetChanged()
                    NotiManager.instance!!.fetched = true
                    placeholder!!.visibility = if (NotiManager.instance!!.hasNotifications()) View.GONE else View.VISIBLE
                }

                override fun failure(error: RetrofitError) {}
            })
        }
        layout!!.setOnRefreshListener(refreshListener)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        if (!NotiManager.instance!!.fetched) {
            autoFetch(layout, refreshListener)
        }
        NotiManager.instance!!.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
        NotiManager.instance!!.removeListener(this)
    }

    override fun notifyNotificationReceived() {
        Log.d(TAG, "notify notification received")
        autoFetch(layout, refreshListener)
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    private fun loadNextDataFromApi(page: Int, totalItemsCount: Int) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        NotiManager.instance!!.addProgressBar()
        adapter!!.notifyDataSetChanged()
        NotiManager.instance!!.loadData(totalItemsCount, object : Callback<List<Notification>> {
            override fun success(notifications: List<Notification>?, response: Response) {
                adapter!!.notifyDataSetChanged()
            }

            override fun failure(error: RetrofitError) {}
        })
    }

    private fun autoFetch(layout: SwipeRefreshLayout?, refreshListener: OnRefreshListener?) {
        layout!!.post {
            layout.isRefreshing = true
            refreshListener!!.onRefresh()
        }
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"
        private const val TAG = "NOTIFICATION_FRAGMENT"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance(sectionNumber: Int): NotificationFragment {
            val fragment = NotificationFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}