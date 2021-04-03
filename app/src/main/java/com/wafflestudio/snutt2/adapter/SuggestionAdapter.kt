package com.wafflestudio.snutt2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTUtils
import com.wafflestudio.snutt2.model.Tag
import com.wafflestudio.snutt2.model.TagType
import java.util.*

/**
 * Created by makesource on 2017. 4. 9..
 */
class SuggestionAdapter(private val tagList: List<Tag>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val filteredList: MutableList<Tag>
    private var filterType: TagType?
    private var query = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    fun getItem(position: Int): Tag {
        return filteredList[position]
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as SuggestionViewHolder).bindData(getItem(position))
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun filter(query: String) {
        Log.d(TAG, "query : $query")
        this.query = query
        val constraintString = query.toLowerCase(Locale.ROOT)
        filteredList.clear()
        if (filterType == null) {
            for (tag in tagList) {
                if (tag.name.toLowerCase(Locale.ROOT).startsWith(constraintString)) {
                    filteredList.add(tag)
                }
            }
        } else {
            for (tag in tagList) {
                val type = tag.tagType ?: continue
                if (type === TagType.ACADEMIC_YEAR && filterType !== TagType.ACADEMIC_YEAR) continue
                if (type === TagType.CATEGORY && filterType !== TagType.CATEGORY) continue
                if (type === TagType.CLASSIFICATION && filterType !== TagType.CLASSIFICATION) continue
                if (type === TagType.CREDIT && filterType !== TagType.CREDIT) continue
                if (type === TagType.DEPARTMENT && filterType !== TagType.DEPARTMENT) continue
                if (type === TagType.INSTRUCTOR && filterType !== TagType.INSTRUCTOR) continue
                if (tag.name.toLowerCase(Locale.ROOT).startsWith(constraintString)) {
                    filteredList.add(tag)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun toggleButton(filterType: TagType?) {
        if (this.filterType === filterType) return
        this.filterType = filterType
        filter(query)
    }

    fun resetState() {
        query = ""
        filterType = null
    }

    protected class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val suggestionTag: TextView
        private val suggestion: TextView
        private var tag: Tag? = null
        fun bindData(tag: Tag) {
            this.tag = tag
            suggestionTag.setTextColor(SNUTTUtils.getTagColor(tag.tagType))
            suggestion.text = tag.name
        }

        override fun onClick(v: View) {
            if (clickListener != null) {
                clickListener!!.onClick(v, tag)
            }
        }

        init {
            suggestionTag = itemView.findViewById<View>(R.id.suggestion_tag) as TextView
            suggestion = itemView.findViewById<View>(R.id.suggestion_text) as TextView
            itemView.setOnClickListener(this)
        }
    }

    interface ClickListener {
        fun onClick(v: View?, tag: Tag?)
    }

    fun setClickListener(_clickListener: ClickListener) {
        clickListener = _clickListener
    }

    companion object {
        private const val TAG = "SUGGESTION_ADAPTER"
        private var clickListener: ClickListener? = null
    }

    init {
        filteredList = ArrayList()
        filterType = null
        query = ""
    }
}
