package com.wafflestudio.snutt2.views.logged_in.home.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.ItemTagBinding
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.base.BaseAdapter
import com.wafflestudio.snutt2.lib.base.BaseViewHolder
import com.wafflestudio.snutt2.model.TagDto

class TagSelectionAdapter(
    private val onClickTag: (TagDto) -> Unit,
) : BaseAdapter<Selectable<TagDto>>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<out Selectable<TagDto>> {
        return ViewHolder(
            ItemTagBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<out Selectable<TagDto>>, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bindData(getItem(position))
            }
        }
    }

    inner class ViewHolder(override val binding: ItemTagBinding) :
        BaseViewHolder<Selectable<TagDto>>(binding) {
        override fun bindData(data: Selectable<TagDto>) {
            val tag = data.item
            val context = itemView.context
            binding.checkIcon.setImageResource(if (data.state) R.drawable.ic_vivid_checked else R.drawable.ic_vivid_unchecked)
            binding.tagName.text = tag.name
            binding.root.setOnClickListener {
                onClickTag(tag)
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Selectable<TagDto>>() {
            override fun areItemsTheSame(
                oldItem: Selectable<TagDto>,
                newItem: Selectable<TagDto>
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Selectable<TagDto>,
                newItem: Selectable<TagDto>
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
