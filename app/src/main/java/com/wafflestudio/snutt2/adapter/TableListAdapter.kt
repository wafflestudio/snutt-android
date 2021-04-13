package com.wafflestudio.snutt2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.manager.PrefManager
import com.wafflestudio.snutt2.model.Table

/**
 * Created by makesource on 2016. 1. 20..
 */
class TableListAdapter(private val tables: List<Table>) : RecyclerView.Adapter<TableListAdapter.ViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cellLayoutView: View
        val viewHolder: ViewHolder
        when (viewType) {
            TYPE_TABLE_SECTION -> {
                // create a new view
                cellLayoutView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.cell_table_section, parent, false)
                // create ViewHolder
                viewHolder = ViewHolder(cellLayoutView)
                return viewHolder
            }
            TYPE_TABLE_CELL -> {
                cellLayoutView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.cell_table, parent, false)
                // create ViewHolder
                viewHolder = ViewHolder(cellLayoutView)
                return viewHolder
            }
        }
        throw IllegalStateException("WTF")
    }

    override fun getItemViewType(position: Int): Int {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        val id = tables[position].id
        return if (id == TABLE_SECTION_STRING) {
            TYPE_TABLE_SECTION
        } else {
            TYPE_TABLE_CELL
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val table = tables[position]
        when (holder!!.itemViewType) {
            TYPE_TABLE_SECTION -> holder.tableSectionName.text = table.title
            TYPE_TABLE_CELL -> {
                holder.tableName.text = table.title
                if (PrefManager.instance!!.lastViewTableId.equals(table.id)) {
                    holder.checked.visibility = View.VISIBLE
                } else {
                    holder.checked.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return tables.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tableSectionName: TextView
        var tableName: TextView
        var checked: ImageView

        init {
            tableSectionName = view.findViewById<View>(R.id.cell_table_section) as TextView
            tableName = view.findViewById<View>(R.id.cell_table) as TextView
            checked = view.findViewById<View>(R.id.checked) as ImageView
        }
    }

    companion object {
        const val TABLE_SECTION_STRING = "table_section_string"
        private const val TYPE_TABLE_SECTION = 0
        private const val TYPE_TABLE_CELL = 1
    }
}
