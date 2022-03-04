package com.wafflestudio.snutt2.views.logged_in.notification

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.res.stringResource
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.ItemNotificationBinding
import com.wafflestudio.snutt2.lib.base.BaseAdapter
import com.wafflestudio.snutt2.lib.base.BaseViewHolder
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto

class NotificationAdapter(

): PagingDataAdapter<String, NotificationAdapter.DataViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        return DataViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        )
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        getItem(position)?.let { holder.bindData(it) }
    }


    inner class DataViewHolder(val binding : ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

//        fun bindData(notification: NotificationDto) {
//
//            val type = notification.type
//            if(type==2) { // 임시 (각 type 별로 뭐가 '업데이트' 고 뭐가 '삭제' 고 뭐가 '추가' 인지 모름)
//                binding.icon.setBackgroundResource(R.drawable.ic_refresh)
//                binding.title.text = "업데이트" // R.string.notifications_noti_update 어떻게가져오지..
//            }
//
//            binding.date.text = notification.createdAt.substring(0, 10) // TODO : 'X시간 전' 으로 뜨는 건 직접 계산?
//            binding.description.text = notification.message
//        }
        fun bindData(notification: String) {
            binding.description.text = notification
        }

    }


//    companion object {
//        private val diffCallback = object : DiffUtil.ItemCallback<NotificationDto>() {
//            override fun areItemsTheSame(oldItem: NotificationDto, newItem: NotificationDto): Boolean {
//                return when (oldItem) {
//                    is NotificationDto -> (newItem as? NotificationDto)?.message == oldItem.message
//                    else -> false
//                }
//            }
//
//            override fun areContentsTheSame(oldItem: NotificationDto, newItem: NotificationDto): Boolean {
//                return oldItem == newItem
//            }
//        }
//    }
companion object {
    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return when (oldItem) {
                is String -> (newItem as? String) == oldItem
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}

}
