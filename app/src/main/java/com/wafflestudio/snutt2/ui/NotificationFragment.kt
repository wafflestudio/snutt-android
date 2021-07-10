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
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.listener.EndlessRecyclerViewScrollListener
import com.wafflestudio.snutt2.manager.NotificationsRepository
import com.wafflestudio.snutt2.manager.NotificationsRepository.OnNotificationReceivedListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Created by makesource on 2016. 1. 16..
 */
@AndroidEntryPoint
class NotificationFragment : SNUTTBaseFragment(), OnNotificationReceivedListener {

    @Inject
    lateinit var notificationsRepository: NotificationsRepository

    @Inject
    lateinit var apiOnError: ApiOnError

    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    private var adapter: NotificationAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var placeholder: LinearLayout? = null
    private var layout: SwipeRefreshLayout? = null
    private var refreshListener: OnRefreshListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_notification, container, false)
        val recyclerView = rootView.findViewById<View>(R.id.notification_recyclerView) as RecyclerView
        placeholder = rootView.findViewById<View>(R.id.placeholder) as LinearLayout
        placeholder!!.visibility = if (notificationsRepository.hasNotifications()) View.GONE else View.VISIBLE
        linearLayoutManager = LinearLayoutManager(context)
        adapter = NotificationAdapter(notificationsRepository.getNotifications())
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
            notificationsRepository.refreshNotification()
                .bindUi(
                    this,
                    onSuccess = {
                        scrollListener!!.init()
                        layout!!.isRefreshing = false
                        adapter!!.notifyDataSetChanged()
                        notificationsRepository.fetched = true
                        placeholder!!.visibility = if (notificationsRepository.hasNotifications()) View.GONE else View.VISIBLE
                    },
                    onError = {
                        apiOnError(it)
                    }
                )
        }
        layout!!.setOnRefreshListener(refreshListener)
        return rootView
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")
        if (!notificationsRepository.fetched) {
            autoFetch(layout, refreshListener)
        }
        notificationsRepository.addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")
        notificationsRepository.removeListener(this)
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
//        Refactoring FIXME: dirty progress
//        notiManager.addProgressBar()
        notificationsRepository.loadData(totalItemsCount)
            .bindUi(
                this,
                onSuccess = {
                    adapter!!.notifyDataSetChanged()
                },
                onError = {
                    apiOnError(it)
                }
            )
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
