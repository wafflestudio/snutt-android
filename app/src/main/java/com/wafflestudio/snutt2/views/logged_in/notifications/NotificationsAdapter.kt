package com.wafflestudio.snutt2.views.logged_in.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.ItemNotificationBinding
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class NotificationsAdapter : PagingDataAdapter<NotificationDto, NotificationsAdapter.ViewHolder>(
    diffCallback
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val binding = holder.binding
        val context = holder.itemView.context

        if (item == null) return

        when (item.type) {
            0 -> {
                binding.icon.setImageResource(R.drawable.ic_warning)
                binding.title.text = context.getString(R.string.notifications_noti_warning)
            }
            1 -> {
                binding.icon.setImageResource(R.drawable.ic_calendar)
                binding.title.text = context.getString(R.string.notifications_noti_add)
            }
            2 -> {
                binding.icon.setImageResource(R.drawable.ic_refresh)
                binding.title.text = context.getString(R.string.notifications_noti_update)
            }
            3 -> {
                binding.icon.setImageResource(R.drawable.ic_trash)
                binding.title.text = context.getString(R.string.notifications_noti_delete)
            }
            else -> Timber.e("notification type is out of bound!!")
        }

        binding.description.text = item.message
        binding.date.text = try {
            val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            format.timeZone = TimeZone.getTimeZone("UTC")
            val date1 = format.parse(item.createdAt) ?: Date()
            val date2 = Date()

            val diff = date2.time - date1.time
            val hours = diff / (1000 * 60 * 60)
            val days = hours / 24
            when {
                days > 0 -> {
                    DateFormat.getDateInstance().format(date1)
                }
                hours > 0 -> {
                    "$hours 시간 전"
                }
                else -> {
                    "방금"
                }
            }
        } catch (e: ParseException) {
            Timber.e("notification created time parse error!")
            "-"
        }

    }

    class ViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<NotificationDto>() {
            override fun areItemsTheSame(
                oldItem: NotificationDto,
                newItem: NotificationDto
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: NotificationDto,
                newItem: NotificationDto
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
