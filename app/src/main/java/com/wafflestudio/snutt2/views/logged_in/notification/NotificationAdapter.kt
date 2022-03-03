package com.wafflestudio.snutt2.views.logged_in.notification

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.res.stringResource
import androidx.recyclerview.widget.DiffUtil
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.ItemNotificationBinding
import com.wafflestudio.snutt2.lib.base.BaseAdapter
import com.wafflestudio.snutt2.lib.base.BaseViewHolder
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto

class NotificationAdapter(

): BaseAdapter<NotificationAdapter.Data>(diffCallback) {


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Data.Notification -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<out Data> {
        return when(viewType) {
            0 -> DataViewHolder(
                ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> throw IllegalStateException("no matching viewholder")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<out Data>, position: Int) {
        when(holder) {
            is DataViewHolder -> {
                holder.bindData(getItem(position) as Data.Notification)
            }
        }
    }

    inner class DataViewHolder(override val binding : ItemNotificationBinding) :
        BaseViewHolder<Data.Notification>(binding) {

        override fun bindData(data: Data.Notification) {
            val notification = data.notification

            val type = data.notification.type
            if(type==2) { // 임시 (각 type 별로 뭐가 '업데이트' 고 뭐가 '삭제' 고 뭐가 '추가' 인지 모름)
                binding.icon.setBackgroundResource(R.drawable.ic_refresh)
                binding.title.text = "업데이트" // R.string.notifications_noti_update 어떻게가져오지..
            }

            binding.date.text = notification.createdAt.substring(0, 10) // TODO : 'X시간 전' 으로 뜨는 건 직접 계산?
            binding.description.text = notification.message
        }
    }

    // sealed class 가 뭔지는 아직 잘 모름
    sealed class Data {
        class Notification(val notification: NotificationDto) : Data()
    }

    // 다른 곳에서 가져옴
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Data>() {
            override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
                return when (oldItem) {
                    is Data.Notification -> (newItem as? Data.Notification)?.notification?.id == oldItem.notification.id
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
                return oldItem == newItem
            }
        }
    }

}
