package com.wafflestudio.snutt2.views.logged_in.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.wafflestudio.snutt2.databinding.ItemAddTableBinding
import com.wafflestudio.snutt2.databinding.ItemTableBinding
import com.wafflestudio.snutt2.lib.base.BaseAdapter
import com.wafflestudio.snutt2.lib.base.BaseViewHolder
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.RxBindable
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import io.reactivex.rxjava3.core.Observable

class TableListAdapter(
    private val onCreateItem: () -> Unit,
    private val onSelectItem: (table: TableDto) -> Unit,
    private val onShowMoreItem: (table: TableDto) -> Unit,
    private val onDuplicateItem: (table: TableDto) -> Unit,
    private val selectedTableId: Observable<String>,
    private val bindable: RxBindable
) : BaseAdapter<TableListAdapter.Data>(diffCallback) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Data.Table -> 0
            is Data.Add -> 1
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<out Data> {
        return when (viewType) {
            0 -> DataViewHolder(
                ItemTableBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            1 -> AddViewHolder(
                ItemAddTableBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalStateException("no matching viewholder")
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<out Data>,
        position: Int
    ) {
        when (holder) {
            is DataViewHolder -> {
                holder.bindData(getItem(position) as Data.Table)
            }
            is AddViewHolder -> {
                holder.bindData(getItem(position) as Data.Add)
            }
        }
    }

    inner class DataViewHolder(override val binding: ItemTableBinding) :
        BaseViewHolder<Data.Table>(binding) {

        override fun bindData(data: Data.Table) {
            val table = data.table

            binding.clickArea.throttledClicks()
                .bindUi(bindable) { onSelectItem(table) }

            binding.moreButton.throttledClicks()
                .bindUi(bindable) { onShowMoreItem(table) }

            binding.duplicateButton.throttledClicks()
                .bindUi(bindable) { onDuplicateItem(table) }

            binding.name.text = table.title

            selectedTableId
                .map { it == table.id }
                .bindUi(bindable) {
                    binding.credit.text = "(${table.totalCredit ?: 0L})"
                    binding.checkIcon.visibility = if (it) View.VISIBLE else View.INVISIBLE
                }
        }
    }

    inner class AddViewHolder(override val binding: ItemAddTableBinding) :
        BaseViewHolder<Data.Add>(binding) {

        override fun bindData(data: Data.Add) {
            binding.root.throttledClicks()
                .bindUi(bindable) { onCreateItem() }
        }
    }

    sealed class Data {
        class Table(val table: TableDto) : Data()

        object Add : Data()
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Data>() {
            override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
                return when (oldItem) {
                    is Data.Table -> (newItem as? Data.Table)?.table?.id == oldItem.table.id
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
                return oldItem == newItem
            }
        }
    }
}
