package com.wafflestudio.snutt2.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.manager.TagManager
import com.wafflestudio.snutt2.model.Tag

/**
 * Created by makesource on 2016. 2. 21..
 */
class TagListAdapter(private val context: Context, private val tags: List<Tag>, private val tagManager: TagManager) : RecyclerView.Adapter<TagListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cellLayoutView: View
        val viewHolder: ViewHolder
        cellLayoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.cell_tag,
            parent,
            false
        )
        viewHolder = ViewHolder(cellLayoutView)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tag = tags[position]
        val bgShape = holder.itemView.background as GradientDrawable
        bgShape.setColor(SNUTTUtils.getTagColor(tag.tagType))
        holder.tagTitle.text = tag.name
        holder.setClickListener(
            object : ViewHolder.ClickListener {
                override fun onClick(v: View?, position: Int) {
                    tagManager.removeTag(position)
                    notifyItemRemoved(position)
                }
            }
        )
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    // inner class to hold a reference to each item of RecyclerView
    class ViewHolder(var tagLayout: View) : RecyclerView.ViewHolder(tagLayout), View.OnClickListener {
        var tagTitle: TextView
        private var clickListener: ClickListener? = null

        interface ClickListener {
            /**
             * Called when the view is clicked.
             *
             * @param v view that is clicked
             * @param position of the clicked item
             */
            fun onClick(v: View?, position: Int)
        }

        fun setClickListener(clickListener: ClickListener?) {
            this.clickListener = clickListener
        }

        override fun onClick(v: View) {
            if (clickListener != null) {
                clickListener!!.onClick(v, position)
            }
        }

        init {
            tagTitle = tagLayout.findViewById<View>(R.id.tag_title) as TextView
            tagLayout.setOnClickListener(this)
        }
    }

    companion object {
        private const val TAG = "TAG_LIST_ADAPTER"
    }
}
