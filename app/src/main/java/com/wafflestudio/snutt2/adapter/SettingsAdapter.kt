package com.wafflestudio.snutt2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.model.SettingsItem

/**
 * Created by makesource on 2016. 11. 21..
 */
class SettingsAdapter(private val lists: List<SettingsItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == SettingsItem.ViewType.Header.value) {
            val view = inflater.inflate(R.layout.cell_setting_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.cell_settings, parent, false)
            TitleViewHolder(view) // view holder for header items
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemType = getItemViewType(position)
        if (itemType == SettingsItem.ViewType.ItemTitle.value) {
            (holder as TitleViewHolder).bindData(getItem(position))
        }
    }

    override fun getItemCount(): Int {
        return lists.size
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return item.viewType.value
    }

    fun getItem(position: Int): SettingsItem {
        return lists[position]
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var layout: LinearLayout

        init {
            layout = view.findViewById<View>(R.id.cell_header) as LinearLayout
            // layout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5));
        }
    }

    class TitleViewHolder constructor(private val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val title: TextView
        private val detail: TextView
        private val arrow: ImageView
        fun bindData(item: SettingsItem) {
            title.text = item.title
            detail.text = item.detail
            when (item.type) {
                SettingsItem.Type.AddIdPassword, SettingsItem.Type.ChangePassword, SettingsItem.Type.LinkFacebook, SettingsItem.Type.DeleteFacebook, SettingsItem.Type.ChangeEmail, SettingsItem.Type.Leave -> arrow.visibility = View.VISIBLE
                else -> arrow.visibility = View.GONE
            }
        }

        override fun onClick(v: View) {
            if (clickListener != null) {
                clickListener!!.onClick(v, position)
            }
        }

        init {
            title = view.findViewById<View>(R.id.settings_text) as TextView
            detail = view.findViewById<View>(R.id.settings_detail) as TextView
            arrow = view.findViewById<View>(R.id.settings_arrow) as ImageView
            view.setOnClickListener(this)
        }
    }

    interface ClickListener {
        fun onClick(v: View?, position: Int)
    }

    fun setOnItemClickListener(_clickListener: ClickListener) {
        clickListener = _clickListener
    }

    companion object {
        private var clickListener: ClickListener? = null
    }
}
