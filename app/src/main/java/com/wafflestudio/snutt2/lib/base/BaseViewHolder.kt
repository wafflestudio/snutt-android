package com.wafflestudio.snutt2.lib.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<R : Any>(open val binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    abstract fun bindData(data: R)
}
