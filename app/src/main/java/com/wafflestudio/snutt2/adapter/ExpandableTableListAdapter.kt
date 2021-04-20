package com.wafflestudio.snutt2.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.manager.PrefStorage
import com.wafflestudio.snutt2.network.dto.core.TableDto
import com.wafflestudio.snutt2.ui.TableListActivity
import java.util.*

/**
 * Created by makesource on 2016. 1. 24..
 */
class ExpandableTableListAdapter(
        c: TableListActivity?,
        private val groupList: ArrayList<String>? = null,
        private val childList: ArrayList<ArrayList<TableDto>>? = null,
        private val prefStorage: PrefStorage
) : BaseExpandableListAdapter() {
    private var viewHolder: ViewHolder? = null

    // 그룹 포지션을 반환한다.
    override fun getGroup(groupPosition: Int): String {
        return groupList!![groupPosition]
    }

    // 그룹 사이즈를 반환한다.
    override fun getGroupCount(): Int {
        return groupList!!.size
    }

    // 그룹 ID를 반환한다.
    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    // 그룹뷰 각각의 ROW
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val inflater = LayoutInflater.from(parent.context)
        var v = convertView
        if (v == null) {
            viewHolder = ViewHolder()
            v = inflater.inflate(R.layout.cell_table_section, parent, false)
            viewHolder!!.tableSectionName = v.findViewById<View>(R.id.cell_table_section) as TextView
            v.tag = viewHolder
        } else {
            viewHolder = v.tag as ViewHolder
        }

        // 그룹을 펼칠때와 닫을때 아이콘을 변경해 준다.
        /*if(isExpanded){
            viewHolder.iv_image.setBackgroundColor(Color.GREEN);
        }else{
            viewHolder.iv_image.setBackgroundColor(Color.WHITE);
        }*/viewHolder!!.tableSectionName!!.text = getGroup(
            groupPosition
        )
        return v!!
    }

    // 차일드뷰를 반환한다.
    override fun getChild(groupPosition: Int, childPosition: Int): String {
        return if (childList!![groupPosition].isEmpty()) "+ 새로운 시간표 추가" else childList[groupPosition][childPosition].title!!
    }

    // 차일드뷰 사이즈를 반환한다.
    override fun getChildrenCount(groupPosition: Int): Int {
        return if (childList!![groupPosition].isEmpty()) 1 else childList[groupPosition].size
    }

    // 차일드뷰 ID를 반환한다.
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    // 차일드뷰 각각의 ROW
    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val inflater = LayoutInflater.from(parent.context)
        var v = convertView
        if (v == null) {
            viewHolder = ViewHolder()
            v = inflater.inflate(R.layout.cell_table, null)
            viewHolder!!.tableName = v.findViewById<View>(R.id.cell_table) as TextView
            viewHolder!!.checked = v.findViewById<View>(R.id.checked) as ImageView
            v.tag = viewHolder
        } else {
            viewHolder = v.tag as ViewHolder
        }
        viewHolder!!.tableName!!.text = getChild(groupPosition, childPosition)
        if (childList!![groupPosition].isEmpty()) {
            viewHolder!!.tableName!!.setTextColor(Color.GRAY)
            viewHolder!!.checked!!.visibility = View.INVISIBLE
        } else {
            viewHolder!!.tableName!!.setTextColor(Color.BLACK)
            val id = childList[groupPosition][childPosition].id
            if (prefStorage.lastViewTableId.equals(id)) {
                viewHolder!!.checked!!.visibility = View.VISIBLE
            } else {
                viewHolder!!.checked!!.visibility = View.INVISIBLE
            }
        }
        return v!!
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    internal inner class ViewHolder {
        var tableName: TextView? = null
        var tableSectionName: TextView? = null
        var checked: ImageView? = null
    }
}
