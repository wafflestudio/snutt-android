package com.wafflestudio.snutt2.lib.base

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.wafflestudio.snutt2.lib.rx.RxBinder

abstract class BaseAdapter<TData : Any>(
    diffCallback: DiffUtil.ItemCallback<TData>
) : ListAdapter<TData, BaseViewHolder<out TData>>(diffCallback), RxBinder
