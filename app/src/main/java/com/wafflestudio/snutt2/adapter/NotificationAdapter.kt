package com.wafflestudio.snutt2.adapter

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by makesource on 2017. 2. 27..
 */
class NotificationAdapter(private val lists: List<NotificationDto?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    enum class VIEW_TYPE(val value: Int) {
        Notification(0), ProgressBar(1);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE.Notification.value) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_notification, parent, false)
            NotificationViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.cell_progressbar, parent, false)
            ProgressBarViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemType = getItemViewType(position)
        if (itemType == VIEW_TYPE.Notification.value) {
            (holder as NotificationViewHolder).bindData(getItem(position)!!)
        }
    }

    fun getItem(position: Int): NotificationDto? {
        return lists[position]
    }

    override fun getItemViewType(position: Int): Int {
        val item = lists[position]
        return if (item == null) VIEW_TYPE.ProgressBar.value else VIEW_TYPE.Notification.value
    }

    override fun getItemCount(): Int {
        // Log.d(TAG, "notification list size : " + lists.size());
        return lists.size
    }

    // inner class to hold a reference to each item of RecyclerView
    class NotificationViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val message: TextView
        private val image: ImageView
        fun bindData(notification: NotificationDto) {
            // Log.d(TAG, "notification message : " + notification.getMessage());
            var text = notification.message
            when (notification.type) {
                0 -> image.setImageResource(R.drawable.noticewarning)
                1 -> image.setImageResource(R.drawable.noticetimetable)
                2 -> image.setImageResource(R.drawable.noticeupdate)
                3 -> image.setImageResource(R.drawable.noticetrash)
                else -> Log.e(TAG, "notification type is out of bound!!")
            }
            try {
                val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                format.timeZone = TimeZone.getTimeZone("UTC")
                val date1 = format.parse(notification.createdAt)
                val date2 = Date()
                val diff = date2.time - date1.time
                val hours = diff / (1000 * 60 * 60)
                val days = hours / 24
                val months = days / 30
                val years = months / 12
                text += " "
                text += if (years > 0) {
                    "<font color='#808080'>" + years + "년 전</font>"
                } else if (months > 0) {
                    "<font color='#808080'>" + months + "달 전</font>"
                } else if (days > 0) {
                    "<font color='#808080'>" + days + "일 전</font>"
                } else if (hours > 0) {
                    "<font color='#808080'>" + hours + "시간 전</font>"
                } else {
                    "<font color='#808080'>방금</font>"
                }
            } catch (e: ParseException) {
                Log.e(TAG, "notification created time parse error!")
                e.printStackTrace()
            }
            message.text = Html.fromHtml(text)
        }

        init {
            image = view.findViewById<View>(R.id.notification_image) as ImageView
            message = view.findViewById<View>(R.id.notification_text) as TextView
        }
    }

    class ProgressBarViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        private val progressBar: ProgressBar

        init {
            progressBar = view.findViewById<View>(R.id.progressBar) as ProgressBar
        }
    }

    companion object {
        private const val TAG = "NOTIFICATION_ADAPTER"
    }
}
