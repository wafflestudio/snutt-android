package com.wafflestudio.snutt2.views.logged_in.home.search

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.wafflestudio.snutt2.databinding.CellTagBinding
import com.wafflestudio.snutt2.lib.base.BaseAdapter
import com.wafflestudio.snutt2.lib.base.BaseViewHolder
import com.wafflestudio.snutt2.lib.color
import com.wafflestudio.snutt2.model.TagDto

class TagAdapter(
    private val onClickTag: (TagDto) -> Unit,
) : BaseAdapter<TagDto>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<out TagDto> {
        return ViewHolder(
            CellTagBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder<out TagDto>, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bindData(getItem(position))
            }
        }
    }

    inner class ViewHolder(override val binding: CellTagBinding) : BaseViewHolder<TagDto>(binding) {
        override fun bindData(data: TagDto) {
            binding.tagTitle.text = data.name
            (binding.root.background as? GradientDrawable)?.setColor(data.type.color())
            binding.root.setOnClickListener {
                onClickTag(data)
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<TagDto>() {
            override fun areItemsTheSame(oldItem: TagDto, newItem: TagDto): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: TagDto,
                newItem: TagDto
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
